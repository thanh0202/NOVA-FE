package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Chọn danh mục cha, xem nhóm con, thương hiệu, phân khúc giá và ô sản phẩm nổi bật.
 * Bố cục liên quan: item_category_product.xml, item_category_sidebar.xml, screen_categories.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.BuildConfig;
import com.example.nova.CategoryUi;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/** Danh mục Figma dạng hai cột, lấy danh mục con, hãng và sản phẩm từ API. */
public final class CategoryScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public CategoryScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_categories; }

  private LinearLayout sidebar, details;
  private final List<JSONObject> categories = new ArrayList<>();
  private String selected = "";
  private int revision;

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    LinearLayout root = screen(layoutId());
    root.findViewById(R.id.categories_search).setOnClickListener(v -> go("search"));
    sidebar = root.findViewById(R.id.categories_sidebar);
    details = root.findViewById(R.id.categories_detail);
    get("/categories", data -> {
      JSONArray rows = arr(data);
      for (int i = 0; i < rows.length(); i++) {
        JSONObject item = rows.optJSONObject(i);
        if (item != null) categories.add(item);
      }
      List<String> designOrder = List.of("phone", "tablet", "laptop", "audio", "watch", "accessory");
      categories.sort(java.util.Comparator.comparingInt(item -> {
        int index = designOrder.indexOf(item.optString("code"));
        return index < 0 ? designOrder.size() : index;
      }));
      JSONObject first = null;
      for (JSONObject item : categories) {
        if (first == null && item.isNull("parentId")) first = item;
        if ("phone".equals(item.optString("code"))) { first = item; break; }
      }
      if (first != null) select(first);
      else u.add(details, u.text("Chưa có danh mục.", 13, Ui.MUTED, false));
    });
  }

  // Áp dụng lựa chọn và cập nhật các vùng giao diện phụ thuộc lựa chọn đó.
  private void select(JSONObject category) {
    selected = category.optString("slug", category.optString("code"));
    String categorySlug = selected;
    int request = ++revision;
    sidebar.removeAllViews();
    for (JSONObject item : categories) {
      if (!item.isNull("parentId")) continue;
      LinearLayout tile = inflateLayout(R.layout.item_category_sidebar, sidebar);
      boolean active = item.optString("slug", item.optString("code")).equals(selected);
      CategoryUi.bind(tile, item, images, active);
      tile.setOnClickListener(v -> select(item));
      sidebar.addView(tile);
    }
    details.removeAllViews();
    LinearLayout title = u.card(), heading = u.row();
    u.weight(heading, u.text(category.optString("name"), 15, Ui.INK, true));
    heading.addView(u.link("Xem tất cả ›", () -> products(categorySlug, "", "", "")));
    u.add(title, heading);
    u.add(details, title);
    u.space(details, 10);

    List<JSONObject> children = new ArrayList<>();
    for (JSONObject item : categories) {
      if (item.optLong("parentId", -1) == category.optLong("id")) children.add(item);
    }
    if (!children.isEmpty()) {
      LinearLayout subcategories = panel("Danh mục con", "");
      addChoices(subcategories, labels(children),
          i -> products(children.get(i).optString("slug", children.get(i).optString("code")), "", "", ""));
    }

    LinearLayout brands = panel("Thương hiệu", "Khám phá theo thương hiệu");
    LinearLayout prices = panel("Phân khúc giá", "");
    addChoices(prices, List.of("Dưới 2 triệu", "2 – 4 triệu", "4 – 7 triệu",
        "7 – 13 triệu", "13 – 20 triệu", "Trên 20 triệu"),
        i -> {
          String[] thresholds = {"", "2000000", "4000000", "7000000", "13000000", "20000000", ""};
          String upper = thresholds[i + 1].isEmpty() ? "" : String.valueOf(Long.parseLong(thresholds[i + 1]) - 1);
          products(categorySlug, "", thresholds[i], upper);
        });
    LinearLayout highlights = panel("Sản phẩm nổi bật", "");
    String categoryQuery = "category=" + ApiClient.encode(categorySlug);
    String productPath = "/products?" + categoryQuery
        + (BuildConfig.NOVA_MIDTERM_API ? "&size=12&sort=featured" : "&sort=bestselling");
    get(productPath, data -> {
      if (request != revision) return;
      JSONArray rows = arr(data);
      List<JSONObject> products = new ArrayList<>();
      LinkedHashSet<String> brandNames = new LinkedHashSet<>();
      for (int i = 0; i < rows.length(); i++) {
        JSONObject item = rows.optJSONObject(i);
        if (item == null) continue;
        if (products.size() < 6) products.add(item);
        if (!item.optString("brand").isBlank()) brandNames.add(item.optString("brand"));
      }
      addFeaturedProducts(highlights, products);
      if (!BuildConfig.NOVA_MIDTERM_API) showBrands(brands, new ArrayList<>(brandNames), categorySlug);
    });
    if (BuildConfig.NOVA_MIDTERM_API) get("/brands?" + categoryQuery, data -> {
      if (request != revision) return;
      JSONArray rows = arr(data);
      List<String> names = new ArrayList<>();
      for (int i = 0; i < rows.length(); i++) {
        JSONObject item = rows.optJSONObject(i);
        if (item != null && !item.optString("name").isBlank()) names.add(item.optString("name"));
      }
      showBrands(brands, names, categorySlug);
    });
  }

  // Dựng các thương hiệu phù hợp với danh mục đang chọn.
  private void showBrands(LinearLayout panel, List<String> names, String slug) {
    addChoices(panel, names, i -> products(slug, names.get(i), "", ""));
  }

  // Lấy danh sách nhãn từ dữ liệu JSON để dựng lựa chọn.
  private List<String> labels(List<JSONObject> items) {
    List<String> names = new ArrayList<>();
    for (JSONObject item : items) names.add(item.optString("name"));
    return names;
  }

  // Thêm các ô sản phẩm nổi bật vào panel danh mục.
  private void addFeaturedProducts(LinearLayout panel, List<JSONObject> products) {
    if (products.isEmpty()) {
      u.add(panel, u.text("Đang cập nhật sản phẩm", 12, Ui.MUTED, false));
      return;
    }
    for (int i = 0; i < products.size(); i += 2) {
      LinearLayout row = u.row();
      row.setGravity(Gravity.TOP);
      row.setBaselineAligned(false);
      for (int j = i; j < Math.min(i + 2, products.size()); j++) {
        JSONObject product = products.get(j);
        long id = product.optLong("id");
        String name = shortProductName(product.optString("name"));
        LinearLayout tile = inflateLayout(R.layout.item_category_product, row);
        ((TextView) tile.findViewById(R.id.category_product_name)).setText(name);
        ImageView photo = tile.findViewById(R.id.category_product_image);
        images.product(photo, product.optString("imageUrl"), id);
        tile.setTag("category-featured-product-" + id);
        tile.setContentDescription(name);
        tile.setOnClickListener(v -> go("product", id));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        if (j == i) params.rightMargin = u.d(8);
        row.addView(tile, params);
      }
      if (i + 1 >= products.size()) row.addView(new View(this),
          new LinearLayout.LayoutParams(0, 1, 1));
      u.add(panel, row);
      if (i + 2 < products.size()) u.space(panel, 8);
    }
  }

  /** Chỉ rút gọn nhãn trong danh mục; tên và cấu hình đầy đủ vẫn ở trang sản phẩm. */
  // Rút gọn tên sản phẩm cho vùng ô nhỏ mà vẫn giữ phần nhận diện.
  private String shortProductName(String name) {
    String result = name.trim().replaceFirst(
        "(?iu)\\s+(?:RAM\\s*)?\\d+(?:[.,]\\d+)?\\s*(?:GB|TB)\\b.*$", "");
    result = result.replaceFirst("(?i)\\s+Wi[- ]?Fi$", "").trim();
    return result.isEmpty() ? name.trim() : result;
  }

  // Tạo panel nội dung với nền và khoảng cách của màn Danh mục.
  private LinearLayout panel(String title, String subtitle) {
    LinearLayout card = u.card();
    u.add(card, u.text(title, 14, Ui.INK, true));
    if (!subtitle.isEmpty()) {
      u.space(card, 6);
      u.add(card, u.text(subtitle, 11, Ui.MUTED, false));
    }
    u.space(card, 9);
    u.add(details, card);
    u.space(details, 10);
    return card;
  }

  // Dựng các chip lựa chọn và chuyển lựa chọn thành tham số khi mở danh sách sản phẩm.
  private void addChoices(LinearLayout card, List<String> labels, java.util.function.IntConsumer click) {
    if (labels.isEmpty()) {
      u.add(card, u.text("Đang cập nhật", 11, Ui.MUTED, false));
      return;
    }
    for (int i = 0; i < labels.size(); i += 2) {
      LinearLayout row = u.row();
      for (int j = i; j < i + 2; j++) {
        int option = j;
        TextView text = u.text(j < labels.size() ? labels.get(j) : "", 12, Ui.BRAND, false);
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setPadding(u.d(8), u.d(7), u.d(8), u.d(7));
        text.setMinHeight(u.d(44));
        if (j < labels.size()) {
          text.setBackground(u.bg(Ui.BG, 10));
          text.setOnClickListener(v -> click.accept(option));
        } else text.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        if (j == i) params.rightMargin = u.d(6);
        row.addView(text, params);
      }
      u.add(card, row);
      u.space(card, 6);
    }
  }

  // Ghi danh mục/thương hiệu/khoảng giá vào AppState rồi mở trang danh sách sản phẩm.
  private void products(String category, String brand, String min, String max) {
    s.category = category;
    s.brand = brand;
    s.query = "";
    s.minPrice = min;
    s.maxPrice = max;
    s.inStock = false;
    go("products");
  }
}

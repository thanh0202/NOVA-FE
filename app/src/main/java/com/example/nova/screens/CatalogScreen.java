package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp lưới sản phẩm theo danh mục, hỗ trợ chuyển nhóm, từ khóa, sắp xếp và lọc.
 * Bố cục liên quan: screen_catalog.xml, screen_search_results.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.app.AlertDialog;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.BuildConfig;
import com.example.nova.Ui;
import com.example.nova.MainActivity;
import com.example.nova.R;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/** Trang sản phẩm giữ ngữ cảnh danh mục và tải dữ liệu từ API hiện có. */
public class CatalogScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public CatalogScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_catalog; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    LinearLayout layout = screen(layoutId());
    boolean searchResults = layoutId() == R.layout.screen_search_results;
    TextView search = layout.findViewById(R.id.catalog_search);
    search.setOnClickListener(v -> go("search"));
    if (searchResults && !s.query.isBlank()) {
      search.setText(s.query);
      ((TextView) layout.findViewById(R.id.search_results_title)).setText("Kết quả cho “" + s.query + "”");
    }
    if (!searchResults) get("/categories", data -> showCategories(layout, arr(data)));

    TextView sort = layout.findViewById(R.id.catalog_sort);
    String[] labels = {"Bán chạy", "Giá tăng dần", "Giá giảm dần", "Mới nhất"};
    String[] values = {"bestselling", "price_asc", "price_desc", "newest"};
    int selected = 0;
    for (int i = 0; i < values.length; i++) {
      if (values[i].equals(s.sort)) selected = i;
    }
    sort.setText(labels[selected] + "  ⌄");
    sort.setOnClickListener(v -> new AlertDialog.Builder(this)
        .setTitle("Sắp xếp sản phẩm").setItems(labels, (dialog, index) -> {
          s.sort = values[index];
          activity.render();
        }).show());
    TextView filter = layout.findViewById(R.id.catalog_filter);
    boolean filtered = !s.minPrice.isEmpty() || !s.maxPrice.isEmpty() || !s.brand.isEmpty() || s.inStock;
    filter.setText(filtered ? "☷  Bộ lọc •" : "☷  Bộ lọc");
    filter.setOnClickListener(v -> go("product-filters"));

    LinearLayout grid = layout.findViewById(R.id.catalog_grid);
    String path = "/products?q=" + ApiClient.encode(s.query)
        + "&category=" + ApiClient.encode(s.category)
        + "&sort=" + s.sort + "&minPrice=" + s.minPrice + "&maxPrice=" + s.maxPrice
        + "&brand=" + ApiClient.encode(s.brand);
    path += "&size=60&inStock=" + s.inStock;
    get(path, data -> {
      JSONArray products = arr(data);
      int total = data instanceof JSONObject page ? page.optInt("total", products.length()) : products.length();
      String summary = total + " sản phẩm";
      if (!s.query.isBlank()) summary += " cho “" + s.query + "”";
      ((TextView) layout.findViewById(R.id.catalog_result_summary)).setText(summary);
      productGrid(grid, products, 1000, true);
    });
  }

  // Đọc cây danh mục, đặt tiêu đề/ngữ cảnh hiện tại và dựng lựa chọn chuyển nhóm.
  private void showCategories(LinearLayout layout, JSONArray data) {
    List<JSONObject> categories = new ArrayList<>();
    JSONObject selected = null;
    for (int i = 0; i < data.length(); i++) {
      JSONObject item = data.optJSONObject(i);
      if (item == null) continue;
      categories.add(item);
      if (slug(item).equals(s.category)) selected = item;
    }

    // Danh mục lá giữ các nhánh cùng cha để người dùng chuyển loại sản phẩm.
    JSONObject branch = selected;
    if (selected != null && !hasChildren(categories, selected.optLong("id"))) {
      JSONObject parent = findById(categories, selected.optLong("parentId", -1));
      if (parent != null) branch = parent;
    }
    JSONObject parent = branch == null ? null : findById(categories, branch.optLong("parentId", -1));
    TextView back = layout.findViewById(R.id.catalog_parent);
    String parentSlug = parent == null ? "" : slug(parent);
    back.setText(parent == null ? "‹  Danh mục" : "‹  " + parent.optString("name"));
    back.setOnClickListener(v -> {
      if (parent == null) tab("categories");
      else chooseCategory(parentSlug);
    });
    String title = selected == null ? "Tất cả sản phẩm" : selected.optString("name");
    ((TextView) layout.findViewById(R.id.catalog_context_title)).setText(title);
    activity.setScreenTitle(selected == null ? "Sản phẩm theo danh mục" : title);
    TextView subtitle = layout.findViewById(R.id.catalog_subtitle);
    if (subtitle != null) subtitle.setText("Khám phá " + title.toLowerCase(java.util.Locale.forLanguageTag("vi-VN")) + " phù hợp với bạn.");

    LinearLayout choices = layout.findViewById(R.id.catalog_categories);
    choices.removeAllViews();
    long branchId = branch == null ? -1 : branch.optLong("id");
    String allSlug = branch == null ? "" : slug(branch);
    addCategory(choices, allSlug, "Tất cả");
    int children = 0;
    for (JSONObject item : categories) {
      boolean belongs = branch == null ? item.isNull("parentId") : item.optLong("parentId", -1) == branchId;
      if (belongs) {
        addCategory(choices, slug(item), item.optString("name"));
        children++;
      }
    }
    HorizontalScrollView scroll = layout.findViewById(R.id.catalog_category_scroll);
    scroll.setVisibility(children == 0 ? View.GONE : View.VISIBLE);
    scroll.post(() -> {
      View active = choices.findViewWithTag("catalog-category-" + s.category);
      if (active != null) scroll.smoothScrollTo(Math.max(0, active.getLeft() - u.d(8)), 0);
    });
  }

  // Tìm đối tượng danh mục có ID cần tìm trong danh sách đã tải.
  private JSONObject findById(List<JSONObject> categories, long id) {
    for (JSONObject item : categories) {
      if (item.optLong("id", -2) == id) return item;
    }
    return null;
  }

  // Kiểm tra danh mục có nhóm con để xác định nhánh điều hướng.
  private boolean hasChildren(List<JSONObject> categories, long id) {
    for (JSONObject item : categories) {
      if (item.optLong("parentId", -1) == id) return true;
    }
    return false;
  }

  // Lấy slug điều hướng danh mục, dùng code khi dữ liệu chưa có slug.
  private String slug(JSONObject category) {
    return category.optString("slug", category.optString("code"));
  }

  // Tạo lựa chọn danh mục với trạng thái đang chọn và callback.
  private void addCategory(LinearLayout choices, String value, String label) {
    TextView tile = u.chip(label, s.category.equals(value), () -> chooseCategory(value));
    tile.setMinHeight(u.d(40));
    tile.setContentDescription("Danh mục " + label);
    tile.setTag("catalog-category-" + value);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, u.d(40));
    if (choices.getChildCount() > 0) params.leftMargin = u.d(8);
    choices.addView(tile, params);
  }

  // Ghi danh mục vào bộ lọc, bỏ thương hiệu cũ và dựng lại kết quả.
  private void chooseCategory(String value) {
    s.category = value;
    s.brand = "";
    activity.render();
  }
}

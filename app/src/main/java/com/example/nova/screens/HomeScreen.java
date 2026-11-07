package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp banner, danh mục hai hàng, hàng mới về, bán chạy và nổi bật. Hiện Flash Sale khi có chương trình hoạt động từ API tương ứng.
 * Bố cục liên quan: item_home_category.xml, screen_home.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.BuildConfig;
import com.example.nova.CategoryUi;
import com.example.nova.HomeBanner;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.SaleReminderReceiver;
import com.example.nova.Ui;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/** Customer screen: home; its layout and behavior are edited together. */
public final class HomeScreen extends BaseScreen {
  private HomeBanner banner;
  private HomeCategoryBrowser categoryBrowser;
  private LinearLayout flash, discovery;
  private JSONArray homeProducts;
  private boolean resumed;
  private int flashRequest;
  private final Handler flashClock = new Handler(Looper.getMainLooper());
  private final Runnable refreshFlash = this::loadFlashSales;

  // Tiếp tục công việc cần chạy khi màn hình trở lại foreground.
  @Override public void onResume() {
    resumed = true;
    if (banner != null) banner.resume();
    if (homeProducts != null) loadFlashSales();
  }

  // Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời.
  @Override public void onPause() {
    resumed = false;
    flashRequest++;
    flashClock.removeCallbacks(refreshFlash);
    if (banner != null) banner.pause();
  }

  // Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn.
  @Override public void onDispose() {
    if (categoryBrowser != null) categoryBrowser.onDispose();
    super.onDispose();
  }

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public HomeScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_home; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    final LinearLayout layout = screen(R.layout.screen_home);
    layout.findViewById(R.id.home_favorites).setOnClickListener(v -> go("favorites"));
    layout.findViewById(R.id.home_notifications).setOnClickListener(v -> go("notifications"));
    layout.findViewById(R.id.home_search).setOnClickListener(v -> go("search"));
    banner = new HomeBanner(activity, layout);
    get("/banners", data -> banner.setBanners(arr(data)));
    loadNewArrivals(layout);
    categoryBrowser = new HomeCategoryBrowser(activity, layout.findViewById(R.id.home_category_products));
    categoryBrowser.render();
    layout.findViewById(R.id.home_category_all).setOnClickListener(v -> go("categories"));
    loadHomeCategories(layout);
    flash = layout.findViewById(R.id.home_flash);
    discovery = layout.findViewById(R.id.home_discovery);
    if (BuildConfig.NOVA_MIDTERM_API) {
      get("/home?limit=6", data -> {
        JSONObject home = obj(data);
        showDiscoverTabs(discovery, arr(home.opt("bestsellers")), arr(home.opt("featured")));
      });
      return;
    }
    get("/products?sort=bestselling", data -> {
      homeProducts = arr(data);
      renderFullDiscovery(homeProducts, Collections.emptySet());
      if (resumed) loadFlashSales();
    });
    if (loggedIn())
      api.get(
          "/cart",
          (data, error, status) -> {
            if (error == null)
              s.cartCount =
                  obj(data).optJSONArray("items") == null
                      ? 0
                      : obj(data).optJSONArray("items").length();
            updateCartBadge();
          });
  }

  // Tải các khung Flash Sale và làm mới theo mốc thời gian của dữ liệu.
  private void loadFlashSales() {
    flashClock.removeCallbacks(refreshFlash);
    int request = ++flashRequest;
    api.get("/flash-sales", (data, error, status) -> {
      if (!resumed || request != flashRequest || isFinishing() || isDestroyed()) return;
      JSONArray sales = error == null ? arr(data) : new JSONArray();
      long now = System.currentTimeMillis();
      JSONObject first = sales.optJSONObject(0);
      if (first != null) {
        long serverNow = SaleReminderReceiver.instant(first.optString("serverNow"));
        if (serverNow > 0) now = serverNow;
      }
      JSONArray active = activeFlashItems(sales, now);
      flash.removeAllViews();
      flash.setVisibility(active.length() == 0 ? View.GONE : View.VISIBLE);
      Set<Long> featuredSale = active.length() == 0
          ? Collections.emptySet() : renderFlashCards(flash, homeProducts, active);
      renderFullDiscovery(homeProducts, featuredSale);
      flashClock.postDelayed(refreshFlash, nextFlashRefresh(sales, now));
    });
  }

  // Lọc các mục Flash Sale còn hiệu lực tại thời điểm hiện tại.
  private static JSONArray activeFlashItems(JSONArray sales, long now) {
    for (int i = 0; i < sales.length(); i++) {
      JSONObject sale = sales.optJSONObject(i);
      if (sale == null || !sale.optBoolean("active", true)) continue;
      long start = SaleReminderReceiver.instant(sale.optString("startsAt"));
      long end = SaleReminderReceiver.instant(sale.optString("endsAt"));
      JSONArray items = sale.optJSONArray("items");
      if (start <= 0 || start > now || end <= now || items == null) continue;
      JSONArray available = new JSONArray();
      for (int j = 0; j < items.length(); j++) {
        JSONObject item = items.optJSONObject(j);
        if (item != null && item.optLong("productId") > 0 && item.optLong("remaining") > 0) {
          available.put(item);
        }
      }
      if (available.length() > 0) return available;
    }
    return new JSONArray();
  }

  // Tính thời điểm cần tải lại khi một chương trình bắt đầu hoặc kết thúc.
  private static long nextFlashRefresh(JSONArray sales, long now) {
    long delay = 60_000L;
    for (int i = 0; i < sales.length(); i++) {
      JSONObject sale = sales.optJSONObject(i);
      if (sale == null || !sale.optBoolean("active", true)) continue;
      long start = SaleReminderReceiver.instant(sale.optString("startsAt"));
      long end = SaleReminderReceiver.instant(sale.optString("endsAt"));
      if (start > now) delay = Math.min(delay, start - now);
      if (end > now) delay = Math.min(delay, end - now);
    }
    return Math.max(1000L, delay);
  }

  // Dựng hai tab Bán chạy/Nổi bật và cập nhật danh sách khi người dùng chuyển tab.
  private void showDiscoverTabs(LinearLayout parent, JSONArray bestsellers, JSONArray featured) {
    parent.removeAllViews();
    u.add(parent, u.text("Khám phá thêm", 19, Ui.INK, true));
    u.space(parent, 10);
    LinearLayout tabs = u.row(), grid = u.col();
    TextView popular = u.button("Bán chạy", false, () -> {});
    TextView highlights = u.button("Nổi bật", false, () -> {});
    popular.setTag("home-tab-bestsellers");
    highlights.setTag("home-tab-featured");
    grid.setTag("home-discovery-products");
    LinearLayout.LayoutParams first = new LinearLayout.LayoutParams(0, -2, 1);
    first.rightMargin = u.d(8);
    tabs.addView(popular, first);
    tabs.addView(highlights, new LinearLayout.LayoutParams(0, -2, 1));
    u.add(parent, tabs);
    u.space(parent, 12);
    u.add(parent, grid);
    Runnable showPopular = () -> {
      s.homeFeatured = false;
      popular.setSelected(true);
      highlights.setSelected(false);
      popular.setBackground(u.bg(Ui.TINT, 14));
      highlights.setBackground(u.bg(Color.WHITE, 14));
      productGrid(grid, bestsellers, 6, true);
    };
    popular.setOnClickListener(v -> showPopular.run());
    Runnable showFeatured = () -> {
      s.homeFeatured = true;
      highlights.setSelected(true);
      popular.setSelected(false);
      highlights.setBackground(u.bg(Ui.TINT, 14));
      popular.setBackground(u.bg(Color.WHITE, 14));
      productGrid(grid, featured, 6, true);
    };
    highlights.setOnClickListener(v -> showFeatured.run());
    if (s.homeFeatured) showFeatured.run();
    else showPopular.run();
  }

  // Tải nhóm hàng mới về và dựng các thẻ sản phẩm tương ứng.
  private void loadNewArrivals(LinearLayout layout) {
    LinearLayout block = layout.findViewById(R.id.home_new_arrivals);
    int generation = s.generation;
    String path = BuildConfig.NOVA_MIDTERM_API ? "/home/new-arrivals" : "/new-arrivals";
    api.get(path, (data, error, status) -> {
      if (generation != s.generation || isDestroyed() || isFinishing()) return;
      block.removeAllViews();
      u.add(block, u.text("Hàng mới về", 20, Ui.INK, true));
      u.space(block, 12);
      if (error != null) {
        u.add(block, u.link("Chạm để tải lại hàng mới về", () -> loadNewArrivals(layout)));
      } else if (arr(data).length() == 0) {
        u.add(block, u.text("Chưa có phiếu nhập gần đây.", 13, Ui.MUTED, false));
      } else {
        LinearLayout grid = u.col();
        u.add(block, grid);
        productGrid(grid, arr(data), 6);
      }
    });
  }

  // Ghi ngữ cảnh danh mục, bỏ bộ lọc không còn phù hợp rồi mở danh sách sản phẩm.
  private void openCategory(String slug) {
    s.category = slug;
    s.query = "";
    s.brand = "";
    s.inStock = false;
    s.minPrice = "";
    s.maxPrice = "";
    go("products");
  }

  /** Read every active category, including categories added later in the admin. */
  // Lấy danh mục từ API và chuyển cùng dữ liệu cho dải khám phá/khối sản phẩm theo danh mục.
  private void loadHomeCategories(LinearLayout layout) {
    TextView state = layout.findViewById(R.id.home_category_state);
    state.setVisibility(View.VISIBLE);
    state.setText("Đang tải danh mục…");
    state.setOnClickListener(null);
    final int generation = s.generation;
    api.get("/categories", (data, error, status) -> {
      if (generation != s.generation || isDestroyed() || isFinishing()) return;
      if (error != null) {
        state.setText("Chưa tải được danh mục. Chạm để thử lại.");
        state.setTextColor(Ui.BRAND);
        state.setOnClickListener(v -> loadHomeCategories(layout));
        categoryBrowser.showCategoriesError(() -> loadHomeCategories(layout));
        return;
      }
      JSONArray source = arr(data);
      List<JSONObject> categories = new ArrayList<>();
      Set<String> seen = new HashSet<>();
      for (int i = 0; i < source.length(); i++) {
        JSONObject category = source.optJSONObject(i);
        if (category != null && !category.optString("slug").isBlank()
            && seen.add(category.optString("slug"))) categories.add(category);
      }
      if (categories.isEmpty()) {
        state.setText("Chưa có danh mục. Bạn có thể khám phá tất cả sản phẩm.");
        categoryBrowser.setCategories(categories);
        return;
      }
      state.setVisibility(View.GONE);
      categoryBrowser.setCategories(categories);
      HorizontalScrollView scroll = layout.findViewById(R.id.home_category_scroll);
      scroll.setVisibility(View.VISIBLE);
      scroll.post(() -> {
        if (generation != s.generation || isDestroyed()) return;
        populateHomeCategories(layout, categories);
      });
    });
  }

  // Dựng dải danh mục hai hàng, gắn ảnh và cho phép vuốt ngang.
  private void populateHomeCategories(LinearLayout layout, List<JSONObject> categories) {
    HorizontalScrollView scroll = layout.findViewById(R.id.home_category_scroll);
    LinearLayout columns = layout.findViewById(R.id.home_category_columns);
    columns.removeAllViews();
    // A two-row strip keeps the existing horizontal gesture on one continuous surface.
    int viewport = scroll.getWidth() > 0 ? scroll.getWidth() : layout.getWidth();
    if (viewport <= 0) viewport = getResources().getDisplayMetrics().widthPixels
        - 2 * getResources().getDimensionPixelSize(R.dimen.nova_page_inset);
    int width = Math.min(u.d(112), Math.max(u.d(88), Math.round(viewport / 3.35f)));
    int columnCount = (categories.size() + 1) / 2;
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
      LinearLayout column = u.col();
      LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(width, -2);
      columns.addView(column, columnParams);
      for (int row = 0; row < 2; row++) {
        int index = columnIndex + row * columnCount;
        if (index >= categories.size()) continue;
        JSONObject category = categories.get(index);
        String slug = category.optString("slug");
        LinearLayout tile = inflateLayout(R.layout.item_home_category, column);
        CategoryUi.bind(tile, category, images, false);
        tile.setTag("home-category-" + slug);
        tile.setOnClickListener(v -> openCategory(slug));
        LinearLayout.LayoutParams tileParams = new LinearLayout.LayoutParams(-1, -2);
        column.addView(tile, tileParams);
      }
    }
  }

  // Dựng các thẻ Flash Sale với giá, lượng còn lại và thao tác tương ứng.
  private Set<Long> renderFlashCards(LinearLayout flash, JSONArray products, JSONArray items) {
    Map<Long, JSONObject> byId = new HashMap<>();
    for (int i = 0; i < products.length(); i++) {
      JSONObject p = products.optJSONObject(i);
      byId.put(p.optLong("id"), p);
    }
    LinearLayout heading = u.row();
    u.weight(heading, u.text("Ưu đãi chớp nhoáng", 18, Color.WHITE, true));
    heading.addView(
        u.text(
            java.time.LocalDate.now(java.time.ZoneId.of("Asia/Bangkok"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            10,
            Color.WHITE,
            false));
    u.add(flash, heading);
    u.space(flash, 14);
    Set<Long> saleIds = new HashSet<>();
    LinearLayout row = u.row();
    row.setGravity(Gravity.TOP);
    for (int i = 0; i < Math.min(2, items.length()); i++) {
      JSONObject item = items.optJSONObject(i);
      long id = item.optLong("productId");
      saleIds.add(id);
      JSONObject product = byId.get(id);
      if (product == null) product = item;
      LinearLayout card = u.col();
      FrameLayout photo = new FrameLayout(this);
      photo.setBackground(u.bg(0xFFEEF2F8, 20));
      ImageView image =
          image(
              json(
                  "id", id, "name", item.optString("name"), "imageUrl", item.optString("imageUrl")),
              128);
      photo.addView(image, new FrameLayout.LayoutParams(-1, u.d(128), Gravity.CENTER));
      long original = item.optLong("originalPrice"), price = item.optLong("price");
      long off = original > 0 ? Math.max(0, (original - price) * 100 / original) : 0;
      TextView badge = u.text("−" + off + "%", 10, Ui.BLUE, true);
      badge.setPadding(u.d(7), u.d(4), u.d(7), u.d(4));
      badge.setBackground(u.outline(Color.WHITE, 8, Ui.BLUE));
      FrameLayout.LayoutParams badgeParams =
          new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.LEFT);
      badgeParams.leftMargin = u.d(10);
      badgeParams.topMargin = u.d(10);
      photo.addView(badge, badgeParams);
      ImageView heart = u.icon("home_imgheart", 16);
      heart.setPadding(u.d(11), u.d(11), u.d(11), u.d(11));
      heart.setBackground(u.bg(0xBBFFFFFF, 100));
      heart.setContentDescription("Yêu thích " + item.optString("name"));
      FrameLayout.LayoutParams heartParams =
          new FrameLayout.LayoutParams(u.d(40), u.d(40), Gravity.RIGHT | Gravity.TOP);
      heartParams.rightMargin = u.d(9);
      heartParams.topMargin = u.d(9);
      photo.addView(heart, heartParams);
      heart.setOnClickListener(
          v -> {
            if (!loggedIn()) {
              s.afterLogin = "home";
              go("login");
              return;
            }
            send("POST", "/favorites/" + id, null, d -> toast("Đã lưu vào yêu thích"));
          });
      card.addView(photo, new LinearLayout.LayoutParams(-1, u.d(150)));
      u.space(card, 10);
      TextView name = u.text(item.optString("name"), 13, Color.WHITE, true);
      name.setMaxLines(2);
      name.setMinLines(2);
      name.setEllipsize(android.text.TextUtils.TruncateAt.END);
      u.add(card, name);
      u.space(card, 5);
      JSONArray variants = product.optJSONArray("variants");
      u.add(
          card,
          u.text(
              variants != null && variants.length() > 0
                  ? variants.optJSONObject(0).optString("name")
                  : "Chính hãng · Bảo hành đầy đủ",
              10,
              Color.WHITE,
              false));
      u.space(card, 5);
      u.add(card, u.text(Ui.money(price), 15, Color.WHITE, true));
      u.space(card, 5);
      LinearLayout last = u.row();
      TextView old = u.text(Ui.money(original), 10, Color.WHITE, false);
      old.setPaintFlags(old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      u.weight(last, old);
      last.addView(
          u.text(
              "★ " + product.optDouble("rating", 4.9) + " (" + product.optInt("reviewCount") + ")",
              10,
              Color.WHITE,
              false));
      u.add(card, last);
      card.setOnClickListener(v -> go("product", id));
      LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, -2, 1);
      if (i == 0) cp.rightMargin = u.d(14);
      row.addView(card, cp);
    }
    u.add(flash, row);
    u.space(flash, 14);
    TextView more = u.button("Xem tất cả Flash Sale  →", false, () -> go("flash"));
    more.setMinHeight(u.d(38));
    more.setGravity(Gravity.CENTER);
    more.setTextColor(Color.WHITE);
    more.setBackground(u.outline(0x1CFFFFFF, 100, 0x33FFFFFF));
    u.add(flash, more);
    return saleIds;
  }

  // Dựng các khối khám phá dùng cho chế độ API đầy đủ.
  private void renderFullDiscovery(JSONArray products, Set<Long> saleIds) {
    JSONArray rest = new JSONArray(), featured = new JSONArray();
    for (int i = 0; i < products.length(); i++) {
      JSONObject product = products.optJSONObject(i);
      if (product == null || saleIds.contains(product.optLong("id"))) continue;
      if (rest.length() < 6) rest.put(product);
      if (featured.length() < 6 && product.optBoolean("featured")) featured.put(product);
      if (rest.length() == 6 && featured.length() == 6) break;
    }
    showDiscoverTabs(discovery, rest, featured);
  }
}

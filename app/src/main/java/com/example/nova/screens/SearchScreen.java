package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Khi ô trống, hiện lịch sử và danh mục. Khi gõ, nạp từ khóa và sản phẩm gợi ý có ảnh, giá. Chạm sản phẩm để mở chi tiết hoặc chuyển sang kết quả đầy đủ.
 * Bố cục liên quan: item_search_product.xml, screen_search.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/** Hiển thị lịch sử khi chưa nhập và truy vấn gợi ý theo nội dung đang gõ. */
public final class SearchScreen extends BaseScreen {
  private static final long SUGGESTION_DELAY_MS = 300;
  private static final int REQUEST_LIMIT = 8;
  private static final int PRODUCT_LIMIT = 5;
  private static final int KEYWORD_LIMIT = 4;

  private final Handler handler = new Handler(Looper.getMainLooper());
  private final List<String> historyKeywords = new ArrayList<>();
  private LinearLayout root;
  private EditText input;
  private Runnable scheduledSearch;
  private int requestVersion;
  private boolean disposed;

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public SearchScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_search; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    root = screen(layoutId());
    input = root.findViewById(R.id.search_query);
    input.setText(s.searchDraft);
    input.setSelection(input.length());
    root.findViewById(R.id.search_back).setOnClickListener(v -> {
      hideKeyboard();
      onBackPressed();
    });
    root.findViewById(R.id.search_clear).setOnClickListener(v -> {
      input.setText("");
      input.requestFocus();
      getSystemService(InputMethodManager.class).showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    });
    root.findViewById(R.id.search_submit).setOnClickListener(v -> showResults(input.getText().toString()));
    root.findViewById(R.id.search_show_all).setOnClickListener(v -> showResults(input.getText().toString()));
    root.findViewById(R.id.search_retry).setOnClickListener(v -> updateQuery(input.getText().toString()));
    input.setOnEditorActionListener((v, action, event) -> {
      if (action != EditorInfo.IME_ACTION_SEARCH) return false;
      showResults(input.getText().toString());
      return true;
    });
    input.addTextChangedListener(new TextWatcher() {
      // Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó.
      @Override public void beforeTextChanged(CharSequence value, int start, int count, int after) {}
      // Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu.
      @Override public void onTextChanged(CharSequence value, int start, int before, int count) {
        updateQuery(value.toString());
      }
      // Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi.
      @Override public void afterTextChanged(Editable value) {}
    });
    loadHistory();
    loadCategories();
    updateQuery(s.searchDraft);
  }

  // Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn.
  @Override public void onDispose() {
    disposed = true;
    cancelScheduledSearch();
  }

  // Chuẩn hóa từ khóa mới, hủy lịch cũ và lên lịch tải gợi ý.
  private void updateQuery(String value) {
    s.searchDraft = value;
    String query = clean(value);
    int version = ++requestVersion;
    cancelScheduledSearch();
    boolean typing = !query.isEmpty();
    visible(R.id.search_clear, !value.isEmpty());
    visible(R.id.search_idle, !typing);
    visible(R.id.search_live, typing);
    visible(R.id.search_keywords, false);
    visible(R.id.search_products, false);
    visible(R.id.search_retry, false);
    visible(R.id.search_show_all, false);
    rows(R.id.search_keyword_rows).removeAllViews();
    rows(R.id.search_product_rows).removeAllViews();
    if (!typing) return;
    status("Đang tìm sản phẩm…");
    scheduledSearch = () -> loadSuggestions(query, version);
    handler.postDelayed(scheduledSearch, SUGGESTION_DELAY_MS);
  }

  // Gỡ callback tìm kiếm đã lên lịch để không gửi từ khóa cũ.
  private void cancelScheduledSearch() {
    if (scheduledSearch != null) {
      handler.removeCallbacks(scheduledSearch);
      scheduledSearch = null;
    }
  }

  // Gọi API gợi ý sau khoảng chờ nhập, chỉ nhận kết quả khớp từ khóa/phiên bản mới nhất.
  private void loadSuggestions(String query, int version) {
    scheduledSearch = null;
    String path = "/products?q=" + ApiClient.encode(query)
        + "&category=" + ApiClient.encode(s.searchCategory)
        + "&sort=bestselling&size=" + REQUEST_LIMIT;
    api.get(path, (data, error, code) -> {
      // Chỉ phản hồi của lần nhập mới nhất được phép cập nhật màn hình.
      if (disposed || version != requestVersion) return;
      visible(R.id.search_show_all, true);
      if (error != null) {
        status("Chưa tải được gợi ý. Bạn có thể thử lại hoặc mở kết quả tìm kiếm.");
        visible(R.id.search_retry, true);
        return;
      }
      List<JSONObject> products = productsFrom(arr(data), query);
      showKeywords(query, products);
      if (products.isEmpty()) {
        status("Chưa tìm thấy sản phẩm phù hợp. Bạn thử tên ngắn hơn hoặc tên thương hiệu.");
        return;
      }
      visible(R.id.search_status, false);
      visible(R.id.search_products, true);
      LinearLayout parent = rows(R.id.search_product_rows);
      for (int i = 0; i < Math.min(PRODUCT_LIMIT, products.size()); i++) {
        if (i > 0) u.add(parent, u.divider(), 1);
        u.add(parent, productRow(parent, products.get(i), query));
      }
    });
  }

  // Đọc danh sách sản phẩm từ dạng phản hồi API được hỗ trợ.
  private List<JSONObject> productsFrom(JSONArray data, String query) {
    List<JSONObject> products = new ArrayList<>();
    for (int i = 0; i < data.length(); i++) {
      JSONObject product = data.optJSONObject(i);
      if (product != null && product.optLong("id") > 0 && !product.optString("name").isBlank()) {
        products.add(product);
      }
    }
    String term = key(query);
    // Java sắp lại nhóm kết quả nhỏ đã nhận để ưu tiên tên bắt đầu bằng từ đang gõ.
    products.sort(Comparator.comparingInt(product -> {
      String name = key(product.optString("name"));
      if (name.equals(term)) return 0;
      if (name.startsWith(term)) return 1;
      if (name.contains(" " + term)) return 2;
      return 3;
    }));
    return products;
  }

  // Dựng các gợi ý từ khóa để chạm và chuyển thành tìm kiếm.
  private void showKeywords(String query, List<JSONObject> products) {
    Map<String, String> candidates = new LinkedHashMap<>();
    String term = key(query);
    for (String recent : historyKeywords) addKeyword(candidates, recent, term);
    for (JSONObject product : products) {
      addKeyword(candidates, product.optString("brand"), term);
      addKeyword(candidates, product.optString("name"), term);
    }
    visible(R.id.search_keywords, !candidates.isEmpty());
    LinearLayout parent = rows(R.id.search_keyword_rows);
    for (String suggestion : candidates.values()) {
      u.add(parent, textRow(suggestion, "ic_nova_search", () -> showResults(suggestion)));
    }
  }

  // Thêm một lựa chọn từ khóa cùng sự kiện mở kết quả.
  private void addKeyword(Map<String, String> candidates, String value, String query) {
    String label = clean(value);
    String match = key(label);
    if (candidates.size() < KEYWORD_LIMIT && !match.equals(query) && match.contains(query)) {
      candidates.putIfAbsent(match, label);
    }
  }

  // Dựng dòng sản phẩm gợi ý gồm ảnh, tên và giá.
  private LinearLayout productRow(LinearLayout parent, JSONObject product, String query) {
    LinearLayout row = inflateLayout(R.layout.item_search_product, parent);
    long id = product.optLong("id");
    row.setTag("search-product-" + id);
    images.product(row.findViewById(R.id.search_item_image), product.optString("imageUrl"), id);
    ((TextView) row.findViewById(R.id.search_item_name)).setText(product.optString("name"));
    long price = product.optLong("price");
    ((TextView) row.findViewById(R.id.search_item_price)).setText(price > 0 ? Ui.money(price) : "Liên hệ");
    long originalPrice = product.optLong("originalPrice");
    TextView original = row.findViewById(R.id.search_item_original_price);
    if (price > 0 && originalPrice > price) {
      original.setText(Ui.money(originalPrice));
      original.setPaintFlags(original.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      original.setVisibility(View.VISIBLE);
    }
    u.action(row, () -> {
      hideKeyboard();
      saveHistory(query);
      go("product", id);
    });
    return row;
  }

  // Lấy lịch sử tìm kiếm của tài khoản đang đăng nhập.
  private void loadHistory() {
    LinearLayout parent = rows(R.id.search_history_rows);
    parent.removeAllViews();
    historyKeywords.clear();
    visible(R.id.search_history_clear, false);
    if (!loggedIn()) {
      u.add(parent, u.link("Đăng nhập để đồng bộ lịch sử →", () -> {
        hideKeyboard();
        s.afterLogin = "search";
        s.afterLoginId = 0;
        go("login");
      }));
      return;
    }
    api.get("/search-history", (data, error, code) -> {
      if (disposed) return;
      if (error != null) {
        u.add(parent, u.link("Chưa tải được lịch sử. Thử lại →", this::loadHistory));
        return;
      }
      JSONArray list = arr(data);
      for (int i = 0; i < list.length() && historyKeywords.size() < 6; i++) {
        JSONObject item = list.optJSONObject(i);
        if (item == null) continue;
        String keyword = clean(item.optString("keyword", item.optString("query")));
        if (keyword.isEmpty()) continue;
        historyKeywords.add(keyword);
        u.add(parent, textRow(keyword, "node_6_2783_imgwallet", () -> showResults(keyword)));
      }
      if (historyKeywords.isEmpty()) {
        u.add(parent, u.text("Chưa có tìm kiếm gần đây.", 12, Ui.MUTED, false));
      } else {
        visible(R.id.search_history_clear, true);
        root.findViewById(R.id.search_history_clear).setOnClickListener(v ->
            confirm("Xóa lịch sử?", "Các tìm kiếm đã lưu của bạn sẽ được xóa.", () ->
                send("DELETE", "/search-history", null, ignored -> loadHistory())));
      }
    });
  }

  // Lấy các lựa chọn danh mục để lọc/phân hướng tìm kiếm.
  private void loadCategories() {
    api.get("/categories", (data, error, code) -> {
      if (disposed || error != null) return;
      JSONArray categories = arr(data);
      LinearLayout parent = rows(R.id.search_category_rows);
      for (int i = 0; i < categories.length(); i++) {
        JSONObject category = categories.optJSONObject(i);
        if (category == null) continue;
        String slug = category.optString("slug", category.optString("code"));
        String label = category.optString("name");
        if (slug.isEmpty() || label.isEmpty()) continue;
        if (slug.equals(s.searchCategory)) {
          ((TextView) root.findViewById(R.id.search_scope)).setText("Tìm trong " + label);
          visible(R.id.search_scope, true);
        }
        if (category.isNull("parentId") && parent.getChildCount() < 6) {
          u.add(parent, textRow(label, "ic_nova_search", () -> openCategory(slug)));
        }
      }
      visible(R.id.search_category_panel, parent.getChildCount() > 0);
    });
  }

  // Dựng dòng chữ có thể chạm cho lịch sử/gợi ý tìm kiếm.
  private LinearLayout textRow(String label, String icon, Runnable action) {
    LinearLayout row = u.row();
    row.setMinimumHeight(u.d(46));
    row.addView(u.icon(icon, 17));
    TextView text = u.text(label, 14);
    text.setPadding(u.d(12), u.d(8), u.d(8), u.d(8));
    text.setMaxLines(2);
    text.setEllipsize(android.text.TextUtils.TruncateAt.END);
    u.weight(row, text);
    row.addView(u.text("›", 20, Ui.MUTED, false));
    u.action(row, action);
    return row;
  }

  // Ghi từ khóa hiện tại và mở route kết quả tìm kiếm.
  private void showResults(String value) {
    String query = clean(value);
    s.searchDraft = query;
    setCatalogQuery(query, s.searchCategory);
    hideKeyboard();
    saveHistory(query);
    go("search-results");
  }

  // Ghi ngữ cảnh danh mục, bỏ bộ lọc không còn phù hợp rồi mở danh sách sản phẩm.
  private void openCategory(String slug) {
    setCatalogQuery("", slug);
    hideKeyboard();
    go("products");
  }

  // Đặt từ khóa/danh mục vào bộ lọc danh sách và xóa điều kiện cũ không còn phù hợp.
  private void setCatalogQuery(String query, String category) {
    s.query = query;
    s.category = category;
    s.brand = "";
    s.minPrice = "";
    s.maxPrice = "";
    s.inStock = false;
  }

  // Gửi từ khóa đã tìm vào lịch sử của tài khoản khi có phiên.
  private void saveHistory(String query) {
    if (loggedIn() && !query.isEmpty()) {
      api.request("POST", "/search-history", ApiClient.json("keyword", query),
          (data, error, code) -> {});
    }
  }

  // Ẩn bàn phím sau khi xác nhận tìm kiếm hoặc chọn một kết quả.
  private void hideKeyboard() {
    getSystemService(InputMethodManager.class).hideSoftInputFromWindow(input.getWindowToken(), 0);
    input.clearFocus();
  }

  // Chuẩn hóa dữ liệu danh sách phục vụ vùng gợi ý/lịch sử.
  private LinearLayout rows(int id) { return root.findViewById(id); }

  // Đặt View hiện hoặc ẩn theo việc có nội dung cần hiển thị.
  private void visible(int id, boolean show) {
    root.findViewById(id).setVisibility(show ? View.VISIBLE : View.GONE);
  }

  // Cập nhật dòng trạng thái tải/rỗng/lỗi của vùng tìm kiếm.
  private void status(String message) {
    TextView status = root.findViewById(R.id.search_status);
    status.setText(message);
    status.setVisibility(View.VISIBLE);
  }

  // Cắt và chuẩn hóa khoảng trắng của từ khóa người dùng nhập.
  private static String clean(String value) {
    return value.trim().replaceAll("\\s+", " ");
  }

  // Tạo khóa so sánh từ khóa/danh mục để nhận diện kết quả tìm kiếm còn phù hợp.
  private static String key(String value) {
    return clean(value).toLowerCase(Locale.ROOT);
  }
}

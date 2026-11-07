package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Điểm điều phối Android: khôi phục trạng thái, quản lý phiên, chọn lớp Screen theo route và dựng thanh điều hướng.
 * Bố cục liên quan: activity_main.xml, component_bottom_navigation.xml, item_bottom_navigation.xml.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;
import org.json.*;
import com.example.nova.AppState.Destination;
import com.example.nova.screens.*;

/** Activity host: lifecycle, navigation, shared state and guarded API callbacks only. */
public final class MainActivity extends Activity {
  private Ui u;
  private SecureSession session;
  private ApiClient api;
  private Images images;
  private ImagePicker imagePicker;
  private LinearLayout headerHost, content, footer;
  private TextView cartBadge, screenTitle;
  private final AppState s = new AppState();
  private BaseScreen currentScreen;
  private boolean activityResumed;
  public interface Data { void accept(Object data); }

  // Trả AppState dùng chung để các Screen đọc/ghi trạng thái điều hướng và bộ lọc.
  public AppState state() { return s; }
  // Trả bộ hàm giao diện Ui của Activity.
  public Ui ui() { return u; }
  // Trả bộ quản lý phiên đăng nhập dùng chung.
  public SecureSession session() { return session; }
  // Trả ApiClient dùng chung của Activity.
  public ApiClient api() { return api; }
  // Trả bộ tải ảnh dùng chung của Activity.
  public Images images() { return images; }
  // Trả vùng chứa nội dung màn hình.
  public LinearLayout contentHost() { return content; }
  // Trả vùng chứa thanh điều hướng hoặc thao tác ở chân màn hình.
  public LinearLayout footerHost() { return footer; }

  @Override
  // Khởi tạo thành phần và phục hồi trạng thái theo vòng đời Android.
  public void onCreate(Bundle state) {
    super.onCreate(state);
    u = new Ui(this);
    session = new SecureSession(this);
    api = new ApiClient(session);
    images = new Images(this, session);
    imagePicker = new ImagePicker(this);
    if (state != null) {
      s.route = state.getString("route", "home");
      s.entityId = state.getLong("entityId");
      s.query = state.getString("query", "");
      s.searchDraft = state.getString("searchDraft", s.query);
      s.searchCategory = state.getString("searchCategory", "");
      s.homeCategory = state.getString("homeCategory", "dien-thoai");
      s.homeSubcategory = state.getString("homeSubcategory", "");
      s.homeBrand = state.getString("homeBrand", "");
      s.homeFeatured = state.getBoolean("homeFeatured");
      s.category = state.getString("category", "");
      s.sort = state.getString("sort", "bestselling");
      s.minPrice = state.getString("minPrice", "");
      s.brand = state.getString("brand", "");
      s.inStock = state.getBoolean("inStock");
      s.maxPrice = state.getString("maxPrice", "");
      s.checkoutMethod = state.getString("checkoutMethod", "COD");
      s.checkoutNote = state.getString("checkoutNote", "");
      s.checkoutKey = state.getString("checkoutKey", s.checkoutKey);
      s.voucher = state.getString("voucher", "");
      s.addressId = state.getLong("addressId", 0);
      s.afterLogin = state.getString("afterLogin", "home");
      s.afterLoginId = state.getLong("afterLoginId", 0);
      if (s.route.equals("reset-password")) s.route = "forgot";
    } else if (getIntent().getLongExtra("productId", 0) > 0) {
      s.route = "product";
      s.entityId = getIntent().getLongExtra("productId", 0);
    }
    if (loggedIn()) verifyStoredCustomerSession();
    else render();
  }

  // Gọi /me để xác minh token lưu trên máy trước khi dùng hồ sơ và mở màn hình.
  private void verifyStoredCustomerSession() {
    setContentView(R.layout.activity_main);
    content = findViewById(R.id.main_content);
    u.add(content, u.text("Đang kiểm tra phiên đăng nhập…", 14, Ui.MUTED, false));
    api.get("/me", (data, error, status) -> {
      if (isFinishing() || isDestroyed()) return;
      if (error != null) {
        session.clear(); s.user = new JSONObject(); s.route = "home"; s.history.clear(); render();
        if (status != 401) alert("Kiểm tra kết nối", "Chưa xác minh được phiên đăng nhập. Vui lòng kiểm tra kết nối rồi đăng nhập lại.");
        return;
      }
      JSONObject profile = obj(data);
      if (!isCustomerProfile(profile)) { rejectNonCustomer(session.token()); return; }
      s.user = profile; render();
    });
  }

  // Thu hồi token không thuộc vai trò hỗ trợ, xóa phiên cục bộ và đưa về đăng nhập.
  public void rejectNonCustomer(String token) {
    api.revokeToken(token);
    session.clear(); s.user = new JSONObject(); s.cartCount = 0;
    s.history.clear(); s.route = "login"; s.entityId = 0; s.afterLogin = "account"; s.afterLoginId = 0;
    render();
    alert("Tài khoản chưa được hỗ trợ",
        "App NOVA hỗ trợ tài khoản khách hàng và quản trị viên. Vui lòng dùng tài khoản được cấp quyền phù hợp.");
  }

  /** The system picker grants access only to the images explicitly chosen by the user. */
  // Mở bộ chọn ảnh hệ thống theo số lượng tối đa mà màn hình yêu cầu.
  public void pickImages(boolean multiple, int maxCount, ImagePicker.Callback callback) {
    imagePicker.open(multiple, maxCount, callback);
  }

  // Nhận kết quả từ Android và chuyển kết quả chọn ảnh tới ImagePicker.
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (imagePicker != null && imagePicker.onActivityResult(requestCode, resultCode, data)) return;
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  // Lưu trạng thái giao diện vào Bundle để phục hồi khi Android tạo lại Activity.
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);
    state.putString("route", s.route);
    state.putLong("entityId", s.entityId);
    state.putString("query", s.query);
    state.putString("searchDraft", s.searchDraft);
    state.putString("searchCategory", s.searchCategory);
    state.putString("homeCategory", s.homeCategory);
    state.putString("homeSubcategory", s.homeSubcategory);
    state.putString("homeBrand", s.homeBrand);
    state.putBoolean("homeFeatured", s.homeFeatured);
    state.putString("category", s.category);
    state.putString("sort", s.sort);
    state.putString("minPrice", s.minPrice);
    state.putString("brand", s.brand);
    state.putBoolean("inStock", s.inStock);
    state.putString("maxPrice", s.maxPrice);
    state.putString("checkoutMethod", s.checkoutMethod);
    state.putString("checkoutNote", s.checkoutNote);
    state.putString("checkoutKey", s.checkoutKey);
    state.putString("voucher", s.voucher);
    state.putLong("addressId", s.addressId);
    state.putString("afterLogin", s.afterLogin);
    state.putLong("afterLoginId", s.afterLoginId);
  }

  @Override
  // Tiếp tục công việc cần chạy khi màn hình trở lại foreground.
  public void onResume() {
    super.onResume();
    activityResumed = true;
    if (currentScreen != null) currentScreen.onResume();
  }

  @Override
  // Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời.
  public void onPause() {
    activityResumed = false;
    if (currentScreen != null) currentScreen.onPause();
    super.onPause();
  }

  @Override
  // Đóng các tài nguyên nền khi Activity bị hủy.
  public void onDestroy() {
    if (currentScreen != null) currentScreen.onDispose();
    super.onDestroy();
    s.generation++;
    api.close();
    images.close();
    if (imagePicker != null) imagePicker.close();
  }

  @Override
  // Quay lại đích trước đó theo lịch sử điều hướng.
  public void onBackPressed() {
    if (!s.history.isEmpty()) {
      Destination d = s.history.pop();
      s.route = d.route();
      s.entityId = d.id();
      render();
    } else if (!s.route.equals("home")) {
      s.route = "home";
      render();
    } else super.onBackPressed();
  }

  // Kiểm tra có token phiên đang lưu; phiên vẫn cần BE xác thực khi gọi API.
  public boolean loggedIn() {
    return !session.token().isEmpty();
  }

  // Mở route mới và giữ đích hiện tại trong lịch sử Back.
  public void go(String next) {
    go(next, 0);
  }

  // Mở route mới và giữ đích hiện tại trong lịch sử Back.
  public void go(String next, long id) {
    if (next.equals("search")) {
      boolean fromCatalog = s.route.equals("products") || s.route.equals("search-results");
      s.searchDraft = fromCatalog ? s.query : "";
      s.searchCategory = fromCatalog ? s.category : "";
    }
    s.history.push(new Destination(s.route, s.entityId));
    s.route = next;
    s.entityId = id;
    render();
  }

  /** Replace transient QR/result routes without keeping an expired payment on Back. */
  // Thay route hiện tại để Back không quay về trạng thái thanh toán tạm đã hết hiệu lực.
  public void replace(String next, long id) {
    s.route = next;
    s.entityId = id;
    render();
  }

  // Chuyển tab chính và đặt lại lịch sử của nhánh điều hướng.
  public void tab(String next) {
    s.history.clear();
    s.route = next;
    s.entityId = 0;
    render();
  }

  // Chuyển sang đăng nhập nếu thiếu phiên, giữ lại đích để quay về sau khi xác thực.
  public boolean requireLogin() {
    if (loggedIn()) return true;
    s.afterLogin = s.route;
    s.afterLoginId = s.entityId;
    s.route = "login";
    render();
    return false;
  }

  // Chuẩn hóa dữ liệu thành JSONObject, dùng đối tượng rỗng khi kiểu không phù hợp.
  private JSONObject obj(Object value) {
    return value instanceof JSONObject ? (JSONObject) value : new JSONObject();
  }

  // Đọc JSONArray trực tiếp hoặc items của phản hồi phân trang, tránh ép sai kiểu dữ liệu.
  private JSONArray arr(Object value) {
    return value instanceof JSONArray ? (JSONArray) value : new JSONArray();
  }

  // Gửi GET qua lớp API dùng chung rồi xử lý dữ liệu trong callback.
  public void get(String path, Data success) {
    int stamp = s.generation;
    api.get(
        path,
        (data, error, status) -> {
          if (stamp != s.generation || isFinishing()) return;
          if (error != null) {
            if (BuildConfig.NOVA_MIDTERM_API && status == 404 && isFullEditionRoute()) {
              unavailableFeature();
              return;
            }
            error(error, () -> render());
            return;
          }
          if (path.equals("/me") && !isCustomerProfile(obj(data))) {
            rejectNonCustomer(session.token()); return;
          }
          success.accept(data);
        });
  }

  // Gửi yêu cầu thay đổi dữ liệu bằng HTTP method, đường dẫn và JSON được cung cấp.
  public void send(String method, String path, JSONObject body, Data success) {
    int stamp = s.generation;
    api.request(
        method,
        path,
        body,
        (data, error, status) -> {
          if (stamp != s.generation || isFinishing()) return;
          if (error != null) {
            alert("Chưa thể thực hiện", error);
            return;
          }
          success.accept(data);
        });
  }

  // Mở hộp thoại thông báo cho người dùng.
  public void alert(String title, String message) {
    new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Đã hiểu", null)
        .show();
  }

  // Hiện thông báo ngắn của Android.
  public void toast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  // Mở hộp thoại xác nhận rồi chỉ chạy action khi người dùng đồng ý.
  public void confirm(String title, String message, Runnable action) {
    new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton("Để sau", null)
        .setPositiveButton("Xác nhận", (d, w) -> action.run())
        .show();
  }

  // Kiểm tra route thuộc nhóm chức năng cần backend bản đầy đủ.
  private boolean isFullEditionRoute() {
    return switch (s.route) {
      case "favorites", "cart", "checkout", "addresses", "select-address", "edit-address", "add-address", "payment-methods",
          "orders", "order", "order-success", "order-tracking", "return-request", "cancel-order",
          "cancel-order-result", "product-reviews", "payments", "payment", "payment-failed",
          "returns", "return", "membership", "tiers", "flash", "notifications", "support",
          "chat", "vouchers" -> true;
      default -> false;
    };
  }

  // Thông báo giới hạn chức năng của chế độ API đang dùng.
  private void unavailableFeature() {
    LinearLayout card = u.card();
    u.add(card, u.text("Tính năng đang được hoàn thiện", 16, Ui.INK, true));
    u.space(card, 8);
    u.add(card, u.text("Bạn có thể tiếp tục khám phá sản phẩm và quản lý tài khoản của mình.", 13, Ui.MUTED, false));
    u.space(card, 12);
    u.add(card, u.button("Khám phá sản phẩm", false, () -> tab("categories")));
    u.add(content, card);
  }

  // Dựng thẻ lỗi có nút Thử lại gọi đúng tác vụ tải dữ liệu đã truyền vào.
  private void error(String message, Runnable retry) {
    LinearLayout c = u.card();
    u.add(c, u.text(message, 13, Ui.MUTED, false));
    u.space(c, 12);
    u.add(c, u.button("Thử lại", false, retry));
    u.add(content, c);
    u.space(content, 14);
  }

  // Ánh xạ route hiện tại thành tiêu đề tiếng Việt.
  private String title() {
    return switch (s.route) {
      case "categories" -> "Danh mục sản phẩm";
      case "products" -> "Sản phẩm theo danh mục";
      case "search-results" -> "Kết quả tìm kiếm";
      case "product-filters" -> "Bộ lọc sản phẩm";
      case "otp" -> "Xác thực OTP";
      case "product" -> "Chi tiết sản phẩm";
      case "favorites" -> "Yêu thích";
      case "search" -> "Tìm kiếm";
      case "cart" -> "Giỏ hàng";
      case "checkout" -> "Thanh toán";
      case "account" -> "Tài khoản";
      case "profile" -> "Thông tin cá nhân";
      case "admin-images" -> "Quản lý ảnh sản phẩm";
      case "password" -> "Đổi mật khẩu";
      case "email" -> "Đổi email";
      case "orders" -> "Đơn hàng của tôi";
      case "order" -> "Chi tiết đơn hàng";
      case "order-success" -> "Đặt hàng thành công";
      case "order-tracking" -> "Theo dõi giao hàng";
      case "return-request" -> "Yêu cầu đổi trả";
      case "cancel-order" -> "Hủy đơn hàng";
      case "cancel-order-result" -> "Kết quả hủy đơn";
      case "product-reviews" -> "Đánh giá sản phẩm";
      case "payments" -> "Lịch sử giao dịch";
      case "payment" -> "Thanh toán đơn hàng";
      case "payment-failed" -> "Thanh toán không thành công";
      case "returns" -> "Đổi trả & hoàn tiền";
      case "return" -> "Chi tiết yêu cầu";
      case "membership" -> "Thành viên NOVA";
      case "tiers" -> "Quyền lợi thành viên";
      case "flash" -> "Flash Sale";
      case "notifications" -> "Thông báo";
      case "support" -> "Trung tâm hỗ trợ";
      case "chat" -> "Chat với NOVA";
      case "vouchers" -> "Ưu đãi của bạn";
      case "forgot" -> "Quên mật khẩu";
      case "reset-password" -> "Tạo mật khẩu mới";
      case "edit-address" -> "Sửa địa chỉ";
      case "add-address" -> "Thêm địa chỉ";
      case "payment-methods" -> "Phương thức thanh toán";
      default -> "Địa chỉ giao hàng";
    };
  }

  // Dựng vùng đầu màn hình với tiêu đề và thao tác quay lại phù hợp.
  private void header(String title) {
    LinearLayout h = u.row();
    int inset = getResources().getDimensionPixelSize(R.dimen.nova_page_inset);
    h.setPadding(inset, u.d(12), inset, u.d(12));
    FrameLayout back = new FrameLayout(this);
    back.setBackground(u.outline(Color.WHITE, 12, Ui.BORDER));
    back.addView(
        u.icon("node_6_2044_imgarrowleft", 20),
        new FrameLayout.LayoutParams(u.d(20), u.d(20), Gravity.CENTER));
    back.setContentDescription("Quay lại");
    back.setOnClickListener(v -> onBackPressed());
    h.addView(back, new LinearLayout.LayoutParams(u.d(36), u.d(40)));
    boolean profile = s.route.equals("profile");
    TextView t = u.text(title, profile ? 16 : 20, Ui.INK, true);
    screenTitle = t;
    if (profile) t.setGravity(Gravity.CENTER);
    if (!profile) t.setPadding(u.d(12), 0, 0, 0);
    u.weight(h, t);
    if (profile) h.addView(new View(this), new LinearLayout.LayoutParams(u.d(36), u.d(40)));
    else h.addView(u.icon("nova_logo", 30));
    u.add(headerHost, h);
  }

  // Đổi tiêu đề đang hiển thị trong phần đầu màn hình.
  public void setScreenTitle(String title) { if (screenTitle != null) screenTitle.setText(title); }

  // Kiểm tra vai trò hồ sơ được ứng dụng hỗ trợ theo chế độ API hiện tại.
  public static boolean isCustomerProfile(JSONObject profile) {
    String expectedRole = BuildConfig.NOVA_MIDTERM_API ? "CUSTOMER" : "USER";
    return expectedRole.equals(profile.optString("role")) || "ADMIN".equals(profile.optString("role"));
  }

  // Dựng bốn tab chính, trạng thái đang chọn và badge giỏ hàng.
  private void navigation() {
    View navigation = getLayoutInflater().inflate(R.layout.component_bottom_navigation, footer, false);
    LinearLayout tabs = navigation.findViewById(R.id.bottom_nav_tabs);
    String[] labels = {"Trang chủ", "Danh mục", "Giỏ hàng", "Tài khoản"};
    String[] routes = {"home", "categories", "cart", "account"};
    int[] icons = {R.drawable.nova_nav_home, R.drawable.nova_nav_categories,
        R.drawable.nova_nav_cart, R.drawable.nova_nav_account};
    for (int i = 0; i < routes.length; i++) {
      String target = routes[i];
      boolean selected = s.route.equals(target)
          || (target.equals("categories") && (s.route.equals("products") || s.route.equals("search-results")));
      View item = getLayoutInflater().inflate(R.layout.item_bottom_navigation, tabs, false);
      int tint = selected ? Ui.BLUE : Ui.MUTED;
      ImageView icon = item.findViewById(R.id.bottom_nav_icon);
      icon.setImageResource(icons[i]);
      icon.setColorFilter(tint);
      TextView label = item.findViewById(R.id.bottom_nav_label);
      label.setText(labels[i]);
      label.setTextColor(tint);
      if (selected) {
        label.setTypeface(label.getTypeface(), android.graphics.Typeface.BOLD);
        item.findViewById(R.id.bottom_nav_indicator).setBackgroundResource(R.drawable.nova_nav_selected);
      }
      if (target.equals("cart")) cartBadge = item.findViewById(R.id.bottom_nav_badge);
      item.setSelected(selected);
      item.setOnClickListener(v -> tab(target));
      u.applyTypography(item);
      tabs.addView(item);
    }
    updateCartBadge();
    footer.addView(navigation);
  }

  // Cập nhật số lượng hiển thị trên badge của tab giỏ hàng.
  public void updateCartBadge() {
    if (cartBadge != null) {
      cartBadge.setText(String.valueOf(s.cartCount));
      cartBadge.setVisibility(s.cartCount > 0 ? View.VISIBLE : View.GONE);
    }
  }

  /** Each route gets its own controller instance and its own screen XML. */
  // Hủy controller cũ, tăng generation, dựng khung Activity và chọn đúng lớp Screen qua switch(route); generation chặn callback của màn hình trước.
  public void render() {
    if (currentScreen != null) currentScreen.onDispose();
    s.generation++;
    setContentView(R.layout.activity_main);
    screenTitle = null;
    cartBadge = null;
    headerHost = findViewById(R.id.main_header_host);
    content = findViewById(R.id.main_content);
    footer = findViewById(R.id.main_footer);
    if (!Arrays.asList("home", "login", "register", "account", "reset-password", "search").contains(s.route)) header(title());
    // Route chỉ chọn lớp xử lý giao diện; toàn bộ màn dùng chung một Activity và AppState.
    currentScreen = switch (s.route) {
      case "home" -> new HomeScreen(this);
      case "login" -> new LoginScreen(this);
      case "register" -> new RegisterScreen(this);
      case "forgot" -> new ForgotPasswordScreen(this);
      case "reset-password" -> new ResetPasswordScreen(this);
      case "categories" -> new CategoryScreen(this);
      case "products" -> new CatalogScreen(this);
      case "search-results" -> new SearchResultsScreen(this);
      case "product-filters" -> new ProductFiltersScreen(this);
      case "otp" -> new OtpScreen(this);
      case "product" -> new ProductScreen(this);
      case "favorites" -> new FavoritesScreen(this);
      case "search" -> new SearchScreen(this);
      case "cart" -> new CartScreen(this);
      case "checkout" -> new CheckoutScreen(this);
      case "addresses" -> new AddressesScreen(this);
      case "select-address" -> new SelectAddressScreen(this);
      case "edit-address" -> new AddressFormScreen(this);
      case "add-address" -> new AddAddressScreen(this);
      case "payment-methods" -> new PaymentMethodsScreen(this);
      case "account" -> new AccountScreen(this);
      case "profile" -> new ProfileScreen(this);
      case "admin-images" -> new AdminImagesScreen(this);
      case "password" -> new ChangePasswordScreen(this);
      case "email" -> new ChangeEmailScreen(this);
      case "orders" -> new OrdersScreen(this);
      case "order" -> new OrderDetailScreen(this);
      case "order-success" -> new OrderSuccessScreen(this);
      case "order-tracking" -> new OrderTrackingScreen(this);
      case "return-request" -> new ReturnRequestScreen(this);
      case "cancel-order" -> new CancelOrderScreen(this);
      case "cancel-order-result" -> new CancelOrderResultScreen(this);
      case "product-reviews" -> new ProductReviewsScreen(this);
      case "payments" -> new PaymentsScreen(this);
      case "payment" -> new PaymentDetailScreen(this);
      case "payment-failed" -> new PaymentFailedScreen(this);
      case "returns" -> new ReturnsScreen(this);
      case "return" -> new ReturnDetailScreen(this);
      case "membership" -> new MembershipScreen(this);
      case "tiers" -> new TiersScreen(this);
      case "flash" -> new FlashSaleScreen(this);
      case "notifications" -> new NotificationsScreen(this);
      case "support" -> new SupportScreen(this);
      case "chat" -> new ChatScreen(this);
      case "vouchers" -> new VouchersScreen(this);
      default -> new HomeScreen(this);
    };
    // Giữ tham chiếu controller đang dựng vì một callback có thể điều hướng sang màn khác.
    BaseScreen rendering = currentScreen;
    rendering.render();
    if (activityResumed && currentScreen == rendering) rendering.onResume();
    boolean showNavigation = Arrays.asList("home", "categories", "products", "search-results",
        "cart", "account", "favorites", "flash").contains(s.route);
    getWindow().setNavigationBarColor(showNavigation ? getColor(R.color.nova_bg) : Color.WHITE);
    if (showNavigation) navigation();
  }
}

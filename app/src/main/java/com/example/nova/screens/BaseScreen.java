package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Lớp nền cho các màn hình: dùng chung MainActivity, AppState, ApiClient, Ui, Images và các hàm dựng/gắn giao diện.
 * Bố cục liên quan: dialog_otp.xml, item_product_card.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.Settings;
import android.text.InputType;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import java.util.*;
import org.json.*;
import com.example.nova.*;

/** Shared native view helpers. Concrete screens own their binding and API behavior. */
public abstract class BaseScreen extends android.content.ContextWrapper {
  protected final MainActivity activity;
  protected final AppState s;
  protected final Ui u;
  protected final SecureSession session;
  protected final ApiClient api;
  protected final Images images;
  protected LinearLayout content;
  protected final LinearLayout footer;
  protected static final int EMAIL = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
      PASSWORD = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
      TEXT = InputType.TYPE_CLASS_TEXT, PHONE = InputType.TYPE_CLASS_PHONE;
  protected interface Code { void accept(String code); }

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  protected BaseScreen(MainActivity activity) {
    super(activity);
    this.activity = activity;
    s = activity.state(); u = activity.ui(); session = activity.session();
    api = activity.api(); images = activity.images();
    content = activity.contentHost(); footer = activity.footerHost();
  }

  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  public abstract int layoutId();
  // Hợp đồng để lớp Screen cụ thể dựng và gắn giao diện; MainActivity gọi sau khi chọn route.
  public abstract void render();
  /** Screen timers/listeners run only while their Activity is foreground. */
  // Tiếp tục công việc cần chạy khi màn hình trở lại foreground.
  public void onResume() {}
  // Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời.
  public void onPause() {}
  // Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn.
  public void onDispose() { onPause(); }
  // Kiểm tra có token phiên đang lưu; phiên vẫn cần BE xác thực khi gọi API.
  protected boolean loggedIn() { return activity.loggedIn(); }
  // Chuyển sang đăng nhập nếu thiếu phiên, giữ lại đích để quay về sau khi xác thực.
  protected boolean requireLogin() { return activity.requireLogin(); }
  // Mở route mới và giữ đích hiện tại trong lịch sử Back.
  protected void go(String route) { activity.go(route); }
  // Mở route mới và giữ đích hiện tại trong lịch sử Back.
  protected void go(String route, long id) { activity.go(route, id); }
  // Chuyển tab chính và đặt lại lịch sử của nhánh điều hướng.
  protected void tab(String route) { activity.tab(route); }
  // Quay lại đích trước đó theo lịch sử điều hướng.
  protected void onBackPressed() { activity.onBackPressed(); }
  // Kiểm tra Activity đang kết thúc để không tiếp tục cập nhật giao diện.
  protected boolean isFinishing() { return activity.isFinishing(); }
  // Kiểm tra Activity đã bị hủy trước khi thao tác với View.
  protected boolean isDestroyed() { return activity.isDestroyed(); }
  // Chuyển yêu cầu quyền Android tới Activity sở hữu màn hình.
  protected void requestPermissions(String[] permissions, int requestCode) { activity.requestPermissions(permissions, requestCode); }
  // Lấy LayoutInflater của Activity để nạp các layout XML.
  protected LayoutInflater getLayoutInflater() { return activity.getLayoutInflater(); }
  // Cập nhật số lượng hiển thị trên badge của tab giỏ hàng.
  protected void updateCartBadge() { activity.updateCartBadge(); }
  // Thu hồi token không thuộc vai trò hỗ trợ, xóa phiên cục bộ và đưa về đăng nhập.
  protected void rejectNonCustomer(String token) { activity.rejectNonCustomer(token); }
  // Gửi GET qua lớp API dùng chung rồi xử lý dữ liệu trong callback.
  protected void get(String path, MainActivity.Data success) { activity.get(path, success); }
  // Gửi yêu cầu thay đổi dữ liệu bằng HTTP method, đường dẫn và JSON được cung cấp.
  protected void send(String method, String path, JSONObject body, MainActivity.Data success) { activity.send(method, path, body, success); }
  // Mở hộp thoại thông báo cho người dùng.
  protected void alert(String title, String message) { activity.alert(title, message); }
  // Hiện thông báo ngắn của Android.
  protected void toast(String message) { activity.toast(message); }
  // Mở hộp thoại xác nhận rồi chỉ chạy action khi người dùng đồng ý.
  protected void confirm(String title, String message, Runnable action) { activity.confirm(title, message, action); }
  // Nạp layout và đặt screen_body làm vùng chứa nội dung động của màn hình.
  protected LinearLayout bindBody(int layoutId) {
    LinearLayout root = screen(layoutId);
    LinearLayout body = root.findViewById(R.id.screen_body);
    if (body == null) throw new IllegalStateException("Screen XML is missing screen_body");
    content = body;
    return root;
  }

  // Chuẩn hóa dữ liệu thành JSONObject, dùng đối tượng rỗng khi kiểu không phù hợp.
  protected JSONObject obj(Object value) {
    return value instanceof JSONObject ? (JSONObject) value : new JSONObject();
  }

  // Đọc JSONArray trực tiếp hoặc items của phản hồi phân trang, tránh ép sai kiểu dữ liệu.
  protected JSONArray arr(Object value) {
    if (value instanceof JSONArray list) return list;
    // Catalog responses on NOVA_GK carry a page of products in the items field.
    if (value instanceof JSONObject page && page.optJSONArray("items") != null) {
      return page.optJSONArray("items");
    }
    return new JSONArray();
  }

  // Dựng trạng thái chưa có dữ liệu với tiêu đề và mô tả.
  protected void empty(String title, String message) {
    LinearLayout c = u.card();
    u.space(c, 16);
    u.add(c, u.text(title, 20, Ui.INK, true));
    u.space(c, 10);
    u.add(c, u.text(message, 13, Ui.MUTED, false));
    u.space(c, 16);
    u.add(content, c);
  }

  // Dựng tiêu đề một khối nội dung và gắn thao tác xem thêm khi được cung cấp.
  protected void section(String title, Runnable more) {
    LinearLayout r = u.row();
    u.weight(r, u.text(title, 20, Ui.INK, true));
    if (more != null) r.addView(u.link("Xem tất cả  →", more));
    u.add(content, r);
    u.space(content, 14);
  }

  // Thêm khoảng cách dọc giữa các khối giao diện.
  protected void gap() {
    u.space(content, 16);
  }

  // Tạo vùng ảnh sản phẩm và chuyển URL cho bộ tải ảnh dùng chung.
  protected ImageView image(JSONObject p, int h) {
    ImageView v = new ImageView(this);
    v.setScaleType(ImageView.ScaleType.FIT_CENTER);
    v.setLayoutParams(new LinearLayout.LayoutParams(-1, u.d(h)));
    images.product(v, p.optString("imageUrl"), p.optLong("id"));
    v.setContentDescription(p.optString("name"));
    return v;
  }

  // Inflate XML, bỏ ViewStub chỉ dành cho Preview và áp dụng font dùng chung.
  protected LinearLayout inflateLayout(int layoutId, ViewGroup parent) {
    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(layoutId, parent, false);
    // The editor can inflate these stubs to show sample lists.
    View sample;
    while ((sample = layout.findViewWithTag("nova_preview")) != null) {
      ((ViewGroup) sample.getParent()).removeView(sample);
    }
    u.applyTypography(layout);
    return layout;
  }

  // Nạp layout của màn hình và gắn vào vùng nội dung của Activity.
  protected LinearLayout screen(int layoutId) {
    LinearLayout layout = inflateLayout(layoutId, content);
    u.add(content, layout);
    return layout;
  }

  // Chia danh sách JSON thành lưới hai cột; gắn ảnh, giá và sự kiện mở đúng sản phẩm.
  protected void productGrid(LinearLayout parent, JSONArray products, int limit) {
    productGrid(parent, products, limit, false);
  }

  // Chia danh sách JSON thành lưới hai cột; gắn ảnh, giá và sự kiện mở đúng sản phẩm.
  protected void productGrid(LinearLayout parent, JSONArray products, int limit, boolean homeActions) {
    parent.removeAllViews();
    int count = Math.min(products.length(), limit);
    if (count == 0) {
      u.add(parent, u.text("Chưa có sản phẩm phù hợp.", 13, Ui.MUTED, false));
      return;
    }
    for (int i = 0; i < count; i += 2) {
      LinearLayout row = u.row();
      row.setGravity(Gravity.TOP);
      row.setBaselineAligned(false);
      for (int j = i; j < Math.min(i + 2, count); j++) {
        JSONObject product = products.optJSONObject(j);
        if (product == null) continue;
        long id = product.optLong("id");
        LinearLayout card = inflateLayout(R.layout.item_product_card, row);
        card.setTag("product-card-" + id);
        ImageView photo = card.findViewById(R.id.card_product_image);
        images.product(photo, product.optString("imageUrl"), id);
        ((TextView) card.findViewById(R.id.card_product_name)).setText(product.optString("name"));
        int reviews = product.optInt("reviewCount");
        String rating = reviews > 0
            ? String.format(java.util.Locale.forLanguageTag("vi-VN"), "★ %.1f · %d đánh giá",
                product.optDouble("rating"), reviews)
            : "Chưa có đánh giá";
        ((TextView) card.findViewById(R.id.card_product_rating)).setText(rating);
        ((TextView) card.findViewById(R.id.card_product_price)).setText(Ui.money(product.optLong("price")));
        TextView original = card.findViewById(R.id.card_product_old_price);
        if (product.optLong("originalPrice") > product.optLong("price")) {
          original.setText(Ui.money(product.optLong("originalPrice")));
          original.setPaintFlags(original.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
          original.setVisibility(View.VISIBLE);
        }
        if (homeActions && !BuildConfig.NOVA_MIDTERM_API) {
          LinearLayout actions = card.findViewById(R.id.card_product_actions);
          actions.setVisibility(View.VISIBLE);
          TextView save = u.chip("♡", false, () -> {
            if (requireLogin()) send("POST", "/favorites/" + id, null, ignored -> toast("Đã lưu yêu thích"));
          });
          save.setContentDescription("Yêu thích " + product.optString("name"));
          TextView add = u.chip("+ Thêm giỏ", false, () -> {
            JSONArray variants = product.optJSONArray("variants");
            JSONObject variant = variants == null ? null : variants.optJSONObject(0);
            if (variant == null) go("product", id);
            else addToCart(variant.optLong("id"), false, id);
          });
          actions.addView(save, new LinearLayout.LayoutParams(u.d(40), -2));
          LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(0, -2, 1);
          addParams.leftMargin = u.d(6);
          actions.addView(add, addParams);
        }
        card.setOnClickListener(v -> go("product", id));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        if (j == i) params.rightMargin = u.d(12);
        row.addView(card, params);
      }
      if (i + 1 >= count) row.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1));
      u.add(parent, row);
      u.space(parent, 12);
    }
  }

  // Gửi thao tác thêm SKU vào giỏ và cập nhật thông tin giỏ từ kết quả API.
  protected void addToCart(long variantId, boolean buy) {
    addToCart(variantId, buy, s.entityId);
  }

  // Gửi thao tác thêm SKU vào giỏ và cập nhật thông tin giỏ từ kết quả API.
  protected void addToCart(long variantId, boolean buy, long productId) {
    if (!loggedIn()) {
      s.afterLogin = "product";
      s.afterLoginId = productId;
      go("login");
      return;
    }
    if (variantId == 0) {
      alert("Chọn phiên bản", "Sản phẩm chưa có phiên bản khả dụng.");
      return;
    }
    send(
        "POST",
        "/cart/items",
        json("variantId", variantId, "quantity", 1),
        d -> {
          s.cartCount = obj(d).optInt("count", s.cartCount + 1);
          if (buy) go("cart");
          else { updateCartBadge(); toast("Đã thêm vào giỏ hàng"); }
        });
  }

  // Dựng hộp thoại nhập mã OTP theo callback của thao tác yêu cầu.
  protected void otpDialog(String title, JSONObject challenge, Code callback) {
    LinearLayout form = inflateLayout(R.layout.dialog_otp, content);
    if (challenge.has("developmentCode")) {
      TextView developmentCode = form.findViewById(R.id.otp_development_code);
      developmentCode.setText("Môi trường phát triển · mã thử nghiệm: " + challenge.optString("developmentCode"));
      developmentCode.setVisibility(View.VISIBLE);
    }
    EditText code = form.findViewById(R.id.otp_code);
    AlertDialog dialog = new AlertDialog.Builder(this).setTitle(title).setView(form)
        .setNegativeButton("Đóng", null).setPositiveButton("Xác thực", null).create();
    dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(v -> {
          String value = code.getText().toString().trim();
          if (!value.matches("\\d{6}")) { code.setError("Nhập đủ 6 chữ số OTP"); return; }
          dialog.dismiss(); callback.accept(value);
        }));
    dialog.show();
  }

  // Ghép các trường địa chỉ thành một chuỗi dễ đọc.
  protected String formatAddress(JSONObject a) {
    StringJoiner line = new StringJoiner(", ");
    for (String key : new String[] {"line", "ward", "district", "province"})
      if (!a.optString(key).trim().isEmpty()) line.add(a.optString(key));
    return line.toString();
  }

  // Định dạng dữ liệu ngày giờ để hiển thị trên giao diện.
  protected String date(String raw) {
    try {
      return java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
          .withZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh"))
          .format(java.time.Instant.parse(raw));
    } catch (RuntimeException invalid) { return raw; }
  }

  // Đổi mã trạng thái nghiệp vụ thành nhãn hiển thị cho người dùng.
  protected String statusLabel(String value) {
    return switch (value) {
      case "AWAITING_CONFIRMATION" -> "Chờ xác nhận";
      case "PROCESSING" -> "Đang chuẩn bị";
      case "SHIPPING" -> "Đang giao hàng";
      case "DELIVERED" -> "Đã giao hàng";
      case "CANCELLED" -> "Đã hủy";
      case "PENDING" -> "Đang chờ xử lý";
      case "PAID", "SUCCEEDED", "SUCCESS" -> "Đã thanh toán";
      case "UNPAID" -> "Chưa thanh toán";
      case "EXPIRED" -> "Đã hết hạn";
      case "REFUND_PENDING" -> "Đang chờ hoàn tiền";
      case "PARTIALLY_REFUNDED" -> "Đã hoàn tiền một phần";
      case "REQUESTED" -> "Đã gửi yêu cầu";
      case "FAILED" -> "Không thành công";
      case "REFUNDED" -> "Đã hoàn tiền";
      case "APPROVED" -> "Đã chấp thuận";
      case "REJECTED" -> "Đã từ chối";
      case "COMPLETED" -> "Đã hoàn tất";
      default -> value;
    };
  }

  // Dựng các mốc thời gian/trạng thái từ dữ liệu API.
  protected void timeline(LinearLayout parent, JSONArray history) {
    if (history == null || history.length() == 0) {
      u.add(parent, u.text("Chưa có cập nhật.", 12, Ui.MUTED, false));
      return;
    }
    for (int i = 0; i < history.length(); i++) {
      JSONObject h = history.optJSONObject(i);
      LinearLayout c = u.card();
      u.add(c, u.text("●  " + statusLabel(h.optString("status")), 13, Ui.BRAND, true));
      u.space(c, 7);
      u.add(c, u.text(h.optString("note", h.optString("description")), 12));
      u.space(c, 5);
      u.add(c, u.text(date(h.optString("createdAt")), 10, Ui.MUTED, false));
      u.add(parent, c);
      u.space(parent, 10);
    }
  }

  // Dựng biểu mẫu đánh giá và gửi nội dung người dùng đã nhập.
  protected void reviewForm(long orderItemId, JSONObject existing) {
    LinearLayout form = u.col();
    u.pad(form, 20);
    RatingBar rating = new RatingBar(this);
    rating.setNumStars(5);
    rating.setStepSize(1);
    rating.setRating(existing == null ? 5 : existing.optInt("rating"));
    form.addView(rating, new LinearLayout.LayoutParams(-2, -2));
    u.space(form, 16);
    EditText content =
        u.field(
            form,
            "Chia sẻ trải nghiệm",
            existing == null ? "" : existing.optString("content"),
            TEXT);
    content.setSingleLine(false);
    content.setMinLines(3);
    new AlertDialog.Builder(this)
        .setTitle(existing == null ? "Đánh giá sản phẩm" : "Cập nhật đánh giá")
        .setView(form)
        .setNegativeButton("Đóng", null)
        .setPositiveButton(
            "Gửi đánh giá",
            (d, w) ->
                send(
                    existing == null ? "POST" : "PUT",
                    existing == null ? "/reviews" : "/reviews/" + existing.optLong("id"),
                    json(
                        "orderItemId",
                        orderItemId,
                        "rating",
                        (int) rating.getRating(),
                        "content",
                        content.getText().toString().trim()),
                    ok -> {
                      toast("Đã gửi đánh giá");
                      activity.render();
                    }))
        .show();
  }
}

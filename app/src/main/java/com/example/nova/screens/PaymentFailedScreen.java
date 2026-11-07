package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị lỗi hoặc hết hạn và cho phép kiểm tra hoặc tạo lại giao dịch khi phù hợp.
 * Bố cục liên quan: screen_payment_failed.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;

/** Failure and expiry are displayed only after the server reports them. */
public final class PaymentFailedScreen extends BaseScreen {
  private long paymentId, orderId;
  private int generation, requestEpoch;
  private boolean active, disposed, inFlight, retryAllowed;
  private String sessionToken = "";
  private TextView title, detail, number, amount, error, retry, refresh, order;

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public PaymentFailedScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_payment_failed; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = bindBody(layoutId());
    paymentId = s.entityId;
    generation = s.generation;
    sessionToken = session.token();
    title = root.findViewById(R.id.payment_failed_title);
    detail = root.findViewById(R.id.payment_failed_detail);
    number = root.findViewById(R.id.payment_failed_number);
    amount = root.findViewById(R.id.payment_failed_amount);
    error = root.findViewById(R.id.payment_failed_error);
    retry = root.findViewById(R.id.payment_failed_retry);
    refresh = root.findViewById(R.id.payment_failed_refresh);
    order = root.findViewById(R.id.payment_failed_order);
    retry.setOnClickListener(v -> {
      if (isCurrent() && retryAllowed && orderId > 0)
        request("POST", "/orders/" + orderId + "/payments", true);
    });
    refresh.setOnClickListener(v -> requestStatus());
    order.setOnClickListener(v -> { if (isCurrent() && orderId > 0) activity.replace("order", orderId); });
    if (paymentId <= 0) showError("Không tìm thấy mã giao dịch. Vui lòng mở lại từ đơn hàng.");
  }

  // Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời.
  @Override public void onPause() { stopWork(); }

  // Tiếp tục công việc cần chạy khi màn hình trở lại foreground.
  @Override public void onResume() {
    if (disposed || title == null || active || paymentId <= 0) return;
    active = true;
    requestStatus();
  }

  // Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn.
  @Override public void onDispose() {
    disposed = true;
    stopWork();
  }

  // Hủy các callback kiểm tra trạng thái và bộ đếm của giao dịch cũ.
  private void stopWork() {
    active = false;
    requestEpoch++;
    inFlight = false;
  }

  // Kiểm tra phiên bản yêu cầu vẫn khớp giao dịch/màn hình đang mở.
  private boolean isCurrent() {
    return isCurrentScreen() && session.token().equals(sessionToken);
  }

  // Kiểm tra route, ID giao dịch và vòng đời trước khi cập nhật View.
  private boolean isCurrentScreen() {
    return active && !disposed && !isFinishing() && !isDestroyed()
        && s.generation == generation && s.route.equals("payment-failed") && s.entityId == paymentId;
  }

  // Lấy trạng thái thanh toán mới nhất khi màn hình/giao dịch còn hợp lệ.
  private void requestStatus() {
    if (paymentId > 0) request("GET", "/payments/" + paymentId, false);
    else showError("Không tìm thấy mã giao dịch. Vui lòng mở lại từ đơn hàng.");
  }

  // Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback.
  private void request(String verb, String path, boolean creating) {
    if (!isCurrent() || inFlight) return;
    inFlight = true;
    updateButtons();
    final int epoch = requestEpoch;
    api.request(verb, path, null, (data, issue, code) -> {
      if (!isCurrentScreen() || epoch != requestEpoch) return;
      if (!session.token().equals(sessionToken)) {
        stopWork();
        if (code == 401 || session.token().isEmpty()) activity.requireLogin();
        return;
      }
      inFlight = false;
      if (issue != null) {
        if (code == 401) { stopWork(); activity.requireLogin(); return; }
        showError(issue + " Vui lòng kiểm tra lại trạng thái trước khi tiếp tục.");
      } else {
        JSONObject payment = obj(data);
        long resultId = payment.optLong("id");
        long resultOrder = payment.optLong("orderId");
        String state = payment.optString("status");
        if (resultId <= 0 || resultOrder <= 0) {
          showError("Chưa đọc được thông tin thanh toán. Vui lòng kiểm tra lại.");
        } else if (state.equals("SUCCESS")) {
          activity.replace("order-success", resultOrder);
          return;
        } else if (state.equals("PENDING")) {
          activity.replace("payment", resultId);
          return;
        } else {
          orderId = resultOrder;
          retryAllowed = state.equals("FAILED");
          title.setText(state.equals("EXPIRED") ? "Mã thanh toán đã hết hạn"
              : state.equals("FAILED") ? "Thanh toán chưa thành công" : "Trạng thái thanh toán");
          detail.setText(state.equals("EXPIRED")
              ? "Máy chủ xác nhận mã QR đã hết hiệu lực. Vui lòng đặt lại đơn nếu vẫn muốn mua hàng."
              : state.equals("FAILED")
                  ? "Máy chủ xác nhận giao dịch không thành công. Bạn có thể thử lại hoặc xem đơn hàng."
                  : "Trạng thái từ máy chủ: " + statusLabel(state) + ". Vui lòng xem đơn hàng để tiếp tục.");
          number.setText(payment.optString("orderNumber", "Đơn hàng #" + orderId));
          amount.setText(Ui.money(payment.optLong("amount")));
          error.setVisibility(View.GONE);
          if (creating && retryAllowed)
            showError("Chưa tạo được phiên thanh toán mới. Vui lòng kiểm tra lại hoặc liên hệ hỗ trợ.");
        }
      }
      updateButtons();
    });
  }

  // Bật/tắt các nút kiểm tra hoặc thử lại theo trạng thái thanh toán.
  private void updateButtons() {
    retry.setEnabled(!inFlight && retryAllowed && orderId > 0);
    retry.setAlpha(retry.isEnabled() ? 1f : .55f);
    refresh.setEnabled(!inFlight);
    refresh.setAlpha(inFlight ? .55f : 1f);
    order.setEnabled(orderId > 0);
    order.setAlpha(orderId > 0 ? 1f : .55f);
  }

  // Đưa lỗi vào vùng trạng thái và cập nhật thao tác phù hợp của giao diện.
  private void showError(String message) {
    error.setText(message);
    error.setVisibility(View.VISIBLE);
  }
}

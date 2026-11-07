package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị số tiền, hướng dẫn, QR hoặc cổng thanh toán và theo dõi trạng thái giao dịch.
 * Bố cục liên quan: screen_payment.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Locale;
import org.json.JSONObject;
import com.example.nova.*;

/** A server-owned payment session: the device displays time but never declares payment success. */
public final class PaymentDetailScreen extends BaseScreen {
  private static final long POLL_MS = 3000;
  private final Handler handler = new Handler(Looper.getMainLooper());
  private long paymentId, orderId, deadlineElapsed = -1;
  private int generation, requestEpoch;
  private boolean active, disposed, inFlight, deadlineRecheckRequested;
  private String sessionToken = "", renderedQr = "";
  private JSONObject payment;
  private TextView number, amount, method, status, countdown, expires, instructions, error;
  private TextView refresh, retry, order, open, demo, demoNotice, qrError;
  private ImageView qr;

  private final Runnable poll = () -> requestStatus();
  private final Runnable tick = new Runnable() {
    // Bước công việc hẹn giờ: cập nhật trạng thái hiện tại rồi lên lịch tiếp nếu còn hợp lệ.
    @Override public void run() {
      if (!isCurrent()) return;
      updateCountdown();
      if (payment == null || pending()) handler.postDelayed(this, 1000);
    }
  };

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public PaymentDetailScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_payment; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = bindBody(layoutId());
    paymentId = s.entityId;
    generation = s.generation;
    sessionToken = session.token();
    number = root.findViewById(R.id.payment_order_number);
    amount = root.findViewById(R.id.payment_amount);
    method = root.findViewById(R.id.payment_method);
    status = root.findViewById(R.id.payment_status);
    countdown = root.findViewById(R.id.payment_countdown);
    expires = root.findViewById(R.id.payment_expires);
    instructions = root.findViewById(R.id.payment_instructions);
    error = root.findViewById(R.id.payment_error);
    qr = root.findViewById(R.id.payment_qr);
    qrError = root.findViewById(R.id.payment_qr_error);
    refresh = root.findViewById(R.id.payment_refresh);
    retry = root.findViewById(R.id.payment_retry);
    order = root.findViewById(R.id.payment_order);
    open = root.findViewById(R.id.payment_open_gateway);
    demo = root.findViewById(R.id.payment_demo_success);
    demoNotice = root.findViewById(R.id.payment_demo_notice);
    refresh.setOnClickListener(v -> requestStatus());
    retry.setOnClickListener(v -> retryPayment());
    order.setOnClickListener(v -> { if (isCurrent() && orderId > 0) activity.replace("order", orderId); });
    open.setOnClickListener(v -> openGateway());
    demo.setOnClickListener(v -> {
      if (!isCurrent() || payment == null || !payment.optBoolean("demo") || !pending()) return;
      confirm("Xác nhận thử nghiệm?", "Chỉ mô phỏng giao dịch demo, không chuyển tiền thật.", () -> {
        if (isCurrent() && payment != null && payment.optBoolean("demo") && pending())
          request("POST", "/dev/payments/" + paymentId + "/succeed");
      });
    });
    if (paymentId <= 0) {
      showError("Không tìm thấy mã giao dịch. Vui lòng mở lại từ đơn hàng.");
      refresh.setEnabled(false);
      return;
    }
  }

  // Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời.
  @Override public void onPause() { stopWork(); }

  // Tiếp tục công việc cần chạy khi màn hình trở lại foreground.
  @Override public void onResume() {
    if (disposed || number == null || active || paymentId <= 0) return;
    active = true;
    requestStatus();
    handler.post(tick);
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
    handler.removeCallbacksAndMessages(null);
  }

  // Kiểm tra phiên bản yêu cầu vẫn khớp giao dịch/màn hình đang mở.
  private boolean isCurrent() {
    return isCurrentScreen() && session.token().equals(sessionToken);
  }

  // Kiểm tra route, ID giao dịch và vòng đời trước khi cập nhật View.
  private boolean isCurrentScreen() {
    return active && !disposed && !isFinishing() && !isDestroyed()
        && s.generation == generation && s.route.equals("payment") && s.entityId == paymentId;
  }

  // Kiểm tra giao dịch còn thuộc trạng thái đang chờ xử lý.
  private boolean pending() { return payment != null && payment.optString("status").equals("PENDING"); }

  // Lấy trạng thái thanh toán mới nhất khi màn hình/giao dịch còn hợp lệ.
  private void requestStatus() {
    if (isCurrent() && paymentId > 0) request("GET", "/payments/" + paymentId);
  }

  // Yêu cầu tạo hoặc thử lại giao dịch theo trạng thái hiện tại.
  private void retryPayment() {
    if (!isCurrent() || orderId <= 0) return;
    // The server reuses a valid session or creates a new one only after its own expiry check.
    request("POST", "/orders/" + orderId + "/payments");
  }

  // Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback.
  private void request(String verb, String path) {
    if (!isCurrent() || inFlight) return;
    inFlight = true;
    setBusy(true);
    handler.removeCallbacks(poll);
    final int epoch = requestEpoch;
    final long started = SystemClock.elapsedRealtime();
    api.request(verb, path, null, (data, issue, code) -> {
      if (!isCurrentScreen() || epoch != requestEpoch) return;
      if (!session.token().equals(sessionToken)) {
        stopWork();
        if (code == 401 || session.token().isEmpty()) activity.requireLogin();
        return;
      }
      inFlight = false;
      setBusy(false);
      if (issue != null) {
        if (code == 401) { stopWork(); activity.requireLogin(); return; }
        showError(issue + " Trạng thái thanh toán sẽ được kiểm tra lại; chưa có xác nhận thất bại.");
        retry.setVisibility(orderId > 0 ? View.VISIBLE : View.GONE);
      } else {
        JSONObject result = obj(data);
        if (result.optLong("id") <= 0 || result.optLong("orderId") <= 0) {
          showError("Chưa đọc được trạng thái giao dịch. Vui lòng kiểm tra lại.");
        } else if (result.optLong("id") != paymentId) {
          activity.replace("payment", result.optLong("id"));
          return;
        } else {
          acceptPayment(result, started);
        }
      }
      if (isCurrent() && (payment == null || pending())) handler.postDelayed(poll, POLL_MS);
    });
  }

  // Nhận dữ liệu thanh toán mới, cập nhật giao diện hoặc chuyển sang kết quả.
  private void acceptPayment(JSONObject result, long requestStarted) {
    payment = result;
    orderId = result.optLong("orderId");
    String state = result.optString("status");
    if (state.equals("SUCCESS")) { activity.replace("order-success", orderId); return; }
    if (state.equals("FAILED") || state.equals("EXPIRED")) {
      activity.replace("payment-failed", paymentId);
      return;
    }
    error.setVisibility(View.GONE);
    retry.setVisibility(View.GONE);
    number.setText(result.optString("orderNumber", "Đơn hàng #" + orderId));
    amount.setText(Ui.money(result.optLong("amount")));
    method.setText("Phương thức: " + result.optString("method"));
    status.setText(state.equals("PENDING") ? "Đang chờ xác nhận thanh toán" : statusLabel(state));
    instructions.setText(result.optString("instructions"));
    order.setEnabled(true);
    long serverNow = timestamp(result.optString("serverTime"));
    long serverExpiry = timestamp(result.optString("expiresAt"));
    // Monotonic time and subtracting network time keep polling from extending a QR's validity.
    deadlineElapsed = serverNow >= 0 && serverExpiry >= 0
        ? SystemClock.elapsedRealtime() + Math.max(0, serverExpiry - serverNow
            - (SystemClock.elapsedRealtime() - requestStarted)) : -1;
    expires.setText(serverExpiry >= 0 ? "Hết hạn: " + date(result.optString("expiresAt")) + " (giờ Việt Nam)" : "");
    String payload = result.optString("qrPayload").trim();
    qrError.setVisibility(View.GONE);
    if (pending() && !payload.isEmpty()) {
      try {
        if (!payload.equals(renderedQr)) {
          qr.setImageBitmap(QrCode.encode(payload));
          renderedQr = payload;
        }
        qr.setVisibility(View.VISIBLE);
      } catch (Exception invalidQr) {
        qr.setVisibility(View.GONE);
        qrError.setVisibility(View.VISIBLE);
      }
    } else qr.setVisibility(View.GONE);
    boolean isDemo = result.optBoolean("demo");
    demoNotice.setVisibility(isDemo ? View.VISIBLE : View.GONE);
    demo.setVisibility(isDemo && pending() ? View.VISIBLE : View.GONE);
    open.setVisibility(!isDemo && pending() && !result.optString("checkoutUrl").trim().isEmpty()
        ? View.VISIBLE : View.GONE);
    updateCountdown();
  }

  // Tính thời gian còn lại của giao dịch và cập nhật nhãn/hành động khi hết hạn.
  private void updateCountdown() {
    if (payment == null) return;
    if (payment.optString("method").equals("COD")) {
      countdown.setText("Thanh toán khi nhận hàng");
      return;
    }
    if (!pending()) { countdown.setText(statusLabel(payment.optString("status"))); return; }
    if (deadlineElapsed < 0) {
      countdown.setText("Đang xác minh thời hạn từ máy chủ");
      return;
    }
    long remaining = Math.max(0, deadlineElapsed - SystemClock.elapsedRealtime());
    long seconds = (remaining + 999) / 1000;
    countdown.setText(seconds > 0
        ? String.format(Locale.ROOT, "Mã QR còn hiệu lực %02d:%02d", seconds / 60, seconds % 60)
        : "00:00 · Đang xác minh trạng thái với máy chủ");
    if (remaining == 0) {
      qr.setVisibility(View.GONE);
      open.setVisibility(View.GONE);
      demo.setVisibility(View.GONE);
      retry.setVisibility(View.VISIBLE);
      if (!deadlineRecheckRequested) {
        deadlineRecheckRequested = true;
        requestStatus();
      }
    }
  }

  // Đọc thời điểm từ JSON thành giá trị dùng cho bộ đếm thanh toán.
  private static long timestamp(String value) {
    try { return Instant.parse(value).toEpochMilli(); }
    catch (RuntimeException invalid) {
      try { return OffsetDateTime.parse(value).toInstant().toEpochMilli(); }
      catch (RuntimeException ignored) { return -1; }
    }
  }

  // Đổi trạng thái bận để các nút không gửi thao tác trùng khi đang chờ API.
  private void setBusy(boolean busy) {
    for (TextView button : new TextView[] {refresh, retry, demo}) {
      button.setEnabled(!busy);
      button.setAlpha(busy ? .55f : 1f);
    }
  }

  // Đưa lỗi vào vùng trạng thái và cập nhật thao tác phù hợp của giao diện.
  private void showError(String message) {
    error.setText(message);
    error.setVisibility(View.VISIBLE);
  }

  // Kiểm tra URL thanh toán và mở trình xử lý liên kết của thiết bị.
  private void openGateway() {
    if (!isCurrent() || !pending() || payment.optBoolean("demo")
        || (deadlineElapsed >= 0 && SystemClock.elapsedRealtime() >= deadlineElapsed)) return;
    Uri uri = Uri.parse(payment.optString("checkoutUrl"));
    if (!Arrays.asList("https", "momo", "zalopay").contains(uri.getScheme())) {
      alert("Liên kết chưa hợp lệ", "Vui lòng liên hệ NOVA để được hỗ trợ thanh toán.");
      return;
    }
    try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
    catch (RuntimeException unavailable) { alert("Không mở được thanh toán", "Thiết bị chưa có ứng dụng phù hợp."); }
  }
}

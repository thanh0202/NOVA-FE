package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị mã đơn, số tiền và phương thức. Mở chi tiết hoặc quay lại mua sắm.
 * Bố cục liên quan: screen_order_success.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;
import com.example.nova.*;

/** Figma 04.09 · Đặt hàng thành công. Order ID is always loaded from the server. */
public final class OrderSuccessScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public OrderSuccessScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_order_success; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = bindBody(layoutId());
    final long orderId = s.entityId;
    root.findViewById(R.id.order_success_detail).setOnClickListener(v -> go("order", orderId));
    root.findViewById(R.id.order_success_shopping).setOnClickListener(v -> tab("home"));
    get("/orders/" + orderId, data -> {
      JSONObject order = obj(data);
      ((TextView) root.findViewById(R.id.order_success_number)).setText(order.optString("number"));
      ((TextView) root.findViewById(R.id.order_success_total)).setText(Ui.money(order.optLong("total")));
      ((TextView) root.findViewById(R.id.order_success_method)).setText(
          order.optString("method").equals("COD") ? "Thanh toán khi nhận hàng" : "Thanh toán trực tuyến");
      TextView title = root.findViewById(R.id.order_success_title);
      TextView message = root.findViewById(R.id.order_success_message);
      if (order.optString("status").equals("CANCELLED")) {
        title.setText("Đơn hàng đã hủy");
        message.setText("Đơn hàng này đã được hủy. Xem chi tiết để kiểm tra trạng thái hoàn tiền.");
      } else if (!order.optString("method").equals("COD")
          && !order.optString("paymentStatus").equals("PAID")) {
        title.setText("Đã tiếp nhận đơn hàng");
        message.setText("Trạng thái thanh toán: " + statusLabel(order.optString("paymentStatus"))
            + ". Bạn có thể kiểm tra và tiếp tục thanh toán trong chi tiết đơn hàng.");
      }
    });
  }
}

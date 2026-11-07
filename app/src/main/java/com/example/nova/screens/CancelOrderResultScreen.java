package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp trạng thái sau khi hủy và thông tin hoàn tiền nếu có.
 * Bố cục liên quan: screen_cancel_order_result.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.nova.*;

/** Figma 05.12 · Kết quả hủy đơn đã thanh toán; never assumes money was refunded. */
public final class CancelOrderResultScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public CancelOrderResultScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_cancel_order_result; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = bindBody(layoutId());
    final long orderId = s.entityId;
    root.findViewById(R.id.cancel_result_detail).setOnClickListener(v -> go("order", orderId));
    root.findViewById(R.id.cancel_result_refunds).setOnClickListener(v -> go("returns"));
    root.findViewById(R.id.cancel_result_home).setOnClickListener(v -> tab("home"));
    get("/orders/" + orderId, data -> {
      JSONObject order = obj(data);
      ((TextView) root.findViewById(R.id.cancel_result_number)).setText(order.optString("number"));
      TextView title = root.findViewById(R.id.cancel_result_title);
      TextView message = root.findViewById(R.id.cancel_result_message);
      if (!order.optString("status").equals("CANCELLED")) {
        title.setText("Đơn hàng chưa được hủy");
        message.setText("Trạng thái hiện tại: " + statusLabel(order.optString("status"))
            + ". Vui lòng kiểm tra chi tiết đơn hàng.");
        root.findViewById(R.id.cancel_result_refunds).setVisibility(View.GONE);
        return;
      }
      title.setText("Đã hủy đơn hàng");
      String paymentStatus = order.optString("paymentStatus");
      message.setText(paymentStatus.equals("REFUND_PENDING")
          ? "Đã tạo yêu cầu hoàn tiền. Khoản tiền chưa được xác nhận hoàn tất; bạn có thể theo dõi tiến trình bên dưới."
          : paymentStatus.equals("REFUNDED") ? "Máy chủ đã xác nhận hoàn tiền cho đơn hàng."
          : "Đơn hàng đã được hủy. Trạng thái thanh toán: " + statusLabel(paymentStatus));
      ((TextView) root.findViewById(R.id.cancel_result_amount)).setText(
          "Đã hoàn: " + Ui.money(order.optLong("refundedAmount")));
      get("/refunds", result -> {
        JSONArray refunds = arr(result);
        for (int i = 0; i < refunds.length(); i++) {
          JSONObject refund = refunds.optJSONObject(i);
          if (refund == null || refund.optLong("orderId") != orderId) continue;
          LinearLayout card = u.card();
          u.add(card, u.text("Yêu cầu hoàn tiền #" + refund.optLong("id"), 14, Ui.INK, true));
          u.space(card, 8);
          u.add(card, u.text(Ui.money(refund.optLong("amount")), 18, Ui.BRAND, true));
          u.space(card, 8);
          u.add(card, u.text(statusLabel(refund.optString("status")), 12));
          u.space(card, 6);
          u.add(card, u.text(refund.optString("reason"), 12, Ui.MUTED, false));
          u.add(content, card);
          gap();
        }
      });
    });
  }
}

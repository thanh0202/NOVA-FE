package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Kiểm tra trạng thái, nhận lý do và gửi yêu cầu hủy.
 * Bố cục liên quan: screen_cancel_order.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Arrays;
import org.json.JSONObject;
import com.example.nova.*;

/** Figma 05.11 · Hủy đơn đã thanh toán. Refund creation is owned by the backend. */
public final class CancelOrderScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public CancelOrderScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_cancel_order; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = bindBody(layoutId());
    final long orderId = s.entityId;
    TextView submit = root.findViewById(R.id.cancel_order_submit);
    submit.setEnabled(false);
    root.findViewById(R.id.cancel_order_keep).setOnClickListener(v -> go("order", orderId));
    RadioGroup reasons = root.findViewById(R.id.cancel_order_reasons);
    EditText other = root.findViewById(R.id.cancel_order_reason);
    other.setVisibility(View.GONE);
    reasons.setOnCheckedChangeListener((group, checked) -> {
      other.setVisibility(checked == R.id.cancel_order_other ? View.VISIBLE : View.GONE);
      if (checked != R.id.cancel_order_other) other.setError(null);
    });
    get("/orders/" + orderId, data -> {
      JSONObject order = obj(data);
      ((TextView) root.findViewById(R.id.cancel_order_number)).setText(order.optString("number"));
      ((TextView) root.findViewById(R.id.cancel_order_total)).setText(Ui.money(order.optLong("total")));
      ((TextView) root.findViewById(R.id.cancel_order_payment)).setText(
          "Trạng thái thanh toán: " + statusLabel(order.optString("paymentStatus")));
      if (!Arrays.asList("AWAITING_CONFIRMATION", "PROCESSING").contains(order.optString("status"))) {
        ((TextView) root.findViewById(R.id.cancel_order_notice)).setText(
            "Đơn hàng hiện không thể hủy. Vui lòng xem trạng thái mới nhất hoặc liên hệ hỗ trợ.");
        return;
      }
      submit.setEnabled(true);
      submit.setOnClickListener(v -> {
        int checked = reasons.getCheckedRadioButtonId();
        if (checked == -1) { toast("Vui lòng chọn lý do hủy đơn"); return; }
        String reason = checked == R.id.cancel_order_other ? other.getText().toString().trim()
            : ((RadioButton) root.findViewById(checked)).getText().toString();
        if (reason.isEmpty()) { other.setError("Vui lòng nhập lý do hủy đơn"); return; }
        confirm("Xác nhận hủy đơn hàng", "Bạn muốn hủy đơn " + order.optString("number")
            + "? Khoản thanh toán sẽ được xử lý hoàn tiền theo trạng thái thực tế.", () -> {
          submit.setEnabled(false);
          int generation = s.generation;
          api.request("POST", "/orders/" + orderId + "/cancel", json("reason", reason),
              (result, error, status) -> {
                if (generation != s.generation || isFinishing() || isDestroyed()) return;
                submit.setEnabled(true);
                if (error != null) {
                  if (status == 401) { s.afterLogin = "cancel-order"; s.afterLoginId = orderId; go("login"); }
                  else alert("Chưa thể hủy đơn", error);
                  return;
                }
                go("cancel-order-result", orderId);
              });
        });
      });
    });
  }
}

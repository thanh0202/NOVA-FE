package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị mã vận đơn, địa chỉ và lịch sử trạng thái vận chuyển.
 * Bố cục liên quan: screen_order_tracking.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.nova.*;

/** Figma 05.10 · Theo dõi giao hàng; shows the server's order history. */
public final class OrderTrackingScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public OrderTrackingScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_order_tracking; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = bindBody(layoutId());
    final long orderId = s.entityId;
    root.findViewById(R.id.order_tracking_refresh).setOnClickListener(v -> activity.render());
    root.findViewById(R.id.order_tracking_detail).setOnClickListener(v -> go("order", orderId));
    get("/orders/" + orderId, data -> {
      JSONObject order = obj(data);
      ((TextView) root.findViewById(R.id.order_tracking_number)).setText(order.optString("number"));
      ((TextView) root.findViewById(R.id.order_tracking_status)).setText(statusLabel(order.optString("status")));
      String tracking = order.optString("trackingNumber").trim();
      ((TextView) root.findViewById(R.id.order_tracking_waybill)).setText(
          tracking.isEmpty() ? "Mã vận đơn sẽ được cập nhật khi bàn giao cho đơn vị vận chuyển."
              : "Mã vận đơn: " + tracking);
      JSONObject address = order.optJSONObject("address");
      ((TextView) root.findViewById(R.id.order_tracking_address)).setText(address == null
          ? "Chưa có thông tin địa chỉ." : address.optString("name") + " · " + address.optString("phone")
              + "\n" + formatAddress(address));
      JSONArray history = order.optJSONArray("history");
      timeline(content, history);
    });
  }
}

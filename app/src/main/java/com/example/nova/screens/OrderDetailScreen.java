package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị thông tin đơn và mở các thao tác phù hợp với trạng thái như thanh toán, theo dõi, hủy, đổi trả hoặc viết đánh giá.
 * Bố cục liên quan: screen_order_detail.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.app.AlertDialog;
import android.widget.LinearLayout;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.nova.*;

/** Figma 05.05–05.09: one order detail page, with actions for its current status. */
public final class OrderDetailScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public OrderDetailScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_order_detail; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    bindBody(layoutId());
    final long orderId = s.entityId;
    get("/orders/" + orderId, data -> {
      JSONObject order = obj(data);
      String status = order.optString("status");
      LinearLayout state = u.card();
      state.setBackground(u.bg(Ui.TINT, 20));
      u.add(state, u.text(statusLabel(status), 22, Ui.BRAND, true));
      u.space(state, 8);
      u.add(state, u.text("Đơn hàng " + order.optString("number"), 12));
      u.space(state, 7);
      u.add(state, u.text(date(order.optString("createdAt")), 11, Ui.MUTED, false));
      u.add(content, state);
      gap();
      JSONObject address = order.optJSONObject("address");
      if (address != null) {
        LinearLayout card = u.card();
        u.add(card, u.text("Thông tin nhận hàng", 14, Ui.INK, true));
        u.space(card, 10);
        u.add(card, u.text(address.optString("name") + " · " + address.optString("phone")
            + "\n" + formatAddress(address), 12));
        u.add(content, card);
        gap();
      }
      section("Sản phẩm đã đặt", null);
      JSONArray items = order.optJSONArray("items");
      if (items != null) for (int i = 0; i < items.length(); i++) {
        JSONObject item = items.optJSONObject(i);
        LinearLayout card = u.card();
        u.add(card, u.text(item.optString("name"), 14, Ui.INK, true));
        u.space(card, 6);
        u.add(card, u.text(item.optString("variantName") + " · Số lượng "
            + item.optInt("quantity"), 11, Ui.MUTED, false));
        u.space(card, 8);
        u.add(card, u.text(Ui.money(item.optLong("price")), 14, Ui.BRAND, true));
        if (status.equals("DELIVERED"))
          u.add(card, u.link("Viết đánh giá", () -> reviewForm(item.optLong("id"), null)));
        card.setOnClickListener(v -> go("product", item.optLong("productId")));
        u.add(content, card);
        gap();
      }
      LinearLayout summary = u.card();
      u.labelPair(summary, "Tạm tính", Ui.money(order.optLong("subtotal")), false);
      u.labelPair(summary, "Giảm giá", "−" + Ui.money(order.optLong("discount")), false);
      u.labelPair(summary, "Phí vận chuyển", Ui.money(order.optLong("shippingFee")), false);
      u.labelPair(summary, "Tổng thanh toán", Ui.money(order.optLong("total")), true);
      u.add(summary, u.text("Phương thức: " + order.optString("method")
          + "\nTrạng thái thanh toán: " + statusLabel(order.optString("paymentStatus")),
          12, Ui.MUTED, false));
      u.add(content, summary);
      gap();
      u.add(content, u.button("Theo dõi giao hàng", false, () -> go("order-tracking", orderId)));
      if (Arrays.asList("AWAITING_CONFIRMATION", "PROCESSING").contains(status)) {
        gap();
        boolean paid = Arrays.asList("PAID", "SUCCEEDED", "SUCCESS").contains(order.optString("paymentStatus"));
        u.add(content, u.button("Hủy đơn hàng", false,
            () -> { if (paid) go("cancel-order", orderId); else cancelUnpaid(orderId); }));
      }
      if (!order.optString("method").equals("COD") && !status.equals("CANCELLED")
          && !Arrays.asList("PAID", "SUCCEEDED", "SUCCESS", "REFUND_PENDING", "REFUNDED")
              .contains(order.optString("paymentStatus"))) {
        gap();
        u.add(content, u.button("Tiếp tục thanh toán", true,
            () -> send("POST", "/orders/" + orderId + "/payments", null,
                payment -> go("payment", obj(payment).optLong("id")))));
      }
      if (status.equals("DELIVERED")) {
        gap();
        u.add(content, u.button("Yêu cầu đổi trả / hoàn tiền", false,
            () -> go("return-request", orderId)));
      }
      if (order.optLong("refundedAmount") > 0 || order.optString("paymentStatus").contains("REFUND")) {
        gap();
        u.add(content, u.button("Theo dõi hoàn tiền", false, () -> go("returns")));
      }
      gap();
      u.add(content, u.button("Làm mới trạng thái", false, activity::render));
    });
  }

  /** Unpaid cancellation remains a small confirmation dialog (Figma D06/D07). */
  // Xác nhận và gửi hủy đơn chưa thanh toán, sau đó mở kết quả từ API.
  private void cancelUnpaid(long orderId) {
    LinearLayout form = u.col();
    u.pad(form, 20);
    android.widget.EditText reason = u.field(form, "Lý do hủy đơn", "", TEXT);
    reason.setSingleLine(false);
    reason.setMinLines(2);
    reason.setFilters(new android.text.InputFilter[] {new android.text.InputFilter.LengthFilter(500)});
    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Hủy đơn hàng")
        .setView(form).setNegativeButton("Giữ đơn hàng", null)
        .setPositiveButton("Xác nhận hủy", null).create();
    dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(v -> {
          String value = reason.getText().toString().trim();
          if (value.isEmpty()) { reason.setError("Vui lòng nhập lý do"); return; }
          send("POST", "/orders/" + orderId + "/cancel", json("reason", value), data -> {
            dialog.dismiss();
            toast("Đã hủy đơn hàng");
            activity.render();
          });
        }));
    dialog.show();
  }
}
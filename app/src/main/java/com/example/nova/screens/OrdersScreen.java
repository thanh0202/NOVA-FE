package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp danh sách đơn và lọc theo trạng thái.
 * Bố cục liên quan: screen_orders.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

/** Đơn hàng dùng trạng thái và dữ liệu chụp tại thời điểm đặt hàng từ API. */
public final class OrdersScreen extends BaseScreen {
  private static final String[] STATUS = {"", "AWAITING_CONFIRMATION", "PROCESSING", "SHIPPING", "DELIVERED", "CANCELLED"};
  private static final String[] LABELS = {"Tất cả", "Chờ xác nhận", "Đang chuẩn bị", "Đang giao", "Đã giao", "Đã hủy"};

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public OrdersScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_orders; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = screen(layoutId());
    LinearLayout tabs = root.findViewById(R.id.orders_tabs);
    LinearLayout results = root.findViewById(R.id.orders_list);
    root.findViewById(R.id.orders_refresh).setOnClickListener(v -> activity.render());
    get("/orders", data -> {
      JSONArray orders = arr(data);
      showTabs(tabs, results, orders);
      showOrders(results, orders);
    });
  }

  // Dựng các tab trạng thái đơn hàng và cập nhật bộ lọc khi chọn.
  private void showTabs(LinearLayout tabs, LinearLayout results, JSONArray orders) {
    tabs.removeAllViews();
    for (int i = 0; i < STATUS.length; i++) {
      String filter = STATUS[i];
      TextView chip = u.chip(LABELS[i], filter.equals(s.orderFilter), () -> {
        s.orderFilter = filter;
        showTabs(tabs, results, orders);
        showOrders(results, orders);
      });
      chip.setSelected(filter.equals(s.orderFilter));
      chip.setTag("order-tab-" + filter);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, u.d(42));
      params.rightMargin = u.d(8);
      tabs.addView(chip, params);
      if (chip.isSelected()) chip.post(() -> chip.requestRectangleOnScreen(
          new android.graphics.Rect(0, 0, chip.getWidth(), chip.getHeight()), true));
    }
  }

  // Dựng danh sách hoặc các lối truy cập đơn hàng từ dữ liệu hiện tại.
  private void showOrders(LinearLayout list, JSONArray orders) {
    list.removeAllViews();
    String previousMonth = "";
    for (int i = 0; i < orders.length(); i++) {
      JSONObject order = orders.optJSONObject(i);
      if (order == null || (!s.orderFilter.isEmpty() && !s.orderFilter.equals(order.optString("status")))) continue;
      String month = month(order.optString("createdAt"));
      if (!month.equals(previousMonth)) {
        u.add(list, u.text(month, 13, Ui.MUTED, true));
        u.space(list, 10);
        previousMonth = month;
      }
      u.add(list, orderCard(order));
      u.space(list, 16);
    }
    if (list.getChildCount() == 0) {
      LinearLayout empty = u.card();
      u.add(empty, u.text("Chưa có đơn hàng", 16, Ui.INK, true));
      u.space(empty, 8);
      u.add(empty, u.text(s.orderFilter.isEmpty() ? "Đơn hàng của bạn sẽ xuất hiện ở đây."
          : "Chưa có đơn ở trạng thái này.", 12, Ui.MUTED, false));
      u.add(list, empty);
    }
  }

  // Dựng một thẻ đơn hàng với mã, trạng thái, món hàng và tổng tiền.
  private LinearLayout orderCard(JSONObject order) {
    LinearLayout card = u.card();
    long id = order.optLong("id");
    String status = order.optString("status");
    card.setTag("order-card-" + id);
    LinearLayout heading = u.row();
    u.weight(heading, u.text(order.optString("number"), 13, Ui.INK, true));
    TextView pill = u.text(statusLabel(status), 10, Ui.BRAND, true);
    pill.setPadding(u.d(9), u.d(7), u.d(9), u.d(7));
    pill.setBackground(u.bg(Ui.TINT, 20));
    heading.addView(pill);
    u.add(card, heading);
    u.space(card, 7);
    u.add(card, u.text(date(order.optString("createdAt")), 11, Ui.MUTED, false));
    u.space(card, 13);
    JSONArray items = order.optJSONArray("items");
    if (items != null) {
      for (int i = 0; i < Math.min(2, items.length()); i++) {
        JSONObject item = items.optJSONObject(i);
        if (item != null) u.add(card, orderItem(item));
      }
      if (items.length() > 2) u.add(card, u.text("+ " + (items.length() - 2) + " sản phẩm khác", 11, Ui.MUTED, false));
    }
    addProgress(card, status);
    u.space(card, 13);
    u.labelPair(card, "Tổng thanh toán", Ui.money(order.optLong("total")), true);
    u.add(card, u.button("Xem chi tiết  →", false, () -> go("order", id)));
    u.action(card, () -> go("order", id));
    return card;
  }

  // Dựng một dòng sản phẩm trong thẻ đơn hàng.
  private LinearLayout orderItem(JSONObject item) {
    LinearLayout row = u.row();
    row.setPadding(0, u.d(5), 0, u.d(5));
    ImageView photo = new ImageView(this);
    photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
    photo.setBackground(u.bg(0xFFF0F4FA, 10));
    photo.setPadding(u.d(4), u.d(4), u.d(4), u.d(4));
    images.product(photo, item.optString("imageUrl"), item.optLong("productId"));
    row.addView(photo, new LinearLayout.LayoutParams(u.d(50), u.d(52)));
    LinearLayout labels = u.col();
    labels.setPadding(u.d(10), 0, 0, 0);
    u.add(labels, u.text(item.optString("name"), 12, Ui.INK, true));
    u.space(labels, 4);
    String variant = item.optString("variantName", "");
    u.add(labels, u.text((variant.isBlank() ? "" : variant + " · ") + "× " + item.optInt("quantity"), 10, Ui.MUTED, false));
    u.weight(row, labels);
    return row;
  }

  // Thêm bước tiến độ theo trạng thái đơn.
  private void addProgress(LinearLayout card, String status) {
    int step = switch (status) {
      case "AWAITING_CONFIRMATION" -> 0;
      case "PROCESSING" -> 1;
      case "SHIPPING" -> 2;
      case "DELIVERED" -> 3;
      default -> -1;
    };
    if (step < 0) return;
    u.space(card, 12);
    LinearLayout row = u.row();
    String[] labels = {"Đã đặt", "Chuẩn bị", "Đang giao", "Đã giao"};
    for (int i = 0; i < labels.length; i++) {
      TextView label = u.text((i <= step ? "●\n" : "○\n") + labels[i], 10, i <= step ? Ui.BLUE : Ui.MUTED, i == step);
      label.setGravity(Gravity.CENTER);
      label.setPadding(0, u.d(7), 0, u.d(7));
      u.weight(row, label);
    }
    row.setBackground(u.bg(0xFFF0F5FD, 12));
    u.add(card, row);
  }

  // Định dạng tháng của thời điểm tạo đơn để nhóm/hiển thị lịch sử.
  private String month(String raw) {
    try {
      ZonedDateTime value = Instant.parse(raw).atZone(ZoneId.of("Asia/Ho_Chi_Minh"));
      return "Tháng " + value.getMonthValue() + ", " + value.getYear();
    } catch (RuntimeException invalid) { return "Đơn hàng"; }
  }
}

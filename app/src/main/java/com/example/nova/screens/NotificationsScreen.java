package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp danh sách, cập nhật đã đọc và mở đích liên quan khi có dữ liệu điều hướng.
 * Bố cục liên quan: screen_notifications.xml.
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

/** Customer screen: notifications; its layout and behavior are edited together. */
public final class NotificationsScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public NotificationsScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_notifications; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (BuildConfig.NOVA_MIDTERM_API) {
      bindBody(layoutId());
      empty("Thông báo", "Các cập nhật mua sắm sẽ xuất hiện tại đây khi tính năng được hoàn thiện.");
      u.add(content, u.button("Khám phá sản phẩm", false, () -> tab("categories")));
      return;
    }
    if (!requireLogin()) return;
    LinearLayout layout = bindBody(layoutId());
    layout.findViewById(R.id.notifications_refresh).setOnClickListener(v -> activity.render());
    get("/notifications", data -> {
      JSONArray list = arr(data);
      if (list.length() == 0) empty("Bạn đã xem hết thông báo",
          "Ưu đãi và cập nhật đơn hàng mới sẽ xuất hiện tại đây.");
      for (int i = 0; i < list.length(); i++) {
        JSONObject n = list.optJSONObject(i);
        LinearLayout card = u.card();
        if (!n.optBoolean("read")) card.setBackground(u.bg(Ui.TINT, 20));
        u.add(card, u.text((n.optBoolean("read") ? "" : "●  ") + n.optString("title"), 14, Ui.INK, true));
        u.space(card, 8);
        u.add(card, u.text(n.optString("body"), 12));
        u.space(card, 10);
        u.add(card, u.text(date(n.optString("createdAt")), 10, Ui.MUTED, false));
        card.setOnClickListener(v -> send("POST", "/notifications/" + n.optLong("id") + "/read", null, d -> {
          if (n.optLong("orderId") > 0) go("order", n.optLong("orderId"));
          else activity.render();
        }));
        u.add(content, card);
        gap();
      }
    });
  }
}

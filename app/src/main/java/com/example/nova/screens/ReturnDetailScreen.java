package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị lý do, trạng thái, tiến trình và lối mở đơn liên quan.
 * Bố cục liên quan: screen_return_detail.xml.
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

/** Customer screen: return; its layout and behavior are edited together. */
public final class ReturnDetailScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ReturnDetailScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_return_detail; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    bindBody(R.layout.screen_return_detail);
    get(
        "/returns/" + s.entityId,
        data -> {
          JSONObject r = obj(data);
          LinearLayout c = u.card();
          u.add(c, u.text(r.optString("orderNumber"), 20, Ui.INK, true));
          u.space(c, 10);
          u.add(c, u.text(statusLabel(r.optString("status")), 15, Ui.BRAND, true));
          u.space(c, 10);
          u.add(c, u.text(r.optString("reason"), 13));
          u.add(content, c);
          gap();
          section("Tiến trình xử lý", null);
          timeline(content, r.optJSONArray("history"));
          u.add(content, u.button("Làm mới yêu cầu", false, activity::render));
          gap();
          u.add(content, u.button("Xem đơn hàng", true, () -> go("order", r.optLong("orderId"))));
        });
  }
}

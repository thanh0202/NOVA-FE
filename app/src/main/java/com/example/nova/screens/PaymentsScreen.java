package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp giao dịch, số tiền, trạng thái và mở chi tiết từng giao dịch.
 * Bố cục liên quan: screen_payment_history.xml.
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

/** Customer screen: payments; its layout and behavior are edited together. */
public final class PaymentsScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public PaymentsScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_payment_history; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    bindBody(R.layout.screen_payment_history);
    get(
        "/payments",
        data -> {
          JSONArray list = arr(data);
          if (list.length() == 0)
            empty(
                "Chưa có giao dịch",
                "Các giao dịch thanh toán trực tuyến của bạn sẽ xuất hiện tại đây.");
          for (int i = 0; i < list.length(); i++) {
            JSONObject p = list.optJSONObject(i);
            LinearLayout c = u.card();
            u.add(c, u.text(p.optString("orderNumber"), 15, Ui.INK, true));
            u.space(c, 8);
            u.labelPair(c, p.optString("method"), Ui.money(p.optLong("amount")), false);
            u.add(
                c,
                u.text(
                    statusLabel(p.optString("status")) + " · " + date(p.optString("createdAt")),
                    11,
                    Ui.BRAND,
                    true));
            u.add(c, u.link("Chi tiết giao dịch  →", () -> go("payment", p.optLong("id"))));
            u.add(content, c);
            gap();
          }
        });
  }
}

package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Liệt kê yêu cầu đổi trả và các khoản hoàn tiền.
 * Bố cục liên quan: screen_returns.xml.
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

/** Customer screen: returns; its layout and behavior are edited together. */
public final class ReturnsScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ReturnsScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_returns; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    bindBody(R.layout.screen_returns);
    section("Yêu cầu đổi trả", null);
    get(
        "/returns",
        data -> {
          JSONArray list = arr(data);
          if (list.length() == 0)
            u.add(content, u.text("Bạn chưa có yêu cầu đổi trả.", 13, Ui.MUTED, false));
          for (int i = 0; i < list.length(); i++) {
            JSONObject r = list.optJSONObject(i);
            LinearLayout c = u.card();
            u.add(c, u.text(r.optString("orderNumber"), 15, Ui.INK, true));
            u.space(c, 8);
            u.add(
                c,
                u.text(
                    (r.optString("type").equals("EXCHANGE")
                            ? "Đổi sản phẩm"
                            : "Trả hàng / hoàn tiền")
                        + " · "
                        + statusLabel(r.optString("status")),
                    12,
                    Ui.BRAND,
                    true));
            u.space(c, 8);
            u.add(c, u.text(r.optString("reason"), 12));
            u.add(c, u.link("Theo dõi yêu cầu", () -> go("return", r.optLong("id"))));
            u.add(content, c);
            gap();
          }
          gap();
          section("Lịch sử hoàn tiền", null);
          get(
              "/refunds",
              refunds -> {
                JSONArray records = arr(refunds);
                if (records.length() == 0)
                  u.add(content, u.text("Chưa có khoản hoàn tiền.", 12, Ui.MUTED, false));
                for (int i = 0; i < records.length(); i++) {
                  JSONObject r = records.optJSONObject(i);
                  LinearLayout c = u.card();
                  u.add(
                      c,
                      u.text(
                          "Hoàn tiền #" + r.optLong("id") + " · " + Ui.money(r.optLong("amount")),
                          14,
                          Ui.INK,
                          true));
                  u.space(c, 8);
                  u.add(c, u.text("Đơn hàng: " + r.optString("orderNumber"), 12));
                  u.space(c, 8);
                  u.add(
                      c,
                      u.text(
                          statusLabel(r.optString("status")) + "\n" + r.optString("reason"),
                          12,
                          Ui.MUTED,
                          false));
                  u.add(content, c);
                  gap();
                }
              });
        });
  }
}

package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị ngưỡng điểm và quyền lợi theo từng hạng.
 * Bố cục liên quan: screen_membership_tiers.xml.
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

/** Customer screen: tiers; its layout and behavior are edited together. */
public final class TiersScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public TiersScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_membership_tiers; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    bindBody(R.layout.screen_membership_tiers);
    get(
        "/membership/tiers",
        data -> {
          JSONArray tiers = arr(data);
          for (int i = 0; i < tiers.length(); i++) {
            JSONObject tier = tiers.optJSONObject(i);
            LinearLayout c = u.card();
            u.add(c, u.text(tier.optString("name"), 24, Ui.BRAND, true));
            u.space(c, 10);
            u.add(
                c,
                u.text(
                    "Từ "
                        + tier.optLong("threshold")
                        + " điểm · Ưu đãi "
                        + tier.optDouble("discount")
                        + "%",
                    12,
                    Ui.MUTED,
                    false));
            u.space(c, 14);
            Object benefits = tier.opt("benefits");
            if (benefits instanceof JSONArray list)
              for (int j = 0; j < list.length(); j++) {
                u.add(c, u.text("✓  " + list.optString(j), 13));
                u.space(c, 8);
              }
            else u.add(c, u.text(tier.optString("benefits"), 13));
            u.add(content, c);
            gap();
          }
        });
  }
}

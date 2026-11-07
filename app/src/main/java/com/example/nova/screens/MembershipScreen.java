package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp hạng, điểm tích lũy, tiến độ lên hạng và lịch sử điểm.
 * Bố cục liên quan: screen_membership.xml.
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

/** Customer screen: membership; its layout and behavior are edited together. */
public final class MembershipScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public MembershipScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_membership; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    final LinearLayout layout = screen(R.layout.screen_membership);
    get("/membership", data -> {
      JSONObject m = obj(data);
      ((TextView) layout.findViewById(R.id.membership_tier)).setText(m.optString("tier"));
      ((TextView) layout.findViewById(R.id.membership_points)).setText(m.optLong("points") + " điểm tích lũy");
      layout.findViewById(R.id.membership_card).setOnClickListener(v -> go("tiers"));
      layout.findViewById(R.id.membership_benefits).setOnClickListener(v -> go("tiers"));
      long threshold = m.optLong("nextThreshold"), qualifying = m.optLong("qualifyingPoints");
      ((TextView) layout.findViewById(R.id.membership_next_tier)).setText(threshold > 0
          ? "Hạng tiếp theo: " + m.optString("nextTier") : "Bạn đang ở hạng cao nhất");
      ((ProgressBar) layout.findViewById(R.id.membership_progress)).setProgress(threshold > 0
          ? (int) Math.min(1000, qualifying * 1000 / threshold) : 1000);
      ((TextView) layout.findViewById(R.id.membership_qualifying)).setText(threshold > 0
          ? qualifying + " / " + threshold + " điểm xét hạng" : qualifying + " điểm xét hạng");
      LinearLayout history = layout.findViewById(R.id.membership_history);
      JSONArray list = m.optJSONArray("history");
      if (list == null || list.length() == 0) u.add(history,
          u.text("Điểm được ghi nhận khi đơn hàng đáp ứng điều kiện.", 12, Ui.MUTED, false));
      else for (int i = 0; i < list.length(); i++) {
        JSONObject h = list.optJSONObject(i); LinearLayout c = u.card();
        u.labelPair(c, h.optString("description"), (h.optInt("points") > 0 ? "+" : "")
            + h.optInt("points") + " điểm", false);
        u.add(c, u.text(date(h.optString("createdAt")), 10, Ui.MUTED, false));
        if (h.optLong("orderId") > 0)
          u.add(c, u.link("Xem đơn hàng liên quan", () -> go("order", h.optLong("orderId"))));
        u.add(history, c); u.space(history, 16);
      }
    });
  }
}

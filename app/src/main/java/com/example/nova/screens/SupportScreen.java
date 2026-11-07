package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp bài hướng dẫn, mở nội dung và điều hướng tới chat.
 * Bố cục liên quan: screen_support.xml.
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

/** Customer screen: support; its layout and behavior are edited together. */
public final class SupportScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public SupportScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_support; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    final LinearLayout layout = screen(R.layout.screen_support);
    layout.findViewById(R.id.support_chat).setOnClickListener(v -> go("chat"));
    content = layout.findViewById(R.id.support_articles);
    get(
        "/support/articles",
        data -> {
          JSONArray list = arr(data);
          for (int i = 0; i < list.length(); i++) {
            JSONObject article = list.optJSONObject(i);
            LinearLayout c = u.card();
            u.add(c, u.text(article.optString("title") + "   +", 14, Ui.INK, true));
            TextView body = u.text(article.optString("body"), 13, Ui.MUTED, false);
            body.setPadding(0, u.d(12), 0, 0);
            body.setVisibility(View.GONE);
            u.add(c, body);
            c.setOnClickListener(
                v ->
                    body.setVisibility(
                        body.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
            u.add(content, c);
            gap();
          }
        });
  }
}

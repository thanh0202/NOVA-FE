package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp lịch sử, gửi tin nhắn và làm mới hội thoại.
 * Bố cục liên quan: screen_chat.xml.
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

/** Customer screen: chat; its layout and behavior are edited together. */
public final class ChatScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ChatScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_chat; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    LinearLayout layout = bindBody(layoutId());
    get("/support/messages", data -> {
      JSONArray list = arr(data);
      if (list.length() == 0) u.add(content,
          u.text("Xin chào! NOVA có thể hỗ trợ bạn điều gì?", 14, Ui.MUTED, false));
      for (int i = 0; i < list.length(); i++) {
        JSONObject m = list.optJSONObject(i);
        LinearLayout card = u.card();
        boolean mine = !Arrays.asList("ADMIN", "STAFF", "SUPPORT", "NOVA")
            .contains(m.optString("sender").toUpperCase(Locale.ROOT));
        card.setBackground(u.bg(mine ? Ui.TINT : Color.WHITE, 18));
        u.add(card, u.text(m.optString("sender"), 10, Ui.BRAND, true));
        u.space(card, 6);
        u.add(card, u.text(m.optString("body"), 13));
        u.space(card, 8);
        u.add(card, u.text(date(m.optString("createdAt")), 10, Ui.MUTED, false));
        u.add(content, card);
        u.space(content, 10);
      }
    });
    EditText message = layout.findViewById(R.id.chat_message);
    layout.findViewById(R.id.chat_send).setOnClickListener(v -> {
      String value = message.getText().toString().trim();
      if (value.isEmpty()) { message.setError("Nhập tin nhắn"); return; }
      send("POST", "/support/messages", json("body", value), d -> activity.render());
    });
    layout.findViewById(R.id.chat_refresh).setOnClickListener(v -> activity.render());
  }
}

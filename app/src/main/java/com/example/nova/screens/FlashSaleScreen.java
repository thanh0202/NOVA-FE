package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp khung giờ, giá, số lượng còn lại, đếm ngược và xử lý nhắc ưu đãi.
 * Bố cục liên quan: component_flash_banner.xml, screen_flash_sale.xml.
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

/** Customer screen: flash; its layout and behavior are edited together. */
public final class FlashSaleScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public FlashSaleScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_flash_sale; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    final LinearLayout layout = screen(R.layout.screen_flash_sale);
    get("/flash-sales", data -> {
      JSONArray sales = arr(data);
      if (sales.length() == 0) {
        layout.setVisibility(View.GONE);
        empty("Hẹn bạn ở khung giờ tiếp theo", "Các ưu đãi mới sẽ được cập nhật tại đây."); return;
      }
      LinearLayout tabs = layout.findViewById(R.id.flash_tabs);
      LinearLayout detail = layout.findViewById(R.id.flash_detail);
      for (int i = 0; i < sales.length(); i++) {
        JSONObject sale = sales.optJSONObject(i);
        TextView slot = u.chip(sale.optString("name"), i == 0, () -> flashSlot(detail, sale));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, u.d(44));
        lp.rightMargin = u.d(8); tabs.addView(slot, lp);
      }
      flashSlot(detail, sales.optJSONObject(0));
    });
  }

  // Dựng một khung giờ Flash Sale gồm sản phẩm, thời hạn và thao tác liên quan.
  private void flashSlot(LinearLayout parent, JSONObject sale) {
    parent.removeAllViews();
    LinearLayout banner = inflateLayout(R.layout.component_flash_banner, parent);
    ((TextView) banner.findViewById(R.id.flash_banner_name)).setText(sale.optString("name"));
    TextView timer = banner.findViewById(R.id.flash_banner_timer);
    TextView seconds = banner.findViewById(R.id.flash_banner_seconds);
    u.add(parent, banner);
    u.space(parent, 16);
    startCountdown(timer, seconds, sale);
    JSONArray list = sale.optJSONArray("items");
    if (list == null) return;
    for (int i = 0; i < list.length(); i++) {
      JSONObject item = list.optJSONObject(i);
      LinearLayout c = u.card();
      LinearLayout r = u.row();
      ImageView photo =
          image(
              json(
                  "id",
                  item.optLong("productId"),
                  "name",
                  item.optString("name"),
                  "imageUrl",
                  item.optString("imageUrl")),
              112);
      r.addView(photo, new LinearLayout.LayoutParams(u.d(102), u.d(112)));
      LinearLayout description = u.col();
      description.setPadding(u.d(14), 0, 0, 0);
      u.add(description, u.text(item.optString("name"), 14, Ui.INK, true));
      u.space(description, 8);
      u.add(description, u.text(Ui.money(item.optLong("price")), 18, Ui.BLUE, true));
      TextView original = u.text(Ui.money(item.optLong("originalPrice")), 11, Ui.MUTED, false);
      original.setPaintFlags(original.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      u.add(description, original);
      u.weight(r, description);
      u.add(c, r);
      u.space(c, 12);
      ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
      bar.setMax(Math.max(1, item.optInt("quota")));
      bar.setProgress(item.optInt("sold"));
      u.add(c, bar);
      u.space(c, 8);
      u.add(
          c,
          u.text(
              "Đã bán " + item.optInt("sold") + " / " + item.optInt("quota"), 10, Ui.MUTED, false));
      u.space(c, 12);
      boolean reminded = item.optBoolean("reminded");
      u.add(
          c,
          u.button(
              reminded ? "✓ Đã đặt nhắc · Hủy nhắc" : "Nhắc tôi khi mở bán",
              false,
              () -> {
                if (!loggedIn()) {
                  s.afterLogin = "flash";
                  go("login");
                  return;
                }
                send(
                    reminded ? "DELETE" : "POST",
                    "/flash-sales/items/" + item.optLong("id") + "/reminder",
                    null,
                    d -> {
                      if (reminded) SaleReminderReceiver.cancel(this, item.optLong("id"));
                      else {
                        if (android.os.Build.VERSION.SDK_INT >= 33
                            && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                                != android.content.pm.PackageManager.PERMISSION_GRANTED)
                          requestPermissions(
                              new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, 41);
                        SaleReminderReceiver.schedule(
                            this,
                            item.optLong("id"),
                            item.optString("name"),
                            item.optLong("productId"),
                            sale.optString("startsAt"));
                      }
                      toast(reminded ? "Đã hủy nhắc" : "Đã đặt nhắc Flash Sale");
                      activity.render();
                    });
              }));
      u.add(c, u.link("Xem sản phẩm  →", () -> go("product", item.optLong("productId"))));
      u.add(parent, c);
      u.space(parent, 14);
    }
  }

  // Lên lịch cập nhật bộ đếm theo thời điểm bắt đầu/kết thúc chương trình.
  private void startCountdown(TextView timer, TextView seconds, JSONObject sale) {
    int stamp = s.generation;
    android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    Runnable tick =
        new Runnable() {
          // Bước công việc hẹn giờ: cập nhật trạng thái hiện tại rồi lên lịch tiếp nếu còn hợp lệ.
          public void run() {
            if (stamp != s.generation || !timer.isAttachedToWindow() && isFinishing()) return;
            try {
              long now = System.currentTimeMillis(),
                  start = SaleReminderReceiver.instant(sale.optString("startsAt")),
                  end = SaleReminderReceiver.instant(sale.optString("endsAt"));
              long left = (now < start ? start - now : end - now) / 1000;
              if (left <= 0) {
                timer.setText("Khung giờ đã kết thúc");
                seconds.setText("");
              } else
                timer.setText(
                    (now < start ? "Bắt đầu sau  " : "Kết thúc sau  ")
                        + String.format(
                            Locale.ROOT, "%02d : %02d : ", left / 3600, left / 60 % 60));
              if (left > 0) seconds.setText(String.format(Locale.ROOT, "%02d", left % 60));
              handler.postDelayed(this, 1000);
            } catch (Exception e) {
              timer.setText(
                  date(sale.optString("startsAt")) + " — " + date(sale.optString("endsAt")));
            }
          }
        };
    handler.post(tick);
    if (Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        > 0) {
      android.animation.Keyframe a = android.animation.Keyframe.ofFloat(0f, 1f),
          b = android.animation.Keyframe.ofFloat(.45f, .55f),
          c = android.animation.Keyframe.ofFloat(1f, 1f);
      android.animation.PropertyValuesHolder opacity =
          android.animation.PropertyValuesHolder.ofKeyframe("alpha", a, b, c);
      android.animation.ObjectAnimator animator =
          android.animation.ObjectAnimator.ofPropertyValuesHolder(seconds, opacity);
      animator.setDuration(1000);
      animator.setInterpolator(new AccelerateDecelerateInterpolator());
      animator.setRepeatCount(android.animation.ValueAnimator.INFINITE);
      seconds.addOnAttachStateChangeListener(
          new View.OnAttachStateChangeListener() {
            // Tiếp tục công việc khi View được gắn trở lại cửa sổ.
            public void onViewAttachedToWindow(View v) {}

            // Dừng callback/hẹn giờ khi View rời cửa sổ.
            public void onViewDetachedFromWindow(View v) {
              animator.cancel();
            }
          });
      animator.start();
    }
  }
}

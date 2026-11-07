package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Quản lý danh sách banner: tải ảnh, xử lý vuốt/chạm, chấm chỉ báo và tự chuyển khi màn hình đang hoạt động.
 */
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/** Mỗi slide chỉ vẽ một ảnh. Liên kết đến sản phẩm đến từ bản ghi banner. */
public final class HomeBanner {
  private static final long INTERVAL_MS = 5_000;
  private final MainActivity activity;
  private final View hero;
  private final ImageView photo;
  private final LinearLayout controls;
  private final Handler handler = new Handler(Looper.getMainLooper());
  private final List<JSONObject> banners = new ArrayList<>();
  private int index;
  private boolean active;
  private final Runnable tick = () -> select((index + 1) % banners.size());

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public HomeBanner(MainActivity activity, View layout) {
    this.activity = activity;
    hero = layout.findViewById(R.id.home_hero);
    photo = layout.findViewById(R.id.home_hero_image);
    controls = layout.findViewById(R.id.home_banner_controls);
    hero.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
      int width = right - left;
      int height = Math.round(width / 2f);
      if (height > 0 && view.getLayoutParams().height != height) {
        view.getLayoutParams().height = height;
        view.requestLayout();
      }
    });
    bindSwipe();
  }

  // Gắn GestureDetector để phân biệt thao tác chạm và vuốt banner.
  private void bindSwipe() {
    GestureDetector gestures = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
      // Xác nhận bắt đầu chuỗi sự kiện chạm để bộ nhận cử chỉ tiếp tục theo dõi.
      @Override public boolean onDown(MotionEvent event) { return true; }

      // Xử lý chạm ngắn lên banner hiện tại.
      @Override public boolean onSingleTapUp(MotionEvent event) {
        return hero.performClick();
      }

      // Kiểm tra hướng/độ dài vận tốc vuốt rồi chuyển banner.
      @Override public boolean onFling(MotionEvent start, MotionEvent end, float velocityX, float velocityY) {
        if (start == null || banners.size() < 2) return false;
        float dx = end.getX() - start.getX();
        float dy = end.getY() - start.getY();
        if (Math.abs(dx) < activity.ui().d(32) || Math.abs(dx) <= Math.abs(dy)) return false;
        int direction = dx < 0 ? 1 : -1;
        select((index + direction + banners.size()) % banners.size());
        return true;
      }
    });
    hero.setOnTouchListener((view, event) -> {
      int action = event.getActionMasked();
      if (action == MotionEvent.ACTION_DOWN) handler.removeCallbacks(tick);
      boolean handled = gestures.onTouchEvent(event);
      if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) schedule();
      return handled;
    });
  }

  // Nhận danh sách banner có dữ liệu hợp lệ và chọn banner để hiển thị.
  public void setBanners(JSONArray values) {
    handler.removeCallbacks(tick);
    banners.clear();
    for (int i = 0; i < values.length(); i++) {
      JSONObject banner = values.optJSONObject(i);
      if (banner != null && banner.optLong("productId") > 0 && !banner.isNull("imageUrl")
          && !banner.optString("imageUrl").trim().isEmpty()) {
        banners.add(banner);
      }
    }
    hero.setVisibility(banners.isEmpty() ? View.GONE : View.VISIBLE);
    controls.setVisibility(banners.size() > 1 ? View.VISIBLE : View.GONE);
    if (!banners.isEmpty()) select(0);
  }

  // Cho phép tự chuyển banner khi màn hình đang hoạt động.
  public void resume() {
    active = true;
    schedule();
  }

  // Dừng bộ hẹn giờ tự chuyển banner khi màn hình bị che hoặc rời.
  public void pause() {
    active = false;
    handler.removeCallbacks(tick);
  }

  // Đặt callback tự chuyển banner sau khoảng thời gian quy định, chỉ khi màn hình còn hoạt động.
  private void schedule() {
    handler.removeCallbacks(tick);
    if (active && banners.size() > 1) handler.postDelayed(tick, INTERVAL_MS);
  }

  // Áp dụng lựa chọn và cập nhật các vùng giao diện phụ thuộc lựa chọn đó.
  private void select(int target) {
    index = target;
    JSONObject banner = banners.get(index);
    long product = banner.optLong("productId");
    String url = banner.optString("imageUrl");
    // Reset the previous slide while its configured image is loading.
    photo.setImageDrawable(null);
    activity.images().category(photo, url);
    hero.setContentDescription(banner.optString("title", "Xem sản phẩm"));
    hero.setTag("configured-banner");
    hero.setOnClickListener(v -> activity.go("product", product));
    updateDots();
    schedule();
  }

  // Cập nhật các chấm chỉ báo vị trí banner hiện tại.
  private void updateDots() {
    controls.removeAllViews();
    Ui ui = activity.ui();
    for (int i = 0; i < banners.size(); i++) {
      final int target = i;
      boolean selected = i == index;
      FrameLayout button = new FrameLayout(activity);
      button.setFocusable(true);
      button.setContentDescription("Banner " + (i + 1) + " trên " + banners.size());
      button.setSelected(selected);
      View dot = new View(activity);
      dot.setBackground(ui.bg(selected ? Ui.BLUE : Ui.BORDER, 3));
      button.addView(dot, new FrameLayout.LayoutParams(ui.d(selected ? 18 : 7), ui.d(4), Gravity.CENTER));
      button.setOnClickListener(v -> select(target));
      controls.addView(button, new LinearLayout.LayoutParams(ui.d(28), ui.d(28)));
    }
  }
}

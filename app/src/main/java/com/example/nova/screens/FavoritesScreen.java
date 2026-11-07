package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp lưới các sản phẩm khách đã lưu và cho phép mở chi tiết.
 * Bố cục liên quan: screen_favorites.xml.
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

/** Customer screen: favorites; its layout and behavior are edited together. */
public final class FavoritesScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public FavoritesScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_favorites; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (BuildConfig.NOVA_MIDTERM_API) {
      bindBody(layoutId());
      empty("Danh sách yêu thích", "Tính năng lưu sản phẩm đang được hoàn thiện. Hãy khám phá những sản phẩm phù hợp với bạn.");
      u.add(content, u.button("Khám phá sản phẩm", false, () -> tab("categories")));
      return;
    }
    if (!requireLogin()) return;
    bindBody(R.layout.screen_favorites);
    get(
        "/favorites",
        data -> {
          JSONArray list = arr(data);
          if (list.length() == 0)
            empty("Lưu điều bạn thích", "Nhấn trái tim trên sản phẩm để xem lại tại đây.");
          else productGrid(content, list, 1000);
        });
  }
}

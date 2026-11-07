package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Gửi email để yêu cầu mã khôi phục.
 * Bố cục liên quan: screen_forgot_password.xml.
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

/** Customer screen: forgot; its layout and behavior are edited together. */
public final class ForgotPasswordScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ForgotPasswordScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_forgot_password; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    final LinearLayout layout = screen(R.layout.screen_forgot_password);
    EditText email = layout.findViewById(R.id.forgot_email);
    layout.findViewById(R.id.forgot_submit).setOnClickListener(v ->
        send("POST", "/auth/forgot-password", json("email", email.getText().toString().trim()), data -> {
          s.otpKind = "recover";
          s.otpEmail = email.getText().toString().trim();
          s.otpChallenge = obj(data).optString("challengeId");
          go("otp");
        }));
  }
}

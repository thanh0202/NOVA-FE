package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nhập email và mật khẩu, ghi nhớ phiên, xử lý tài khoản chưa xác thực và điều hướng sang đăng ký hoặc quên mật khẩu.
 * Bố cục liên quan: screen_login.xml.
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

/** Customer screen: login; its layout and behavior are edited together. */
public final class LoginScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public LoginScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_login; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    final LinearLayout layout = screen(R.layout.screen_login);
    
    EditText email = layout.findViewById(R.id.auth_email);
    EditText password = layout.findViewById(R.id.auth_password);
    CheckBox remember = layout.findViewById(R.id.auth_remember);
    TextView submit = layout.findViewById(R.id.auth_submit);

    layout.findViewById(R.id.auth_explore).setOnClickListener(v -> tab("home"));
    layout.findViewById(R.id.auth_forgot).setOnClickListener(v -> go("forgot"));
    layout.findViewById(R.id.auth_switch).setOnClickListener(v -> go("register"));
    View eye = layout.findViewById(R.id.auth_eye);
    final boolean[] visible = {false};
    eye.setOnClickListener(v -> {
      visible[0] = !visible[0];
      password.setInputType(visible[0]
          ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : PASSWORD);
      password.setSelection(password.length());
      eye.setAlpha(visible[0] ? .5f : 1f);
    });
    submit.setOnClickListener(
        v -> {
          String e = email.getText().toString().trim(), pw = password.getText().toString();
          if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.setError("Email chưa hợp lệ");
            return;
          }
          if (pw.length() < 8) {
            password.setError("Mật khẩu tối thiểu 8 ký tự");
            return;
          }

          submit.setEnabled(false);
          submit.setAlpha(.65f);
          JSONObject payload =
              json("email", e, "password", pw);
          int stamp = s.generation;
          api.request(
              "POST",
              "/auth/login",
              payload,
              (data, error, status) -> {
                if (stamp != s.generation) return;
                submit.setEnabled(true);
                submit.setAlpha(1);
                if (error != null) {
                  if (status == 403 && error.toLowerCase(java.util.Locale.ROOT).contains("xác thực email")) {
                    confirm("Xác thực email", "Gửi lại mã xác thực tới " + e + "?", () -> {
                      send("POST", "/auth/resend-verification", json("email", e), sent -> {
                        s.otpKind = "register";
                        s.otpEmail = e;
                        s.otpChallenge = obj(sent).optString("challengeId");
                        s.otpRemember = remember.isChecked();
                        go("otp");
                      });
                    });
                  } else alert("Đăng nhập", error);
                  return;
                }
                try {
                  JSONObject result = obj(data);
                  JSONObject profile = result.getJSONObject("user");
                  String token = result.getString("token");
                  if (!MainActivity.isCustomerProfile(profile)) { rejectNonCustomer(token); return; }
                  session.save(token, remember.isChecked());
                  s.user = profile;
                  s.route = s.afterLogin;
                  s.entityId = s.afterLoginId;
                  s.history.clear();
                  activity.render();
                } catch (Exception ex) {
                  alert("Không thể lưu phiên", "Vui lòng thử lại.");
                }
              });
        });
  }
}

package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Kiểm tra độ mạnh và xác nhận mật khẩu mới sau bước OTP.
 * Bố cục liên quan: screen_reset_password.xml.
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

/** Customer screen: reset-password; its layout and behavior are edited together. */
public final class ResetPasswordScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ResetPasswordScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_reset_password; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    final LinearLayout layout = screen(R.layout.screen_reset_password);
    if (s.recoveryChallenge.isEmpty() || s.recoveryCode.isEmpty()) { s.route = "forgot"; activity.render(); return; }
    layout.findViewById(R.id.reset_back).setOnClickListener(v -> onBackPressed());
    layout.findViewById(R.id.reset_help).setOnClickListener(v -> go("support"));
    layout.findViewById(R.id.reset_login).setOnClickListener(v -> {
      s.recoveryChallenge = ""; s.recoveryCode = ""; tab("login");
    });
    EditText password = layout.findViewById(R.id.reset_new_password);
    EditText confirm = layout.findViewById(R.id.reset_confirm_password);
    bindPasswordVisibility(password, layout.findViewById(R.id.reset_show_password));
    bindPasswordVisibility(confirm, layout.findViewById(R.id.reset_show_confirm));
    View submit = layout.findViewById(R.id.reset_submit);
    submit.setOnClickListener(v -> {
      String next = password.getText().toString();
      if (!PasswordRules.isValid(next)) {
        password.setError(PasswordRules.MESSAGE); password.requestFocus(); return;
      }
      if (!next.equals(confirm.getText().toString())) {
        confirm.setError("Mật khẩu xác nhận chưa khớp"); confirm.requestFocus(); return;
      }
      submit.setEnabled(false);
      int stamp = s.generation;
      JSONObject payload = json("challengeId", s.recoveryChallenge, "code", s.recoveryCode,
          "newPassword", next);
      if (!BuildConfig.NOVA_MIDTERM_API) payload = json("challengeId", s.recoveryChallenge,
          "code", s.recoveryCode, "newPassword", next, "email", s.otpEmail);
      api.request("POST", "/auth/reset-password", payload, (data, issue, status) -> {
        if (stamp != s.generation || isFinishing()) return;
        submit.setEnabled(true);
        if (issue != null) { alert("Chưa thể cập nhật mật khẩu", issue); return; }
        s.recoveryChallenge = ""; s.recoveryCode = ""; session.clear();
        toast("Mật khẩu đã được cập nhật. Vui lòng đăng nhập lại."); tab("login");
      });
    });
    confirm.setOnEditorActionListener((field, action, event) -> {
      if (action == EditorInfo.IME_ACTION_DONE) { submit.performClick(); return true; }
      return false;
    });
  }

  // Đồng bộ nút ẩn/hiện với kiểu hiển thị ô mật khẩu.
  private void bindPasswordVisibility(EditText field, View toggle) {
    toggle.setOnClickListener(v -> {
      boolean hidden = field.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod;
      field.setTransformationMethod(hidden ? android.text.method.HideReturnsTransformationMethod.getInstance()
          : android.text.method.PasswordTransformationMethod.getInstance());
      field.setSelection(field.getText().length());
      toggle.setContentDescription(hidden ? "Ẩn mật khẩu" : "Hiện mật khẩu");
    });
  }
}

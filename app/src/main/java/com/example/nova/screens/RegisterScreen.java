package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Kiểm tra biểu mẫu, gửi yêu cầu tạo tài khoản và mở bước xác thực email bằng OTP.
 * Bố cục liên quan: screen_register.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.MainActivity;
import com.example.nova.PasswordRules;
import com.example.nova.R;
import org.json.JSONObject;

/** Đăng ký chỉ tạo tài khoản chờ xác thực; phiên đăng nhập được cấp sau OTP. */
public final class RegisterScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public RegisterScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_register; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    LinearLayout root = screen(layoutId());
    EditText name = root.findViewById(R.id.auth_name);
    EditText email = root.findViewById(R.id.auth_email);
    EditText phone = root.findViewById(R.id.auth_phone);
    EditText password = root.findViewById(R.id.auth_password);
    EditText repeat = root.findViewById(R.id.auth_password_repeat);
    CheckBox terms = root.findViewById(R.id.auth_terms);
    TextView submit = root.findViewById(R.id.auth_submit);
    root.findViewById(R.id.auth_back).setOnClickListener(v -> onBackPressed());
    root.findViewById(R.id.auth_switch).setOnClickListener(v -> activity.replace("login", 0));
    passwordToggle(root.findViewById(R.id.auth_eye), password);
    passwordToggle(root.findViewById(R.id.auth_eye_repeat), repeat);

    submit.setOnClickListener(v -> {
      String fullName = name.getText().toString().trim();
      String address = email.getText().toString().trim();
      String number = phone.getText().toString().trim();
      String secret = password.getText().toString();
      if (fullName.length() < 2) { name.setError("Nhập họ tên từ 2 ký tự"); return; }
      if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) { email.setError("Email chưa hợp lệ"); return; }
      if (!number.isEmpty() && !number.matches("[+0-9 ()-]{8,20}")) {
        phone.setError("Kiểm tra lại số điện thoại"); return;
      }
      if (!PasswordRules.isValid(secret)) { password.setError(PasswordRules.MESSAGE); return; }
      if (!secret.equals(repeat.getText().toString())) { repeat.setError("Mật khẩu xác nhận chưa khớp"); return; }
      if (!terms.isChecked()) { toast("Vui lòng đồng ý điều khoản để tiếp tục."); return; }
      submit.setEnabled(false);
      submit.setAlpha(.6f);
      int generation = s.generation;
      api.request("POST", "/auth/register", ApiClient.json(
          "name", fullName, "email", address, "phone", number, "password", secret),
          (data, error, status) -> {
            if (generation != s.generation || isFinishing() || isDestroyed()) return;
            submit.setEnabled(true);
            submit.setAlpha(1f);
            if (error != null) { alert("Chưa tạo được tài khoản", error); return; }
            JSONObject challenge = obj(data);
            if (challenge.optString("challengeId").isBlank()) {
              alert("Chưa nhận được mã xác thực", "Vui lòng thử lại sau."); return;
            }
            s.otpKind = "register";
            s.otpEmail = address;
            s.otpChallenge = challenge.optString("challengeId");
            s.otpRemember = true;
            go("otp");
          });
    });
  }

  // Gắn thao tác ẩn/hiện mật khẩu và giữ vị trí con trỏ nhập.
  private void passwordToggle(View button, EditText field) {
    button.setOnClickListener(v -> {
      boolean show = field.getInputType() == PASSWORD;
      field.setInputType(show ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : PASSWORD);
      u.applyTypography(field);
      field.setSelection(field.length());
      button.setAlpha(show ? .55f : 1f);
      button.setContentDescription(show ? "Ẩn mật khẩu" : "Hiện mật khẩu");
    });
  }
}

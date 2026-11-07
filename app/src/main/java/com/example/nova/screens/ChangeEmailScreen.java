package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nhập địa chỉ mới và mật khẩu hiện tại, gửi yêu cầu rồi mở bước xác thực OTP.
 * Bố cục liên quan: screen_change_email.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.MainActivity;
import com.example.nova.R;

/** Email mới chỉ được lưu sau khi xác minh bằng mã gửi đến chính email đó. */
public final class ChangeEmailScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ChangeEmailScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_change_email; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = screen(layoutId());
    EditText email = root.findViewById(R.id.email_new);
    EditText password = root.findViewById(R.id.email_password);
    TextView submit = root.findViewById(R.id.email_submit);
    String currentEmail = s.user.optString("email", "");
    ((TextView) root.findViewById(R.id.email_current)).setText("Email hiện tại: " + currentEmail);
    submit.setOnClickListener(v -> {
      String destination = email.getText().toString().trim();
      if (!Patterns.EMAIL_ADDRESS.matcher(destination).matches()) {
        email.setError("Nhập địa chỉ email hợp lệ");
        email.requestFocus();
        return;
      }
      if (destination.equalsIgnoreCase(currentEmail)) {
        email.setError("Email mới phải khác email hiện tại");
        email.requestFocus();
        return;
      }
      if (password.getText().toString().isEmpty()) {
        password.setError("Nhập mật khẩu hiện tại để xác nhận");
        password.requestFocus();
        return;
      }
      submit.setEnabled(false);
      submit.setAlpha(.55f);
      int generation = s.generation;
      api.request("POST", "/me/email", json(
          "email", destination, "password", password.getText().toString()),
          (data, error, status) -> {
            if (generation != s.generation || isFinishing() || isDestroyed()) return;
            submit.setEnabled(true);
            submit.setAlpha(1f);
            if (error != null) {
              alert("Chưa gửi được mã xác thực", error);
              return;
            }
            password.setText("");
            s.otpKind = "email";
            s.otpEmail = destination;
            s.otpChallenge = obj(data).optString("challengeId");
            go("otp");
          });
    });
  }
}

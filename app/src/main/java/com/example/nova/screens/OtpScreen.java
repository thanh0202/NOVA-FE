package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Xác nhận mã cho đăng ký, khôi phục mật khẩu hoặc đổi email. Xử lý gửi lại mã theo luồng.
 * Bố cục liên quan: screen_otp.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import org.json.JSONObject;

/** Xác thực email dùng một ô nhập số và sáu ô hiển thị, hỗ trợ dán cả mã. */
public final class OtpScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public OtpScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_otp; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    LinearLayout root = screen(layoutId());
    if (s.otpChallenge.isBlank()) {
      empty("Mã xác thực", "Hãy yêu cầu mã OTP mới để tiếp tục.");
      return;
    }
    ((TextView) root.findViewById(R.id.otp_email)).setText(s.otpEmail);
    ((TextView) root.findViewById(R.id.otp_step)).setText(s.otpKind.equals("recover") ? "BƯỚC 2 / 3" : "BƯỚC 2 / 2");
    TextView description = root.findViewById(R.id.otp_description);
    description.setText(s.otpKind.equals("recover")
        ? "Nhập mã 6 chữ số đã gửi đến email để tiếp tục khôi phục mật khẩu."
        : s.otpKind.equals("email")
            ? "Nhập mã 6 chữ số đã gửi đến email mới để xác nhận thay đổi."
            : "Nhập mã 6 chữ số đã gửi đến email để xác thực tài khoản.");
    EditText code = root.findViewById(R.id.otp_code);
    TextView message = root.findViewById(R.id.otp_message);
    TextView submit = root.findViewById(R.id.otp_submit);
    TextView resend = root.findViewById(R.id.otp_resend);
    LinearLayout boxes = root.findViewById(R.id.otp_boxes);
    TextView[] digits = new TextView[6];
    for (int i = 0; i < digits.length; i++) {
      digits[i] = u.text("·", 22, Ui.INK, true);
      digits[i].setGravity(Gravity.CENTER);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, 1);
      if (i != 5) params.rightMargin = u.d(6);
      boxes.addView(digits[i], params);
    }
    updateDigits(digits, "");
    code.addTextChangedListener(new TextWatcher() {
      // Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó.
      @Override public void beforeTextChanged(CharSequence value, int start, int count, int after) {}
      // Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu.
      @Override public void onTextChanged(CharSequence value, int start, int before, int count) {
        updateDigits(digits, value.toString());
        message.setText("");
      }
      // Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi.
      @Override public void afterTextChanged(Editable value) {}
    });
    submit.setOnClickListener(v -> {
      String otp = code.getText().toString();
      if (!otp.matches("[0-9]{6}")) {
        message.setText("Nhập đủ 6 chữ số.");
        return;
      }
      boolean recover = s.otpKind.equals("recover");
      boolean register = s.otpKind.equals("register");
      String path = recover ? "/auth/verify-reset-code" : register ? "/auth/verify-email" : "/me/email/verify";
      int generation = s.generation;
      submit.setEnabled(false);
      resend.setEnabled(false);
      api.request("POST", path, ApiClient.json("challengeId", s.otpChallenge, "code", otp),
          (data, issue, status) -> {
            if (generation != s.generation || isFinishing() || isDestroyed()) return;
            submit.setEnabled(true);
            resend.setEnabled(true);
            if (issue != null) {
              message.setText(issue);
              return;
            }
            if (recover) {
              s.recoveryChallenge = s.otpChallenge;
              s.recoveryCode = otp;
              activity.replace("reset-password", 0);
              return;
            }
            s.otpChallenge = "";
            if (!register) {
              // Bản GK thu hồi mọi phiên khi đổi email; bản đầy đủ cũng đăng xuất tại đây.
              api.revokeToken(session.token());
              session.clear();
              s.user = new JSONObject();
              s.cartCount = 0;
              s.afterLogin = "account";
              s.afterLoginId = 0;
              toast("Đã đổi email. Vui lòng đăng nhập lại.");
              tab("login");
              return;
            }
            JSONObject result = obj(data);
            JSONObject profile = result.optJSONObject("user");
            String token = result.optString("token");
            if (profile == null || token.isBlank() || !MainActivity.isCustomerProfile(profile)) {
              api.revokeToken(token);
              message.setText("Không thể mở phiên tài khoản khách hàng.");
              return;
            }
            try {
              session.save(token, s.otpRemember);
            } catch (Exception error) {
              api.revokeToken(token);
              message.setText("Email đã xác thực. Hãy đăng nhập lại.");
              return;
            }
            s.user = profile;
            s.history.clear();
            activity.replace(s.afterLogin, s.afterLoginId);
          });
    });
    if (s.otpKind.equals("email")) resend.setText("Yêu cầu mã mới");
    resend.setOnClickListener(v -> {
      if ("email".equals(s.otpKind)) {
        onBackPressed();
        return;
      }
      String path = "register".equals(s.otpKind) ? "/auth/resend-verification" : "/auth/forgot-password";
      int generation = s.generation;
      resend.setEnabled(false);
      submit.setEnabled(false);
      api.request("POST", path, ApiClient.json("email", s.otpEmail), (data, issue, status) -> {
        if (generation != s.generation || isFinishing() || isDestroyed()) return;
        resend.setEnabled(true);
        submit.setEnabled(true);
        if (issue != null) {
          message.setText(issue);
          return;
        }
        s.otpChallenge = obj(data).optString("challengeId");
        code.setText("");
        message.setText("Đã yêu cầu mã mới.");
      });
    });
  }

  // Đồng bộ chuỗi mã OTP thành các ô chữ số hiển thị.
  private void updateDigits(TextView[] digits, String value) {
    for (int i = 0; i < digits.length; i++) {
      digits[i].setText(i < value.length() ? value.substring(i, i + 1) : "·");
      digits[i].setBackground(u.outline(0xFFFFFFFF, 14,
          i == value.length() ? Ui.BLUE : Ui.BORDER));
    }
  }
}

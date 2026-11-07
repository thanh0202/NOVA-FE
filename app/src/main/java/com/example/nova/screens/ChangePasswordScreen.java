package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Kiểm tra mật khẩu hiện tại, xác nhận mật khẩu mới rồi gửi cập nhật.
 * Bố cục liên quan: screen_change_password.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.MainActivity;
import com.example.nova.PasswordRules;
import com.example.nova.R;
import org.json.JSONObject;

/** Đổi mật khẩu bằng mật khẩu hiện tại, sau đó kết thúc phiên đăng nhập. */
public final class ChangePasswordScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ChangePasswordScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_change_password; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = screen(layoutId());
    EditText current = root.findViewById(R.id.password_current);
    EditText next = root.findViewById(R.id.password_next);
    EditText repeat = root.findViewById(R.id.password_repeat);
    TextView save = root.findViewById(R.id.password_save);
    save.setOnClickListener(v -> {
      if (current.getText().toString().isEmpty()) {
        current.setError("Nhập mật khẩu hiện tại");
        current.requestFocus();
        return;
      }
      String password = next.getText().toString();
      if (!PasswordRules.isValid(password)) {
        next.setError(PasswordRules.MESSAGE);
        next.requestFocus();
        return;
      }
      if (!password.equals(repeat.getText().toString())) {
        repeat.setError("Mật khẩu xác nhận chưa khớp");
        repeat.requestFocus();
        return;
      }
      save.setEnabled(false);
      save.setAlpha(.55f);
      int generation = s.generation;
      api.request("PUT", "/me/password", json(
          "currentPassword", current.getText().toString(), "newPassword", password),
          (data, error, status) -> {
            if (generation != s.generation || isFinishing() || isDestroyed()) return;
            save.setEnabled(true);
            save.setAlpha(1f);
            if (error != null) {
              alert("Chưa đổi được mật khẩu", error);
              return;
            }
            session.clear();
            s.user = new JSONObject();
            s.cartCount = 0;
            s.afterLogin = "account";
            s.afterLoginId = 0;
            toast("Đã đổi mật khẩu. Vui lòng đăng nhập lại.");
            tab("login");
          });
    });
  }
}

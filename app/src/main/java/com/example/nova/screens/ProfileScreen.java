package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Đọc và lưu họ tên, điện thoại, ngày sinh, giới tính; chọn ảnh đại diện từ thiết bị và tải lên backend GK. Hiển thị email và mở màn đổi email.
 * Bố cục liên quan: screen_profile.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.json.JSONObject;

/** Profile fields and independently saved avatar. Email uses a separate verification flow. */
public final class ProfileScreen extends BaseScreen {
  private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd / MM / yyyy", Locale.ROOT);
  private static final String[] GENDERS = {"male", "female", "other"};
  private String gender = "undisclosed";
  private LocalDate birthDate;

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ProfileScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_profile; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = inflateLayout(layoutId(), content);
    content.addView(root, new LinearLayout.LayoutParams(-1, -2, 1));
    EditText name = root.findViewById(R.id.profile_name);
    EditText phone = root.findViewById(R.id.profile_phone);
    TextView birthday = root.findViewById(R.id.profile_birth_date);
    TextView save = root.findViewById(R.id.profile_save);
    ImageView avatar = root.findViewById(R.id.profile_avatar);
    TextView changeAvatar = root.findViewById(R.id.profile_avatar_change);
    TextView avatarStatus = root.findViewById(R.id.profile_avatar_status);
    String avatarHint = avatarStatus.getText().toString();
    avatar.setBackground(u.bg(0xFFEFF6FF, 40));
    avatar.setClipToOutline(true);
    avatar.setEnabled(false);
    changeAvatar.setEnabled(false);
    changeAvatar.setAlpha(.55f);
    TextView[] choices = {root.findViewById(R.id.profile_male), root.findViewById(R.id.profile_female),
        root.findViewById(R.id.profile_other)};
    root.findViewById(R.id.profile_change_email).setOnClickListener(v -> go("email"));
    for (int i = 0; i < choices.length; i++) {
      final int selected = i;
      choices[i].setOnClickListener(v -> { gender = GENDERS[selected]; showGender(choices); });
    }
    u.action((View) birthday.getParent(), () -> chooseBirthday(birthday));
    save.setEnabled(false);
    save.setAlpha(.55f);
    get("/me", data -> {
      JSONObject me = obj(data);
      s.user = me;
      showAvatar(avatar, me);
      name.setText(optional(me, "name"));
      phone.setText(optional(me, "phone"));
      ((TextView) root.findViewById(R.id.profile_email)).setText(optional(me, "email"));
      root.findViewById(R.id.profile_verified).setVisibility(me.optBoolean("emailVerified") ? View.VISIBLE : View.GONE);
      ((TextView) root.findViewById(R.id.profile_change_email)).setText(
          (me.optBoolean("emailVerified") ? "Email đã xác thực" : "Email đăng nhập") + " · Thay đổi email →");
      gender = optional(me, "gender");
      try { birthDate = LocalDate.parse(optional(me, "birthDate")); }
      catch (RuntimeException absent) { birthDate = null; }
      showBirthday(birthday);
      showGender(choices);
      save.setEnabled(true);
      save.setAlpha(1f);
      changeAvatar.setEnabled(true);
      changeAvatar.setAlpha(1f);
      avatar.setEnabled(true);
    });
    View.OnClickListener chooseAvatar = v -> {
      if (!changeAvatar.isEnabled()) return;
      int generation = s.generation;
      setActionsEnabled(avatar, changeAvatar, save, false);
      avatarStatus.setText("Đang chọn ảnh đại diện…");
      avatarStatus.setTextColor(Ui.MUTED);
      activity.pickImages(false, 1, (selected, issue) -> {
        if (generation != s.generation || isFinishing() || isDestroyed()) return;
        if (issue != null || selected.isEmpty()) {
          setActionsEnabled(avatar, changeAvatar, save, true);
          avatarStatus.setText(issue == null ? avatarHint : issue);
          if (issue != null) alert("Chưa chọn được ảnh", issue);
          return;
        }
        changeAvatar.setText("Đang tải ảnh…");
        avatarStatus.setText("Đang lưu ảnh đại diện…");
        api.uploadImages(this, selected, "/me/avatar", "file", 1024, (data, error, status) -> {
          if (generation != s.generation || isFinishing() || isDestroyed()) return;
          if (status == 401 && !loggedIn()) { requireLogin(); return; }
          setActionsEnabled(avatar, changeAvatar, save, true);
          changeAvatar.setText("Đổi ảnh đại diện");
          if (error != null) {
            avatarStatus.setText(error + " Chọn ảnh để thử lại.");
            avatarStatus.setTextColor(Ui.INK);
            alert("Chưa đổi được ảnh đại diện", error);
            return;
          }
          s.user = obj(data);
          showAvatar(avatar, s.user);
          avatarStatus.setText("Đã lưu ảnh đại diện.");
          avatarStatus.setTextColor(Ui.BLUE);
          toast("Đã lưu ảnh đại diện");
        });
      });
    };
    changeAvatar.setOnClickListener(chooseAvatar);
    avatar.setOnClickListener(chooseAvatar);
    save.setOnClickListener(v -> {
      if (!save.isEnabled()) return;
      String fullName = name.getText().toString().trim();
      String number = phone.getText().toString().trim();
      if (fullName.length() < 2) { name.setError("Nhập họ tên từ 2 ký tự"); return; }
      if (!number.isEmpty() && !number.matches("[+0-9 ()-]{8,20}")) {
        phone.setError("Kiểm tra lại số điện thoại"); return;
      }
      setActionsEnabled(avatar, changeAvatar, save, false);
      int generation = s.generation;
      api.request("PUT", "/me", ApiClient.json("name", fullName, "phone", number, "gender", gender,
          "birthDate", birthDate == null ? JSONObject.NULL : birthDate.toString()),
          (data, error, status) -> {
            if (generation != s.generation || isFinishing() || isDestroyed()) return;
            if (status == 401 && !loggedIn()) { requireLogin(); return; }
            setActionsEnabled(avatar, changeAvatar, save, true);
            if (error != null) { alert("Chưa lưu được thông tin", error); return; }
            s.user = obj(data);
            toast("Đã cập nhật thông tin");
            onBackPressed();
          });
    });
  }

  // Bật/tắt thao tác hồ sơ trong lúc đang tải ảnh hoặc lưu dữ liệu.
  private void setActionsEnabled(ImageView avatar, TextView changeAvatar, TextView save, boolean enabled) {
    avatar.setEnabled(enabled);
    changeAvatar.setEnabled(enabled);
    changeAvatar.setAlpha(enabled ? 1f : .55f);
    save.setEnabled(enabled);
    save.setAlpha(enabled ? 1f : .55f);
  }

  // Nạp URL avatar hoặc ảnh mặc định vào vùng ảnh hồ sơ.
  private void showAvatar(ImageView view, JSONObject me) {
    view.setImageResource(R.drawable.nova_account_avatar);
    images.category(view, me.isNull("avatarUrl") ? "" : me.optString("avatarUrl", ""));
  }

  // Mở DatePickerDialog để người dùng chọn ngày sinh.
  private void chooseBirthday(TextView field) {
    LocalDate initial = birthDate == null ? LocalDate.now().minusYears(18) : birthDate;
    DatePickerDialog dialog = new DatePickerDialog(this, (picker, year, month, day) -> {
      birthDate = LocalDate.of(year, month + 1, day);
      showBirthday(field);
    }, initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth());
    ZoneId zone = ZoneId.systemDefault();
    dialog.getDatePicker().setMinDate(LocalDate.of(1900, 1, 1).atStartOfDay(zone).toInstant().toEpochMilli());
    dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    dialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Bỏ ngày sinh", (d, which) -> {
      birthDate = null; showBirthday(field);
    });
    dialog.show();
  }

  // Hiển thị ngày sinh đã chọn theo định dạng giao diện.
  private void showBirthday(TextView field) {
    field.setText(birthDate == null ? "Chọn ngày sinh" : DISPLAY.format(birthDate));
    field.setTextColor(birthDate == null ? Ui.MUTED : Ui.INK);
  }

  // Hiển thị nhãn giới tính theo giá trị đang chọn.
  private void showGender(TextView[] choices) {
    for (int i = 0; i < choices.length; i++) {
      boolean active = GENDERS[i].equals(gender);
      choices[i].setBackground(active ? u.bg(0xFFFFFFFF, 12) : null);
      choices[i].setTextColor(active ? Ui.BLUE : Ui.MUTED);
      choices[i].setSelected(active);
    }
  }

  // Đọc giá trị trường tùy chọn và chuẩn hóa trường hợp null/rỗng.
  private String optional(JSONObject data, String key) { return data.isNull(key) ? "" : data.optString(key, ""); }
}

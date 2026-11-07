package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Đọc thông tin hiện tại, kiểm tra biểu mẫu và lưu thay đổi. Chứa phần xử lý chung cho cả thêm và sửa.
 * Bố cục liên quan: screen_address_form.xml.
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

/** Customer screen: edit-address; its layout and behavior are edited together. */
public class AddressFormScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public AddressFormScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_address_form; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    LinearLayout layout = bindBody(layoutId());
    TextView save = layout.findViewById(R.id.address_save);
    save.setEnabled(false);
    save.setAlpha(.55f);
    if (s.entityId == 0) bindFields(layout, new JSONObject());
    else get("/addresses", data -> {
      JSONArray list = arr(data);
      for (int i = 0; i < list.length(); i++) {
        JSONObject address = list.optJSONObject(i);
        if (address.optLong("id") == s.entityId) {
          bindFields(layout, address);
          return;
        }
      }
      empty("Không tìm thấy địa chỉ", "Địa chỉ có thể đã được xóa.");
    });
  }

  // Gắn dữ liệu địa chỉ hiện có vào các trường nhập của biểu mẫu.
  private void bindFields(LinearLayout layout, JSONObject address) {
    String[] keys = {"name", "phone", "province", "district", "ward", "line", "label", "note"};
    int[] ids = {R.id.address_name, R.id.address_phone, R.id.address_province,
        R.id.address_district, R.id.address_ward, R.id.address_line, R.id.address_label, R.id.address_note};
    Map<String, EditText> fields = new LinkedHashMap<>();
    for (int i = 0; i < keys.length; i++) {
      EditText field = layout.findViewById(ids[i]);
      field.setText(address.optString(keys[i]));
      fields.put(keys[i], field);
    }
    CheckBox primary = layout.findViewById(R.id.address_default);
    primary.setChecked(address.optBoolean("isDefault", false));
    TextView save = layout.findViewById(R.id.address_save);
    save.setEnabled(true);
    save.setAlpha(1f);
    save.setOnClickListener(v -> {
      JSONObject payload = json("isDefault", primary.isChecked());
      try {
        for (String key : keys) {
          String value = fields.get(key).getText().toString().trim();
          if (Arrays.asList("name", "phone", "province", "district", "ward", "line")
              .contains(key) && value.isEmpty()) {
            fields.get(key).setError("Vui lòng nhập thông tin");
            return;
          }
          payload.put(key, value);
        }
      } catch (JSONException ignored) { }
      send(s.entityId == 0 ? "POST" : "PUT", "/addresses" + (s.entityId == 0 ? "" : "/" + s.entityId),
          payload, d -> {
            toast("Đã lưu địa chỉ");
            onBackPressed();
          });
    });
  }
}

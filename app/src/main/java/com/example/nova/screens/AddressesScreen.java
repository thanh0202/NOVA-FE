package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp địa chỉ đã lưu, hiển thị mặc định và xử lý mở thêm, sửa hoặc xóa.
 * Bố cục liên quan: item_address.xml, screen_addresses.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.MainActivity;
import com.example.nova.R;
import org.json.JSONArray;
import org.json.JSONObject;

/** Customer screen: addresses; its layout and behavior are edited together. */
public final class AddressesScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public AddressesScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_addresses; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    final LinearLayout layout = screen(R.layout.screen_addresses);
    layout.findViewById(R.id.addresses_add).setOnClickListener(v -> go("add-address"));
    content = layout.findViewById(R.id.addresses_list);
    get(
        "/addresses",
        data -> {
          JSONArray list = arr(data);
          if (list.length() == 0)
            empty("Sổ địa chỉ trống", "Thêm địa chỉ để nhận hàng nhanh chóng.");
          for (int i = 0; i < list.length(); i++) {
            JSONObject a = list.optJSONObject(i);
            LinearLayout c = inflateLayout(R.layout.item_address, content);
            ((TextView) c.findViewById(R.id.address_item_title)).setText(
                a.optString("name") + " · " + a.optString("label", "Nhà"));
            ((TextView) c.findViewById(R.id.address_item_phone)).setText(a.optString("phone"));
            ((TextView) c.findViewById(R.id.address_item_location)).setText(formatAddress(a));
            c.findViewById(R.id.address_item_default)
                .setVisibility(a.optBoolean("isDefault") ? View.VISIBLE : View.GONE);
            TextView choose = c.findViewById(R.id.address_item_action);
            choose.setText("Chỉnh sửa");
            choose.setOnClickListener(view -> {
              go("edit-address", a.optLong("id"));
            });
            c.findViewById(R.id.address_item_remove).setOnClickListener(view ->
                confirm("Xóa địa chỉ?", formatAddress(a), () ->
                    send("DELETE", "/addresses/" + a.optLong("id"), null, d -> activity.render())));
            u.add(content, c);
            gap();
          }
        });
  }
}

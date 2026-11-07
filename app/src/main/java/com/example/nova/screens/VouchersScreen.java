package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp voucher, hiển thị điều kiện sử dụng và chọn mã cho phiên đặt hàng.
 * Bố cục liên quan: item_voucher.xml, screen_vouchers.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import org.json.JSONArray;
import org.json.JSONObject;

/** Customer screen: vouchers; its layout and behavior are edited together. */
public final class VouchersScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public VouchersScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_vouchers; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    bindBody(R.layout.screen_vouchers);
    get(
        "/vouchers",
        data -> {
          JSONArray list = arr(data);
          if (list.length() == 0)
            empty("Chưa có ưu đãi", "Các chương trình mới sẽ xuất hiện tại đây.");
          for (int i = 0; i < list.length(); i++) {
            JSONObject v = list.optJSONObject(i);
            LinearLayout c = inflateLayout(R.layout.item_voucher, content);
            ((TextView) c.findViewById(R.id.voucher_code)).setText(v.optString("code"));
            ((TextView) c.findViewById(R.id.voucher_name)).setText(v.optString("name"));
            ((TextView) c.findViewById(R.id.voucher_terms)).setText(
                "Đơn tối thiểu " + Ui.money(v.optLong("minOrder"))
                    + "\nHạn dùng: " + date(v.optString("endsAt")));
            c.findViewById(R.id.voucher_use).setOnClickListener(view -> {
              s.voucher = v.optString("code");
              toast("Đã chọn mã " + s.voucher);
              onBackPressed();
            });
            u.add(content, c);
            gap();
          }
        });
  }
}

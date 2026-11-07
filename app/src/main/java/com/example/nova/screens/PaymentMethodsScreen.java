package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp lựa chọn từ API và lưu phương thức cho phiên đặt hàng.
 * Bố cục liên quan: screen_payment_methods.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import org.json.JSONArray;
import org.json.JSONObject;

/** Chọn phương thức đang hoạt động và chuyển lựa chọn về màn thanh toán. */
public final class PaymentMethodsScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public PaymentMethodsScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_payment_methods; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = screen(layoutId());
    TextView total = root.findViewById(R.id.method_total);
    total.setText(s.checkoutQuoteTotal >= 0 ? Ui.money(s.checkoutQuoteTotal) : "Đang xác định");
    LinearLayout options = root.findViewById(R.id.method_options);
    get("/payment-methods", data -> {
      JSONArray rows = arr(data);
      for (int i = 0; i < rows.length(); i++) {
        JSONObject method = rows.optJSONObject(i);
        if (method == null || method.optString("code").isBlank()) continue;
        boolean selected = method.optString("code").equals(s.checkoutMethod);
        u.add(options, u.button(method.optString("name") + (selected ? "  ✓" : ""), selected, () -> {
          s.checkoutMethod = method.optString("code");
          onBackPressed();
        }));
        u.space(options, 10);
      }
      if (rows.length() == 0) u.add(options, u.text("Chưa có phương thức thanh toán.", 13, Ui.MUTED, false));
    });
  }
}

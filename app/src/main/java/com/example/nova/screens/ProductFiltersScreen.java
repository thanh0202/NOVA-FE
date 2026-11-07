package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Kiểm tra và áp dụng thương hiệu, khoảng giá và tùy chọn còn hàng khi chế độ hỗ trợ.
 * Bố cục liên quan: screen_product_filters.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.example.nova.BuildConfig;
import com.example.nova.MainActivity;
import com.example.nova.R;

/** Bộ lọc có màn hình riêng, dùng đúng điều kiện backend hỗ trợ. */
public final class ProductFiltersScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ProductFiltersScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_product_filters; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    LinearLayout root = screen(layoutId());
    EditText brand = root.findViewById(R.id.filter_brand);
    EditText minimum = root.findViewById(R.id.filter_min);
    EditText maximum = root.findViewById(R.id.filter_max);
    CheckBox available = root.findViewById(R.id.filter_available);
    brand.setText(s.brand);
    minimum.setText(s.minPrice);
    maximum.setText(s.maxPrice);
    available.setChecked(s.inStock);
    root.findViewById(R.id.filter_priority).setVisibility(BuildConfig.NOVA_MIDTERM_API ? View.VISIBLE : View.GONE);
    root.findViewById(R.id.filter_reset).setOnClickListener(v -> {
      brand.setText("");
      minimum.setText("");
      maximum.setText("");
      available.setChecked(false);
    });
    root.findViewById(R.id.filter_apply).setOnClickListener(v -> {
      String low = minimum.getText().toString().trim();
      String high = maximum.getText().toString().trim();
      try {
        long from = low.isEmpty() ? 0 : Long.parseLong(low);
        long to = high.isEmpty() ? 1_000_000_000_000L : Long.parseLong(high);
        if (from < 0 || to < from || to > 1_000_000_000_000L) {
          maximum.setError("Khoảng giá chưa hợp lệ");
          return;
        }
      } catch (NumberFormatException error) {
        maximum.setError("Nhập số tiền hợp lệ");
        return;
      }
      s.brand = brand.getText().toString().trim();
      s.minPrice = low;
      s.maxPrice = high;
      s.inStock = available.isChecked();
      onBackPressed();
    });
  }
}

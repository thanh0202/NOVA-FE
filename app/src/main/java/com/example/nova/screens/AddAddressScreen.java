package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Chọn chế độ tạo mới và dùng xử lý biểu mẫu chung của AddressFormScreen.
 * Bố cục liên quan: screen_add_address.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import com.example.nova.MainActivity;
import com.example.nova.R;

/** Thêm địa chỉ dùng chung việc kiểm tra dữ liệu với màn sửa địa chỉ. */
public final class AddAddressScreen extends AddressFormScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public AddAddressScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_add_address; }
}

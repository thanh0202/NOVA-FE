package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Gọi phần xử lý lưới chung của CatalogScreen ở chế độ kết quả từ khóa.
 * Bố cục liên quan: screen_search_results.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import com.example.nova.MainActivity;
import com.example.nova.R;

/** Tìm kiếm dùng chung cách tải sản phẩm với catalog, có bố cục kết quả riêng. */
public final class SearchResultsScreen extends CatalogScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public SearchResultsScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_search_results; }
}

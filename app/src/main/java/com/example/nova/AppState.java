package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Trạng thái dùng chung của FE: route hiện tại, lịch sử Back, bộ lọc, danh mục Trang chủ, người dùng và thông tin đặt hàng tạm.
 */
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import org.json.JSONObject;

/** Shared customer navigation and checkout draft; recovery credentials remain memory-only. */
public final class AppState {
  // route chọn Screen; các bộ lọc trong nhóm này được CatalogScreen chuyển thành query HTTP.
  public String route = "home", afterLogin = "home", query = "", category = "",
      sort = "bestselling", minPrice = "", maxPrice = "", voucher = "";
  public String searchDraft = "", searchCategory = "";
  // Bộ lọc riêng của Trang chủ giúp Back từ danh sách về vẫn giữ nhóm con/thương hiệu đã chọn.
  public String homeCategory = "dien-thoai", homeSubcategory = "", homeBrand = "";
  public boolean homeFeatured;
  // Bản nháp đặt hàng; checkoutKey giữ nguyên cho một lần đặt để hạn chế gửi trùng đơn.
  public String checkoutMethod = "COD", checkoutNote = "", checkoutKey = UUID.randomUUID().toString();
  public String brand = "", otpKind = "", otpEmail = "", otpChallenge = "";
  public boolean inStock, otpRemember;
  public String orderFilter = "";
  // Thông tin khôi phục chỉ giữ trong bộ nhớ của phiên giao diện.
  public String recoveryChallenge = "", recoveryCode = "";
  public long checkoutQuoteTotal = -1;
  public long entityId = 0, addressId = 0, afterLoginId = 0;
  // Mỗi lần dựng màn hình tăng generation; callback cũ phải bỏ qua khi số này thay đổi.
  public int generation = 0, cartCount = 0;
  public JSONObject user = new JSONObject();
  // Ngăn xếp route/ID phục vụ nút Back; chọn tab chính sẽ xóa nhánh lịch sử cũ.
  public final Deque<Destination> history = new ArrayDeque<>();
  public record Destination(String route, long id) {}
}

package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Tải nhận xét, lọc theo số sao và mở sửa đánh giá thuộc tài khoản hiện tại.
 * Bố cục liên quan: screen_product_reviews.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.nova.*;

/** Figma 03.02 · Đánh giá sản phẩm; independent review list and rating filter. */
public final class ProductReviewsScreen extends BaseScreen {
  private JSONArray reviews = new JSONArray();
  private int stars = 0;
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ProductReviewsScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_product_reviews; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    LinearLayout root = bindBody(layoutId());
    final long productId = s.entityId;
    root.findViewById(R.id.product_reviews_detail).setOnClickListener(v -> go("product", productId));
    RadioGroup filter = root.findViewById(R.id.product_reviews_filter);
    filter.setOnCheckedChangeListener((group, checked) -> {
      stars = checked == R.id.product_reviews_five ? 5
          : checked == R.id.product_reviews_four ? 4
          : checked == R.id.product_reviews_three ? 3
          : checked == R.id.product_reviews_two ? 2
          : checked == R.id.product_reviews_one ? 1 : 0;
      drawReviews();
    });
    get("/products/" + productId, data -> {
      JSONObject product = obj(data);
      ((TextView) root.findViewById(R.id.product_reviews_name)).setText(product.optString("name"));
      ((TextView) root.findViewById(R.id.product_reviews_rating)).setText(
          "★ " + product.optDouble("rating") + " / 5 · " + product.optInt("reviewCount") + " đánh giá");
    });
    get("/products/" + productId + "/reviews", data -> { reviews = arr(data); drawReviews(); });
  }

  // Dựng danh sách đánh giá với nội dung/số sao và thao tác thuộc tài khoản hiện tại.
  private void drawReviews() {
    content.removeAllViews();
    int count = 0;
    for (int i = reviews.length() - 1; i >= 0; i--) {
      JSONObject review = reviews.optJSONObject(i);
      if (review == null || (stars != 0 && review.optInt("rating") != stars)) continue;
      count++;
      LinearLayout card = u.card();
      u.add(card, u.text(review.optString("userName") + "   ★ " + review.optInt("rating"), 13, Ui.INK, true));
      u.space(card, 7);
      u.add(card, u.text(date(review.optString("updatedAt", review.optString("createdAt"))), 10, Ui.MUTED, false));
      u.space(card, 9);
      u.add(card, u.text(review.optString("content"), 13));
      if (loggedIn() && review.optLong("userId") == s.user.optLong("id"))
        u.add(card, u.link("Chỉnh sửa đánh giá", () -> reviewForm(review.optLong("orderItemId"), review)));
      u.add(content, card);
      gap();
    }
    if (count == 0) empty("Chưa có đánh giá", stars == 0
        ? "Khách hàng có thể đánh giá sau khi nhận sản phẩm." : "Chưa có đánh giá " + stars + " sao.");
  }
}

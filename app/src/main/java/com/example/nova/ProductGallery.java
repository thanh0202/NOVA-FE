package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Quản lý ảnh chi tiết sản phẩm, chọn thumbnail và đổi ảnh tương ứng biến thể/SKU.
 */
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;

/** Shows the product's ordered gallery and can jump to the selected SKU's photo. */
public final class ProductGallery {
  private final MainActivity activity;
  private final ImageView cover;
  private final LinearLayout thumbnails;
  private final JSONArray pictures;
  private final long productId;
  private final String name;

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ProductGallery(MainActivity activity, View layout, JSONObject product) {
    this.activity = activity;
    cover = layout.findViewById(R.id.product_image);
    thumbnails = layout.findViewById(R.id.product_gallery_thumbnails);
    pictures = product.optJSONArray("images");
    productId = product.optLong("id");
    name = product.optString("name");
    String first = product.optString("imageUrl", "");
    HashSet<String> seen = new HashSet<>();
    Ui ui = activity.ui();
    if (pictures != null) for (int i = 0; i < pictures.length(); i++) {
      JSONObject picture = pictures.optJSONObject(i);
      if (picture == null) continue;
      String url = picture.optString("url", "");
      if (url.isBlank() || !seen.add(url)) continue;
      if (first.isBlank()) first = url;
      ImageView thumbnail = new ImageView(activity);
      thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
      thumbnail.setBackground(ui.outline(Ui.TINT, 10, Ui.BORDER));
      thumbnail.setContentDescription("Xem ảnh " + seen.size() + " của " + name);
      thumbnail.setFocusable(true);
      thumbnail.setOnClickListener(view -> show(url));
      LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(ui.d(64), ui.d(64));
      size.rightMargin = ui.d(8);
      thumbnails.addView(thumbnail, size);
      activity.images().product(thumbnail, url, productId);
    }
    ((View) thumbnails.getParent()).setVisibility(seen.size() > 1 ? View.VISIBLE : View.GONE);
    show(first);
  }

  // Chọn ảnh tương ứng biến thể/SKU đang hiển thị.
  public void selectVariant(long variantId) {
    if (pictures == null || variantId <= 0) return;
    for (int i = 0; i < pictures.length(); i++) {
      JSONObject picture = pictures.optJSONObject(i);
      if (picture != null && picture.optLong("variantId") == variantId && !picture.optString("url", "").isBlank()) {
        show(picture.optString("url"));
        return;
      }
    }
  }

  // Hiển thị ảnh đang chọn và cập nhật trạng thái thumbnail.
  private void show(String url) {
    activity.images().product(cover, url, productId);
    cover.setContentDescription(name);
  }
}

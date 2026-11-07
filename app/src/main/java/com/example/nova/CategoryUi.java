package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Gắn ảnh, tên và trạng thái chọn của một danh mục vào layout item_home_category.
 */
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;

/** One photo and name for the same category on Home and Catalog. */
public final class CategoryUi {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  private CategoryUi() {}

  // Gắn nội dung của đối tượng dữ liệu vào các View con của layout.
  public static void bind(LinearLayout tile, JSONObject category,
      Images images, boolean selected) {
    String url = category.isNull("imageUrl") ? "" : category.optString("imageUrl", "");
    ImageView photo = tile.findViewById(R.id.category_icon);
    photo.setImageResource(R.drawable.ic_category_generic);
    photo.setTag(R.id.category_image_url, url);
    images.category(photo, url);
    TextView title = tile.findViewById(R.id.category_name);
    title.setText(category.optString("name"));
    title.setTextColor(selected ? Ui.BLUE : Ui.INK);
    tile.setSelected(selected);
    tile.setContentDescription("Danh mục " + category.optString("name")
        + (selected ? ", đang chọn" : ""));
  }
}

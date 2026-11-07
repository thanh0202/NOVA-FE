package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nạp giỏ, cập nhật số lượng, xóa món hoặc xóa giỏ và chuyển sang đặt hàng.
 * Bố cục liên quan: component_cart_item.xml, screen_cart.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.Settings;
import android.text.InputType;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import java.util.*;
import org.json.*;
import com.example.nova.*;

/** Customer screen: cart; its layout and behavior are edited together. */
public final class CartScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public CartScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_cart; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (BuildConfig.NOVA_MIDTERM_API) {
      LinearLayout root = screen(layoutId());
      ((TextView) root.findViewById(R.id.cart_count)).setText("Giỏ hàng của bạn");
      LinearLayout items = root.findViewById(R.id.cart_items);
      LinearLayout card = u.card();
      card.setGravity(Gravity.CENTER);
      card.addView(u.icon("node_6_2783_imgshoppingbag", 44),
          new LinearLayout.LayoutParams(u.d(44), u.d(44)));
      u.space(card, 16);
      TextView title = u.text("Mua sắm cùng NOVA", 19, Ui.INK, true);
      title.setGravity(Gravity.CENTER);
      u.add(card, title);
      u.space(card, 10);
      TextView message = u.text("Tính năng đặt hàng đang được hoàn thiện. Hãy khám phá sản phẩm bạn yêu thích.", 13, Ui.MUTED, false);
      message.setGravity(Gravity.CENTER);
      u.add(card, message);
      u.add(items, card);
      root.findViewById(R.id.cart_summary).setVisibility(View.GONE);
      root.findViewById(R.id.cart_clear).setVisibility(View.GONE);
      TextView explore = root.findViewById(R.id.cart_checkout);
      explore.setText("Khám phá sản phẩm");
      explore.setOnClickListener(v -> tab("categories"));
      return;
    }
    if (!requireLogin()) return;
    final LinearLayout layout = screen(R.layout.screen_cart);
    get("/cart", data -> {
      JSONObject cart = obj(data);
      JSONArray items = cart.optJSONArray("items");
      s.cartCount = cart.optInt("count"); updateCartBadge();
      if (items == null || items.length() == 0) {
        layout.setVisibility(View.GONE);
        empty("Giỏ hàng của bạn đang trống", "Khám phá công nghệ mới và thêm món đồ bạn yêu thích.");
        gap(); u.add(content, u.button("Tiếp tục mua sắm", true, () -> tab("products"))); return;
      }
      ((TextView) layout.findViewById(R.id.cart_count)).setText(s.cartCount + " loại sản phẩm trong giỏ");
      LinearLayout list = layout.findViewById(R.id.cart_items);
      for (int i = 0; i < items.length(); i++) {
        JSONObject item = items.optJSONObject(i);
        LinearLayout card = inflateLayout(R.layout.component_cart_item, list);
        ImageView photo = card.findViewById(R.id.cart_item_image);
        images.product(photo, item.optString("imageUrl"), item.optLong("productId"));
        photo.setContentDescription(item.optString("name"));
        ((TextView) card.findViewById(R.id.cart_item_name)).setText(item.optString("name"));
        ((TextView) card.findViewById(R.id.cart_item_variant)).setText(item.optString("variantName"));
        ((TextView) card.findViewById(R.id.cart_item_price)).setText(Ui.money(item.optLong("price")));
        ((TextView) card.findViewById(R.id.cart_item_quantity)).setText("  " + item.optInt("quantity") + "  ");
        card.findViewById(R.id.cart_item_minus).setOnClickListener(v -> {
          int quantity = item.optInt("quantity");
          if (quantity <= 1) confirm("Xóa sản phẩm?", "Sản phẩm sẽ được xóa khỏi giỏ hàng.", () ->
              send("DELETE", "/cart/items/" + item.optLong("id"), null, d -> activity.render()));
          else send("PUT", "/cart/items/" + item.optLong("id"), json("quantity", quantity - 1), d -> activity.render());
        });
        card.findViewById(R.id.cart_item_plus).setOnClickListener(v ->
            send("PUT", "/cart/items/" + item.optLong("id"), json("quantity", item.optInt("quantity") + 1), d -> activity.render()));
        card.findViewById(R.id.cart_item_remove).setOnClickListener(v ->
            confirm("Xóa sản phẩm?", item.optString("name"), () ->
                send("DELETE", "/cart/items/" + item.optLong("id"), null, d -> activity.render())));
        u.add(list, card); u.space(list, 16);
      }
      LinearLayout summary = layout.findViewById(R.id.cart_summary);
      u.labelPair(summary, "Tạm tính", Ui.money(cart.optLong("subtotal")), false);
      u.labelPair(summary, "Tổng cộng", Ui.money(cart.optLong("subtotal")), true);
      u.add(summary, u.text("Phí vận chuyển và ưu đãi được tính khi thanh toán.", 11, Ui.MUTED, false));
      layout.findViewById(R.id.cart_checkout).setOnClickListener(v -> go("checkout"));
      layout.findViewById(R.id.cart_clear).setOnClickListener(v ->
          confirm("Xóa giỏ hàng?", "Tất cả sản phẩm sẽ được xóa khỏi giỏ.", () ->
              send("DELETE", "/cart", null, d -> activity.render())));
    });
  }
}

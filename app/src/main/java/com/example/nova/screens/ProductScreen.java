package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiện ảnh, mô tả, giá và biến thể. Bản đầy đủ có thao tác yêu thích, giỏ hàng, mua ngay và đánh giá.
 * Bố cục liên quan: component_product_actions.xml, screen_product.xml.
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

/** Customer screen: product; its layout and behavior are edited together. */
public final class ProductScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ProductScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_product; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (BuildConfig.NOVA_MIDTERM_API) {
      renderMidterm();
      return;
    }
    final LinearLayout layout = screen(R.layout.screen_product);
    final long id = s.entityId;
    get("/products/" + id, data -> {
      JSONObject p = obj(data);
      ProductGallery gallery = new ProductGallery(activity, layout, p);
      ((TextView) layout.findViewById(R.id.product_brand)).setText(
          p.optString("brand").toUpperCase(Locale.ROOT) + " / CHÍNH HÃNG");
      ((TextView) layout.findViewById(R.id.product_name)).setText(p.optString("name"));
      ((TextView) layout.findViewById(R.id.product_rating)).setText(
          "★ " + p.optDouble("rating") + "  ·  " + p.optInt("reviewCount") + " đánh giá");
      layout.findViewById(R.id.product_rating).setOnClickListener(v -> go("product-reviews", id));
      View saveFavorite = layout.findViewById(R.id.product_favorite);
      View removeFavorite = layout.findViewById(R.id.product_unfavorite);
      final int[] favoriteEdits = {0};
      removeFavorite.setVisibility(View.GONE);
      saveFavorite.setOnClickListener(v -> {
        if (!loggedIn()) { s.afterLogin = "product"; s.afterLoginId = s.entityId; go("login"); return; }
        favoriteEdits[0]++;
        send("POST", "/favorites/" + id, null, d -> {
          saveFavorite.setVisibility(View.GONE); removeFavorite.setVisibility(View.VISIBLE);
          toast("Đã lưu vào yêu thích");
        });
      });
      removeFavorite.setOnClickListener(v -> {
        favoriteEdits[0]++;
        send("DELETE", "/favorites/" + id, null, d -> {
          saveFavorite.setVisibility(View.VISIBLE); removeFavorite.setVisibility(View.GONE);
          toast("Đã bỏ yêu thích");
        });
      });
      if (loggedIn()) {
        int stamp = s.generation;
        api.get("/favorites", (items, error, status) -> {
          if (stamp != s.generation || error != null || favoriteEdits[0] > 0) return;
          JSONArray list = arr(items);
          boolean saved = false;
          for (int i = 0; i < list.length(); i++)
            if (list.optJSONObject(i).optLong("id") == id) { saved = true; break; }
          saveFavorite.setVisibility(saved ? View.GONE : View.VISIBLE);
          removeFavorite.setVisibility(saved ? View.VISIBLE : View.GONE);
        });
      }
      ((TextView) layout.findViewById(R.id.product_price)).setText(Ui.money(p.optLong("price")));
      TextView old = layout.findViewById(R.id.product_old_price);
      if (p.optLong("originalPrice") > p.optLong("price")) {
        old.setText(Ui.money(p.optLong("originalPrice")));
        old.setPaintFlags(old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      } else old.setVisibility(View.GONE);
      JSONArray variants = p.optJSONArray("variants");
      final long[] selected = {0};
      RadioGroup group = layout.findViewById(R.id.product_variants);
      if (variants != null) for (int i = 0; i < variants.length(); i++) {
        JSONObject variant = variants.optJSONObject(i);
        RadioButton b = new RadioButton(this);
        b.setId(View.generateViewId());
        b.setText(variant.optString("name") + "  ·  " + Ui.money(variant.optLong("price"))
            + " · Còn " + variant.optInt("stock"));
        b.setTextSize(12); b.setTextColor(Ui.INK); b.setEnabled(variant.optInt("stock") > 0);
        group.addView(b);
        b.setOnCheckedChangeListener((button, checked) -> {
          if (checked) {
            selected[0] = variant.optLong("id");
            gallery.selectVariant(selected[0]);
            ((TextView) layout.findViewById(R.id.product_price)).setText(Ui.money(variant.optLong("price")));
          }
        });
        if (selected[0] == 0 && variant.optInt("stock") > 0) b.setChecked(true);
      }
      ((TextView) layout.findViewById(R.id.product_description)).setText(p.optString("description"));
      ((TextView) layout.findViewById(R.id.product_reviews_summary)).setText(
          p.optInt("reviewCount") == 0 ? "Chưa có đánh giá. Hãy chia sẻ sau khi nhận hàng."
              : "★ " + p.optDouble("rating") + " / 5 từ " + p.optInt("reviewCount") + " đánh giá");
      layout.findViewById(R.id.product_reviews_all).setOnClickListener(v -> go("product-reviews", id));
      LinearLayout actions = inflateLayout(R.layout.component_product_actions, footer);
      if (selected[0] == 0) {
        TextView add = actions.findViewById(R.id.product_add), buy = actions.findViewById(R.id.product_buy);
        add.setEnabled(false); buy.setEnabled(false); add.setAlpha(.5f); buy.setAlpha(.5f);
        buy.setText("Tạm hết hàng");
      }
      actions.findViewById(R.id.product_add).setOnClickListener(v -> addToCart(selected[0], false));
      actions.findViewById(R.id.product_buy).setOnClickListener(v -> addToCart(selected[0], true));
      u.add(footer, actions);
    });
  }

  // Dựng chi tiết sản phẩm bằng hợp đồng API GK và chỉ hiển thị thao tác được chế độ này hỗ trợ.
  private void renderMidterm() {
    LinearLayout layout = screen(layoutId());
    get("/products/" + s.entityId, data -> {
      JSONObject product = obj(data);
      ProductGallery gallery = new ProductGallery(activity, layout, product);
      text(layout, R.id.product_name, product.optString("name"));
      text(layout, R.id.product_brand, product.optString("brand"));
      text(layout, R.id.product_description, product.optString("description", product.optString("summary")));
      layout.findViewById(R.id.product_favorite).setVisibility(View.GONE);
      for (int id : new int[]{R.id.product_unfavorite, R.id.product_rating,
          R.id.product_old_price, R.id.product_reviews_summary, R.id.product_reviews_all}) {
        layout.findViewById(id).setVisibility(View.GONE);
      }
      JSONArray variants = product.optJSONArray("variants");
      RadioGroup choices = layout.findViewById(R.id.product_variants);
      if (variants != null) for (int i = 0; i < variants.length(); i++) {
        JSONObject variant = variants.optJSONObject(i);
        if (variant == null) continue;
        RadioButton option = new RadioButton(this);
        option.setId(View.generateViewId());
        String storage = variant.optInt("storageGb") > 0 ? " · " + variant.optInt("storageGb") + " GB" : "";
        option.setText(variant.optString("color") + storage + " · " + Ui.money(variant.optLong("price"))
            + " · Còn " + variant.optInt("stock"));
        choices.addView(option);
        option.setOnCheckedChangeListener((button, checked) -> {
          if (!checked) return;
          text(layout, R.id.product_price, Ui.money(variant.optLong("price")));
          gallery.selectVariant(variant.optLong("id"));
        });
        if (choices.getChildCount() == 1) option.setChecked(true);
      }
      LinearLayout actions = u.col();
      u.pad(actions, 12);
      TextView note = u.text("Tính năng đặt hàng đang được hoàn thiện.", 12, Ui.MUTED, false);
      note.setGravity(Gravity.CENTER);
      u.add(actions, note);
      u.space(actions, 8);
      u.add(actions, u.button("Khám phá sản phẩm khác", true, () -> tab("categories")));
      u.add(footer, actions);
      JSONArray specifications = product.optJSONArray("specifications");
      if (specifications != null && specifications.length() > 0) {
        gap();
        u.add(layout, u.text("Thông số kỹ thuật", 17, Ui.INK, true));
        for (int i = 0; i < specifications.length(); i++) {
          JSONObject spec = specifications.optJSONObject(i);
          if (spec != null) u.add(layout, u.text(spec.optString("name") + " · " + spec.optString("value"), 13, Ui.MUTED, false));
        }
      }
    });
  }

  // Chuyển dữ liệu thông số thành chuỗi hiển thị phù hợp trên màn chi tiết.
  private void text(View layout, int id, String value) {
    ((TextView) layout.findViewById(id)).setText(value);
  }
}

package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Ghép địa chỉ, phương thức, voucher và ghi chú. Yêu cầu báo giá trước khi gửi đơn.
 * Bố cục liên quan: screen_checkout.xml.
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

/** Customer screen: checkout; its layout and behavior are edited together. */
public final class CheckoutScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public CheckoutScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_checkout; }

  @Override
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  public void render() {
    if (!requireLogin()) return;
    final LinearLayout layout = screen(R.layout.screen_checkout);
    get("/cart", cartData -> {
          JSONObject cart = obj(cartData);
          if (cart.optInt("count") == 0) {
            layout.setVisibility(View.GONE);
            empty("Giỏ hàng trống", "Thêm sản phẩm trước khi thanh toán."); return;
          }
          CheckoutState state = new CheckoutState();
          state.method = s.checkoutMethod; state.key = s.checkoutKey;
          state.stamp = s.generation;
          TextView detail = layout.findViewById(R.id.checkout_address);
          layout.findViewById(R.id.checkout_change_address).setOnClickListener(v -> go("select-address"));
          String methodLabel = switch (s.checkoutMethod) {
            case "MOMO" -> "Ví MoMo";
            case "ZALOPAY" -> "ZaloPay";
            case "BANK_QR" -> "Chuyển khoản ngân hàng";
            default -> "Thanh toán khi nhận hàng (COD)";
          };
          TextView methodChoice = layout.findViewById(R.id.checkout_methods);
          methodChoice.setText(methodLabel + "  ›");
          methodChoice.setOnClickListener(v -> {
            s.checkoutQuoteTotal = state.quote == null ? -1 : state.quote.optLong("total", -1);
            go("payment-methods");
          });
          state.code = layout.findViewById(R.id.checkout_voucher);
          state.code.setText(s.voucher);
          layout.findViewById(R.id.checkout_apply).setOnClickListener(v -> refreshCheckoutQuote(state));
          layout.findViewById(R.id.checkout_vouchers).setOnClickListener(v -> go("vouchers"));
          state.note = layout.findViewById(R.id.checkout_note);
          state.note.setText(s.checkoutNote);
          state.note.addTextChangedListener(new android.text.TextWatcher() {
            // Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó.
            public void beforeTextChanged(CharSequence s, int a, int count, int after) {}
            // Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu.
            public void onTextChanged(CharSequence text, int start, int before, int count) { s.checkoutNote = text.toString(); }
            // Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi.
            public void afterTextChanged(android.text.Editable e) {}
          });
          state.summary = layout.findViewById(R.id.checkout_summary);
          state.place = layout.findViewById(R.id.checkout_place);
          state.place.setOnClickListener(v -> placeCheckout(state));
          state.code.addTextChangedListener(
              new android.text.TextWatcher() {
                // Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó.
                public void beforeTextChanged(CharSequence s, int a, int count, int after) {}

                // Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu.
                public void onTextChanged(CharSequence s, int a, int before, int count) {
                  state.quote = null;
                  state.place.setEnabled(false);
                  state.place.setAlpha(.55f);
                  state.place.setText("Áp dụng mã để cập nhật tổng tiền");
                }

                // Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi.
                public void afterTextChanged(android.text.Editable e) {}
              });
          get(
              "/addresses",
              data -> {
                JSONArray list = arr(data);
                JSONObject selected = null;
                for (int i = 0; i < list.length(); i++)
                  if (list.optJSONObject(i).optLong("id") == s.addressId)
                    selected = list.optJSONObject(i);
                if (selected == null && list.length() > 0) {
                  selected = list.optJSONObject(0);
                  for (int i = 0; i < list.length(); i++)
                    if (list.optJSONObject(i).optBoolean("isDefault"))
                      selected = list.optJSONObject(i);
                }
                if (selected == null) {
                  s.addressId = 0;
                  detail.setText("Bạn chưa có địa chỉ. Nhấn Thay đổi để thêm địa chỉ nhận hàng.");
                } else {
                  s.addressId = selected.optLong("id");
                  detail.setText(
                      selected.optString("name")
                          + " · "
                          + selected.optString("phone")
                          + "\n"
                          + formatAddress(selected));
                }
                refreshCheckoutQuote(state);
              });
        });
  }

  private static final class CheckoutState {
    EditText code, note;
    LinearLayout summary;
    TextView place;
    JSONObject quote;
    String method = "COD", key = UUID.randomUUID().toString();
    int stamp;
  }

  // Ghép địa chỉ, phương thức, voucher và ghi chú thành JSON dùng để yêu cầu báo giá.
  private JSONObject quoteBody(CheckoutState state) {
    JSONObject body = json("voucherCode", state.code.getText().toString().trim());
    if (s.addressId > 0)
      try {
        body.put("addressId", s.addressId);
      } catch (JSONException ignored) {
      }
    return body;
  }

  // Gọi API báo giá và bỏ qua phản hồi nếu thông tin đặt hàng đã đổi trong lúc chờ.
  private void refreshCheckoutQuote(CheckoutState state) {
    state.quote = null;
    state.place.setEnabled(false);
    state.place.setAlpha(.55f);
    state.place.setText("Đang cập nhật giá…");
    api.request(
        "POST",
        "/checkout/quote",
        quoteBody(state),
        (data, error, status) -> {
          if (state.stamp != s.generation) return;
          if (error != null) {
            state.summary.removeAllViews();
            u.add(state.summary, u.text(error, 13, Ui.MUTED, false));
            state.place.setText("Cập nhật giá để tiếp tục");
            return;
          }
          state.quote = obj(data);
          s.voucher = state.code.getText().toString().trim();
          showCheckoutQuote(state);
        });
  }

  // Hiển thị các dòng tiền và tổng thanh toán từ báo giá do BE trả.
  private void showCheckoutQuote(CheckoutState state) {
    state.summary.removeAllViews();
    JSONObject q = state.quote;
    JSONArray items = q.optJSONArray("items");
    if (items != null)
      for (int i = 0; i < items.length(); i++) {
        JSONObject item = items.optJSONObject(i);
        u.labelPair(
            state.summary,
            item.optString("name") + " × " + item.optInt("quantity"),
            Ui.money(item.optLong("price") * item.optInt("quantity")),
            false);
      }
    u.labelPair(state.summary, "Tạm tính", Ui.money(q.optLong("subtotal")), false);
    u.labelPair(
        state.summary, "Ưu đãi voucher", "−" + Ui.money(q.optLong("voucherDiscount")), false);
    u.labelPair(
        state.summary, "Ưu đãi thành viên", "−" + Ui.money(q.optLong("memberDiscount")), false);
    u.labelPair(state.summary, "Phí vận chuyển", Ui.money(q.optLong("shippingFee")), false);
    u.labelPair(state.summary, "Tổng thanh toán", Ui.money(q.optLong("total")), true);
    state.place.setText(
        s.addressId > 0 ? "Đặt hàng · " + Ui.money(q.optLong("total")) : "Thêm địa chỉ để đặt hàng");
    state.place.setEnabled(s.addressId > 0);
    state.place.setAlpha(s.addressId > 0 ? 1f : .55f);
  }

  // So sánh dữ liệu đặt hàng hiện tại với bản đã dùng để nhận báo giá.
  private boolean sameQuote(JSONObject a, JSONObject b) {
    for (String key :
        new String[] {"subtotal", "discount", "memberDiscount", "shippingFee", "total"})
      if (a.optLong(key) != b.optLong(key)) return false;
    return String.valueOf(a.opt("items")).equals(String.valueOf(b.opt("items")));
  }

  // Kiểm tra báo giá/địa chỉ, gửi tạo đơn với khóa chống lặp và chuyển đến kết quả.
  private void placeCheckout(CheckoutState state) {
    if (state.quote == null || s.addressId == 0) return;
    JSONObject displayed = state.quote;
    state.place.setEnabled(false);
    state.place.setText("Đang xác nhận giá…");
    api.request(
        "POST",
        "/checkout/quote",
        quoteBody(state),
        (data, error, status) -> {
          if (state.stamp != s.generation) return;
          if (error != null) {
            state.quote = null;
            state.place.setText("Cập nhật giá để tiếp tục");
            alert("Chưa thể xác nhận giá", error);
            return;
          }
          JSONObject latest = obj(data);
          state.quote = latest;
          if (!sameQuote(displayed, latest)) {
            showCheckoutQuote(state);
            alert(
                "Giá đã được cập nhật",
                "Giá hoặc ưu đãi vừa thay đổi. Vui lòng xem lại tổng thanh toán và nhấn Đặt hàng để"
                    + " xác nhận.");
            return;
          }
          JSONObject body =
              json(
                  "addressId",
                  s.addressId,
                  "method",
                  state.method,
                  "voucherCode",
                  state.code.getText().toString().trim(),
                  "note",
                  state.note.getText().toString(),
                  "checkoutKey",
                  state.key,
                  "expectedTotal",
                  latest.optLong("total"));
          api.request(
              "POST",
              "/orders",
              body,
              (orderData, issue, code) -> {
                if (state.stamp != s.generation) return;
                if (issue != null) {
                  showCheckoutQuote(state);
                  alert("Chưa thể đặt hàng", issue);
                  if (code == 409) refreshCheckoutQuote(state);
                  return;
                }
                s.cartCount = 0;
                s.voucher = "";
                s.checkoutNote = ""; s.checkoutMethod = "COD"; s.checkoutKey = UUID.randomUUID().toString();
                s.history.clear();
                long orderId = obj(orderData).optLong("id");
                if (state.method.equals("COD")) {
                  activity.replace("order-success", orderId);
                } else {
                  state.place.setText("Đang mở thanh toán…");
                  api.request("POST", "/orders/" + orderId + "/payments", null,
                      (paymentData, paymentIssue, paymentCode) -> {
                        if (state.stamp != s.generation) return;
                        if (paymentIssue != null) {
                          activity.replace("order", orderId);
                          alert("Chưa mở được thanh toán", paymentIssue
                              + "\nĐơn hàng đã được tạo. Bạn có thể tiếp tục thanh toán trong chi tiết đơn.");
                          return;
                        }
                        activity.replace("payment", obj(paymentData).optLong("id"));
                      });
                }
              });
        });
  }
}

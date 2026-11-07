package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Chọn hình thức, món, số lượng và lý do rồi gửi yêu cầu.
 * Bố cục liên quan: screen_return_request.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import static com.example.nova.ApiClient.json;

import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.nova.*;

/** Figma 05.14 · Yêu cầu đổi trả; one validated full-page form instead of an order dialog. */
public final class ReturnRequestScreen extends BaseScreen {
  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ReturnRequestScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_return_request; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    LinearLayout root = bindBody(layoutId());
    final long orderId = s.entityId;
    TextView submit = root.findViewById(R.id.return_request_submit);
    submit.setEnabled(false);
    root.findViewById(R.id.return_request_cancel).setOnClickListener(v -> go("order", orderId));
    get("/orders/" + orderId, data -> {
      JSONObject order = obj(data);
      ((TextView) root.findViewById(R.id.return_request_number)).setText(order.optString("number"));
      if (!order.optString("status").equals("DELIVERED")) {
        ((TextView) root.findViewById(R.id.return_request_notice)).setText(
            "Chỉ có thể yêu cầu đổi trả sau khi đơn hàng được giao thành công.");
        return;
      }
      JSONArray items = order.optJSONArray("items");
      if (items == null || items.length() == 0) return;
      LinearLayout itemHost = root.findViewById(R.id.return_request_items);
      List<CheckBox> boxes = new ArrayList<>();
      List<EditText> quantities = new ArrayList<>();
      for (int i = 0; i < items.length(); i++) {
        JSONObject item = items.optJSONObject(i);
        LinearLayout card = u.card();
        CheckBox box = new CheckBox(this);
        box.setText(item.optString("name"));
        box.setTextColor(Ui.INK);
        box.setTextSize(13);
        box.setChecked(true);
        box.setMinHeight(u.d(48));
        u.add(card, box);
        u.add(card, u.text(item.optString("variantName") + " · " + Ui.money(item.optLong("price")),
            12, Ui.MUTED, false));
        EditText quantity = u.field(card, "Số lượng (tối đa " + item.optInt("quantity") + ")",
            String.valueOf(item.optInt("quantity")), InputType.TYPE_CLASS_NUMBER);
        quantity.setContentDescription("Số lượng đổi trả " + item.optString("name"));
        box.setOnCheckedChangeListener((button, checked) -> quantity.setEnabled(checked));
        boxes.add(box);
        quantities.add(quantity);
        u.add(itemHost, card);
        u.space(itemHost, 12);
      }
      submit.setEnabled(true);
      submit.setOnClickListener(v -> {
        JSONArray selected = new JSONArray();
        for (int i = 0; i < boxes.size(); i++) if (boxes.get(i).isChecked()) {
          int quantity;
          try { quantity = Integer.parseInt(quantities.get(i).getText().toString().trim()); }
          catch (NumberFormatException invalid) { quantities.get(i).setError("Số lượng không hợp lệ"); return; }
          JSONObject item = items.optJSONObject(i);
          if (quantity < 1 || quantity > item.optInt("quantity")) {
            quantities.get(i).setError("Nhập số lượng từ 1 đến " + item.optInt("quantity"));
            return;
          }
          selected.put(json("orderItemId", item.optLong("id"), "quantity", quantity));
        }
        if (selected.length() == 0) { toast("Vui lòng chọn sản phẩm cần đổi trả"); return; }
        EditText reason = root.findViewById(R.id.return_request_reason);
        String value = reason.getText().toString().trim();
        if (value.isEmpty()) { reason.setError("Vui lòng nhập lý do đổi trả"); return; }
        boolean exchange = ((RadioButton) root.findViewById(R.id.return_request_exchange)).isChecked();
        submit.setEnabled(false);
        int generation = s.generation;
        api.request("POST", "/orders/" + orderId + "/returns", json(
            "type", exchange ? "EXCHANGE" : "RETURN_REFUND", "reason", value, "items", selected),
            (result, error, status) -> {
              if (generation != s.generation || isFinishing() || isDestroyed()) return;
              submit.setEnabled(true);
              if (error != null) {
                if (status == 401) { s.afterLogin = "return-request"; s.afterLoginId = orderId; go("login"); }
                else alert("Chưa thể gửi yêu cầu", error);
                return;
              }
              toast("Đã gửi yêu cầu đổi trả");
              go("return", obj(result).optLong("id"));
            });
      });
    });
  }
}

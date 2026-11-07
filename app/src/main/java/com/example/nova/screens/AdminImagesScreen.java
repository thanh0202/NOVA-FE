package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Giao diện quản lý ảnh cho ADMIN: tìm sản phẩm, xem số chỗ còn lại, chọn nhiều ảnh và gửi multipart đến API quản trị.
 * Bố cục liên quan: screen_admin_images.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.net.Uri;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/** Admin-only gallery management. The API enforces authorization and capacity again on upload. */
public final class AdminImagesScreen extends BaseScreen {
  private int stamp, requestSequence, page;
  private String token, query = "";
  private boolean disposed, busy;
  private LinearLayout results;
  private EditText search;
  private TextView searchButton;
  private JSONObject product;
  private List<Uri> selected = new ArrayList<>();

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public AdminImagesScreen(MainActivity activity) { super(activity); }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_admin_images; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    if (!requireLogin()) return;
    bindBody(layoutId());
    stamp = s.generation;
    token = session.token();
    if (!"ADMIN".equals(s.user.optString("role"))) {
      u.add(content, u.text("Chỉ quản trị viên được quản lý ảnh sản phẩm.", 15, Ui.INK, true));
      return;
    }
    if (s.entityId > 0) loadProduct();
    else showSearch();
  }

  // Kiểm tra phiên bản màn quản lý ảnh và vòng đời Activity trước khi dùng phản hồi API.
  private boolean active() {
    return !disposed && stamp == s.generation && !isFinishing() && !isDestroyed()
        && token.equals(session.token());
  }

  // Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn.
  @Override public void onDispose() { disposed = true; super.onDispose(); }

  // Xóa trạng thái phiên đã hết hạn và yêu cầu đăng nhập lại trước khi quản lý ảnh.
  private boolean expired(int status) {
    if (status != 401 || disposed || stamp != s.generation || isFinishing() || isDestroyed()
        || !session.token().isEmpty()) return false;
    s.afterLogin = "admin-images";
    s.afterLoginId = s.entityId;
    s.user = new JSONObject();
    toast("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
    tab("login");
    return true;
  }

  // Dựng vùng tìm sản phẩm và trạng thái của danh sách quản lý ảnh.
  private void showSearch() {
    u.add(content, u.text("Chọn sản phẩm cần thêm ảnh", 18, Ui.INK, true));
    u.space(content, 8);
    u.add(content, u.text("Danh sách bao gồm sản phẩm đang bán, bản nháp và đã lưu trữ.", 12, Ui.MUTED, false));
    u.space(content, 16);
    search = u.field(content, "Tìm theo tên hoặc mã sản phẩm", query, TEXT);
    search.setHint("Ví dụ: iPhone hoặc samsung");
    search.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
    search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    search.setOnEditorActionListener((view, actionId, event) -> {
      if (actionId != EditorInfo.IME_ACTION_SEARCH) return false;
      startSearch(); return true;
    });
    u.space(content, 10);
    searchButton = u.button("Tìm sản phẩm", true, this::startSearch);
    u.add(content, searchButton);
    u.space(content, 18);
    results = u.col();
    u.add(content, results);
    loadPage();
  }

  // Chuẩn hóa từ khóa, đặt lại phân trang và bắt đầu tìm sản phẩm.
  private void startSearch() {
    if (busy) return;
    query = search.getText().toString().trim();
    page = 0;
    loadPage();
  }

  // Lấy một trang kết quả tìm sản phẩm và kiểm tra yêu cầu còn thuộc màn hình hiện tại.
  private void loadPage() {
    busy = true;
    searchButton.setEnabled(false);
    results.removeAllViews();
    u.add(results, u.text("Đang tải danh sách…", 13, Ui.MUTED, false));
    int sequence = ++requestSequence;
    String path = "/admin/products?page=" + page + "&size=12&q="
        + ApiClient.encode(query);
    api.get(path, (data, error, status) -> {
      if (expired(status) || !active() || sequence != requestSequence) return;
      busy = false;
      searchButton.setEnabled(true);
      results.removeAllViews();
      if (error != null) { showError(results, error, this::loadPage); return; }
      JSONObject response = obj(data);
      JSONArray items = response.optJSONArray("items");
      if (items == null || items.length() == 0) {
        u.add(results, u.text("Không tìm thấy sản phẩm phù hợp.", 13, Ui.MUTED, false));
        return;
      }
      u.add(results, u.text(response.optLong("total") + " sản phẩm · Trang " + (page + 1)
          + "/" + Math.max(1, response.optLong("totalPages")), 12, Ui.MUTED, false));
      u.space(results, 12);
      for (int i = 0; i < items.length(); i++) {
        JSONObject item = items.optJSONObject(i);
        if (item == null) continue;
        LinearLayout card = u.card();
        LinearLayout row = u.row();
        ImageView cover = new ImageView(this);
        cover.setScaleType(ImageView.ScaleType.FIT_CENTER);
        images.product(cover, item.optString("imageUrl"), item.optLong("id"));
        row.addView(cover, new LinearLayout.LayoutParams(u.d(64), u.d(64)));
        LinearLayout labels = u.col();
        labels.setPadding(u.d(12), 0, 0, 0);
        u.add(labels, u.text(item.optString("name"), 14, Ui.INK, true));
        u.space(labels, 5);
        u.add(labels, u.text(item.optString("code") + " · " + productStatus(item.optString("status")), 11, Ui.MUTED, false));
        u.space(labels, 5);
        u.add(labels, u.text("Quản lý ảnh →", 12, Ui.BLUE, true));
        u.weight(row, labels);
        u.add(card, row);
        u.action(card, () -> go("admin-images", item.optLong("id")));
        card.setContentDescription("Quản lý ảnh " + item.optString("name"));
        u.add(results, card);
        u.space(results, 10);
      }
      LinearLayout pager = u.row();
      TextView previous = u.button("← Trang trước", false, () -> { page--; loadPage(); });
      previous.setEnabled(page > 0);
      previous.setAlpha(page > 0 ? 1f : .4f);
      TextView next = u.button("Trang sau →", false, () -> { page++; loadPage(); });
      next.setEnabled(response.optBoolean("hasNext"));
      next.setAlpha(response.optBoolean("hasNext") ? 1f : .4f);
      u.weight(pager, previous);
      u.weight(pager, next);
      u.add(results, pager);
    });
  }

  // Lấy chi tiết sản phẩm kèm bộ ảnh trước khi mở phần thêm ảnh.
  private void loadProduct() {
    content.removeAllViews();
    u.add(content, u.text("Đang tải ảnh sản phẩm…", 13, Ui.MUTED, false));
    busy = true;
    api.get("/admin/products/" + s.entityId, (data, error, status) -> {
      if (expired(status) || !active()) return;
      busy = false;
      if (error != null) {
        content.removeAllViews(); showError(content, error, this::loadProduct); return;
      }
      product = obj(data);
      showProduct();
    });
  }

  // Hiển thị bộ ảnh, giới hạn số ảnh và các nút của sản phẩm đang quản lý.
  private void showProduct() {
    content.removeAllViews();
    u.add(content, u.text(product.optString("name"), 20, Ui.INK, true));
    u.space(content, 6);
    u.add(content, u.text(product.optString("code") + " · " + productStatus(product.optString("status")), 12, Ui.MUTED, false));
    u.space(content, 16);
    JSONArray photos = product.optJSONArray("images");
    int count = product.optInt("imageCount", photos == null ? 0 : photos.length());
    int max = product.optInt("maxImages", 0);
    int available = Math.max(0, max - count);
    u.add(content, u.text("Ảnh hiện tại: " + count + "/" + max, 15, Ui.INK, true));
    u.space(content, 10);
    if (photos != null) {
      for (int i = 0; i < photos.length(); i += 2) {
        LinearLayout row = u.row();
        for (int j = i; j < Math.min(i + 2, photos.length()); j++) {
          JSONObject photo = photos.optJSONObject(j);
          if (photo == null) continue;
          ImageView image = new ImageView(this);
          image.setScaleType(ImageView.ScaleType.FIT_CENTER);
          image.setBackground(u.bg(0xFFFFFFFF, 12));
          images.product(image, photo.optString("url"), product.optLong("id"));
          image.setContentDescription("Ảnh " + (j + 1) + " của " + product.optString("name"));
          LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, u.d(150), 1);
          params.setMargins(u.d(3), u.d(3), u.d(3), u.d(3));
          row.addView(image, params);
        }
        if (i + 1 >= photos.length()) row.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1));
        u.add(content, row);
      }
    }
    u.space(content, 18);
    if (available == 0) {
      u.add(content, u.text(max == 0 ? "Chưa xác định được giới hạn ảnh. Hãy tải lại sản phẩm."
          : "Sản phẩm đã đủ số ảnh tối đa.", 13, Ui.MUTED, false));
      u.add(content, u.button("Tải lại ảnh", false, this::loadProduct));
      return;
    }
    u.add(content, u.text("Có thể thêm " + available + " ảnh. Ảnh được tối ưu kích thước trước khi tải lên.", 12, Ui.MUTED, false));
    u.space(content, 10);
    TextView choose = u.button(selected.isEmpty() ? "Chọn ảnh từ thiết bị" : "Chọn lại ảnh", false,
        () -> chooseImages(available));
    choose.setEnabled(!busy);
    u.add(content, choose);
    if (!selected.isEmpty()) {
      u.space(content, 10);
      u.add(content, u.text("Đã chọn " + selected.size() + " ảnh", 13, Ui.INK, true));
      if (selected.size() > available) {
        u.space(content, 6);
        u.add(content, u.text("Số chỗ còn lại đã thay đổi. Hãy chọn lại tối đa " + available + " ảnh.", 12, Ui.MUTED, false));
      }
      u.space(content, 10);
      TextView upload = u.button(busy ? "Đang tải ảnh lên…" : "Tải lên " + selected.size() + " ảnh", true, this::upload);
      upload.setEnabled(!busy && selected.size() <= available);
      u.add(content, upload);
    }
    if (busy) {
      u.space(content, 10);
      u.add(content, u.text("Đang tải ảnh. Bạn có thể quay lại sản phẩm sau để kiểm tra kết quả.", 12, Ui.MUTED, false));
    }
    u.space(content, 10);
    TextView refresh = u.button("Làm mới ảnh", false, this::loadProduct);
    refresh.setEnabled(!busy);
    u.add(content, refresh);
  }

  // Mở bộ chọn nhiều ảnh với số lượng tối đa bằng số chỗ ảnh còn lại.
  private void chooseImages(int available) {
    if (busy || !active()) return;
    activity.pickImages(true, available, (uris, error) -> {
      if (!active()) return;
      if (error != null) { alert("Chưa chọn được ảnh", error); return; }
      if (uris.isEmpty()) return;
      selected = new ArrayList<>(uris);
      showProduct();
    });
  }

  // Gửi ảnh đã được kiểm tra đến điểm nhận upload và chỉ nhận URL trả về hợp lệ.
  private void upload() {
    if (busy || selected.isEmpty() || !active()) return;
    busy = true;
    showProduct();
    api.uploadImages(activity, new ArrayList<>(selected), "/admin/products/" + product.optLong("id") + "/images",
        "files", 1600, (data, error, status) -> {
          if (expired(status) || !active()) return;
          busy = false;
          if (error != null) {
            showProduct();
            alert("Chưa tải được ảnh", error + "\nBạn có thể thử tải lại các ảnh đã chọn.");
            return;
          }
          selected.clear();
          product = obj(data);
          showProduct();
          toast("Đã lưu ảnh sản phẩm");
        });
  }

  // Đưa lỗi vào vùng trạng thái và cập nhật thao tác phù hợp của giao diện.
  private void showError(LinearLayout parent, String message, Runnable retry) {
    u.add(parent, u.text(message, 13, Ui.MUTED, false));
    u.space(parent, 10);
    u.add(parent, u.button("Thử lại", false, retry));
  }

  // Ghép tình trạng sản phẩm thành nhãn trong giao diện quản lý ảnh.
  private String productStatus(String status) {
    return switch (status) {
      case "active" -> "Đang bán";
      case "draft" -> "Bản nháp";
      case "archived" -> "Đã lưu trữ";
      default -> status;
    };
  }
}

package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Hiển thị hồ sơ tóm tắt, đăng xuất, lối vào địa chỉ, đơn hàng, thành viên và các tiện ích.
 * Bố cục liên quan: component_account_identity.xml, screen_account.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.BuildConfig;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/** Account overview. Profile values come from /me; labels and decoration are local assets. */
public final class AccountScreen extends BaseScreen {
  private static final String[] ORDER_LABELS = {"Chờ xác nhận", "Đang xử lý", "Đang giao"};
  private static final String[] ORDER_ICONS = {
      "node_6_2783_imgwallet", "node_6_2783_imgpackage", "node_6_2783_imgtruck"};
  private static final String[] ORDER_STATUSES = {"AWAITING_CONFIRMATION", "PROCESSING", "SHIPPING"};

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public AccountScreen(MainActivity activity) { super(activity); }

  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.screen_account; }

  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    LinearLayout layout = screen(layoutId());
    u.heading(layout.findViewById(R.id.account_title));
    u.action(layout.findViewById(R.id.account_notifications), () -> go("notifications"));
    content = layout.findViewById(R.id.account_body);
    if (!loggedIn()) {
      showGuest();
      return;
    }
    get("/me", data -> {
      s.user = obj(data);
      content.removeAllViews();
      showIdentity(s.user);
      showMembership(s.user);
      showOrders();
      showSettings();
      showFooter();
    });
  }

  // Dựng giao diện tài khoản khi chưa có phiên và gắn nút đăng nhập/đăng ký.
  private void showGuest() {
    LinearLayout card = u.card();
    card.setGravity(Gravity.CENTER_HORIZONTAL);
    ImageView logo = u.icon("nova_logo", 48);
    card.addView(logo, new LinearLayout.LayoutParams(u.d(48), u.d(48)));
    u.space(card, 18);
    TextView title = u.text("Chào mừng đến với NOVA", 20, Ui.INK, true);
    title.setGravity(Gravity.CENTER);
    u.add(card, title);
    u.space(card, 12);
    TextView message = u.text("Đăng nhập để theo dõi đơn hàng, nhận điểm thưởng và đặc quyền thành viên.",
        13, Ui.MUTED, false);
    message.setGravity(Gravity.CENTER);
    u.add(card, message);
    u.space(card, 20);
    u.add(card, u.button("Đăng nhập", true, () -> openAuth("login")));
    u.space(card, 10);
    u.add(card, u.button("Tạo tài khoản", false, () -> openAuth("register")));
    u.add(content, card);
    u.space(content, 20);
    LinearLayout help = u.card();
    addSetting(help, "node_6_2783_imgshieldcheck", "Trung tâm hỗ trợ", "", () -> go("support"), false);
    u.add(content, help);
  }

  // Lưu đích Tài khoản rồi mở màn hình đăng nhập hoặc đăng ký.
  private void openAuth(String route) {
    s.afterLogin = "account";
    go(route);
  }

  // Hiển thị tên, email, avatar và thao tác mở hồ sơ.
  private void showIdentity(JSONObject profile) {
    LinearLayout identity = inflateLayout(R.layout.component_account_identity, content);
    identity.setTag("account-identity");
    TextView name = identity.findViewById(R.id.account_name);
    TextView email = identity.findViewById(R.id.account_email);
    name.setText(profile.optString("name", "Khách NOVA"));
    email.setText(profile.optString("email"));
    ImageView avatar = identity.findViewById(R.id.account_avatar);
    avatar.setBackground(u.bg(Ui.TINT, 28));
    avatar.setClipToOutline(true);
    avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
    if (!profile.isNull("avatarUrl") && !profile.optString("avatarUrl").isBlank())
      images.category(avatar, profile.optString("avatarUrl"));
    u.action(identity.findViewById(R.id.account_edit_profile), () -> go("profile"));
    u.action(identity, () -> go("profile"));
    u.add(content, identity);
    u.space(content, 20);
  }

  // Hiển thị thông tin hạng/điểm thành viên khi chế độ backend hỗ trợ.
  private void showMembership(JSONObject profile) {
    FrameLayout member = new FrameLayout(this);
    member.setTag("account-member-card");
    u.blueCard(member, 24);
    member.setClipToOutline(true);
    member.setElevation(u.d(3));
    FrameLayout.LayoutParams glow = new FrameLayout.LayoutParams(u.d(235), u.d(235), Gravity.TOP | Gravity.END);
    glow.topMargin = u.d(-60);
    glow.rightMargin = u.d(-81);
    member.addView(u.icon("account_member_glow", 235), glow);
    FrameLayout.LayoutParams orbit = new FrameLayout.LayoutParams(u.d(212), u.d(212), Gravity.TOP | Gravity.END);
    orbit.topMargin = u.d(-55);
    orbit.rightMargin = u.d(-60);
    member.addView(u.icon("node_6_2783_imgorbitaldetail", 212), orbit);
    View inset = new View(this);
    GradientDrawable frostedSurface = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        new int[] {0x1CFFFFFF, 0x05FFFFFF});
    frostedSurface.setCornerRadius(u.d(20));
    frostedSurface.setStroke(u.d(1), 0x30FFFFFF);
    inset.setBackground(frostedSurface);
    FrameLayout.LayoutParams insetParams = new FrameLayout.LayoutParams(-1, -1);
    insetParams.setMargins(u.d(8), u.d(8), u.d(8), u.d(8));
    member.addView(inset, insetParams);

    LinearLayout card = u.col();
    u.pad(card, 18);
    LinearLayout top = u.row();
    u.weight(top, u.text("NOVA / THÀNH VIÊN", 12, Color.WHITE, false));
    top.addView(u.icon("node_6_2783_imgnfc", 22));
    u.add(card, top);
    u.space(card, 24);
    TextView tier = u.text("Thành viên NOVA", 26, Color.WHITE, true);
    tier.setSingleLine(true);
    tier.setEllipsize(TextUtils.TruncateAt.END);
    u.add(card, tier);
    u.space(card, 7);
    u.add(card, u.text("MÃ THÀNH VIÊN  •  NV "
        + String.format(Locale.ROOT, "%06d", profile.optLong("id")), 10, 0xFFBFDAFF, false));
    u.space(card, 24);
    LinearLayout bottom = u.row(), pointColumn = u.col();
    u.add(pointColumn, u.text("Điểm tích lũy", 10, 0xFFBFDAFF, false));
    u.space(pointColumn, 3);
    TextView points = u.text(BuildConfig.NOVA_MIDTERM_API ? "—" : "0", 21, Color.WHITE, false);
    u.add(pointColumn, points);
    u.weight(bottom, pointColumn);
    TextView privilege = u.text("Xem đặc quyền  ↗", 10, Color.WHITE, false);
    privilege.setPadding(u.d(11), u.d(8), u.d(11), u.d(8));
    privilege.setBackground(u.outline(0x1AFFFFFF, 100, 0x33FFFFFF));
    bottom.addView(privilege);
    u.add(card, bottom);
    member.addView(card, new FrameLayout.LayoutParams(-1, -1));
    u.action(member, () -> go("membership"));
    u.add(content, member, 202);
    u.space(content, 20);
    // The midterm API has no membership endpoint; keep its points unspecified.
    if (!BuildConfig.NOVA_MIDTERM_API) get("/membership", data -> {
      JSONObject membership = obj(data);
      tier.setText("Hạng " + membership.optString("tier"));
      points.setText(NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"))
          .format(membership.optLong("points")));
    });
  }

  // Dựng danh sách hoặc các lối truy cập đơn hàng từ dữ liệu hiện tại.
  private void showOrders() {
    LinearLayout heading = u.row();
    u.weight(heading, u.text("Đơn hàng của tôi", 17, Ui.INK, true));
    heading.addView(u.link("Xem tất cả", () -> { s.orderFilter = ""; go("orders"); }));
    u.add(content, heading);
    u.space(content, 12);
    LinearLayout statuses = u.row();
    statuses.setTag("account-order-statuses");
    statuses.setBackground(u.bg(Color.WHITE, 20));
    List<TextView> badges = new ArrayList<>();
    for (int i = 0; i < ORDER_LABELS.length; i++) {
      LinearLayout item = u.col();
      item.setGravity(Gravity.CENTER);
      FrameLayout icon = new FrameLayout(this);
      icon.addView(u.icon(ORDER_ICONS[i], 24),
          new FrameLayout.LayoutParams(u.d(24), u.d(24), Gravity.CENTER));
      TextView badge = u.text("0", 8, Color.WHITE, true);
      badge.setGravity(Gravity.CENTER);
      badge.setBackground(u.bg(Ui.BLUE, 8));
      badge.setVisibility(View.GONE);
      icon.addView(badge, new FrameLayout.LayoutParams(u.d(15), u.d(15), Gravity.TOP | Gravity.END));
      badges.add(badge);
      item.addView(icon, new LinearLayout.LayoutParams(u.d(38), u.d(26)));
      u.space(item, 9);
      TextView name = u.text(ORDER_LABELS[i], 11, Ui.MUTED, false);
      name.setGravity(Gravity.CENTER);
      name.setMaxLines(2);
      item.addView(name);
      item.setContentDescription(ORDER_LABELS[i]);
      String status = ORDER_STATUSES[i];
      u.action(item, () -> { s.orderFilter = status; go("orders"); });
      statuses.addView(item, new LinearLayout.LayoutParams(0, u.d(88), 1));
    }
    u.add(content, statuses);
    u.space(content, 20);
    if (!BuildConfig.NOVA_MIDTERM_API) get("/orders", data -> {
      JSONArray orders = arr(data);
      for (int i = 0; i < badges.size(); i++) {
        int count = 0;
        for (int j = 0; j < orders.length(); j++) {
          JSONObject order = orders.optJSONObject(j);
          if (order != null && ORDER_STATUSES[i].equals(order.optString("status"))) count++;
        }
        badges.get(i).setText(String.valueOf(count));
        badges.get(i).setVisibility(count > 0 ? View.VISIBLE : View.GONE);
      }
    });
  }

  // Dựng các mục cài đặt và chức năng tài khoản.
  private void showSettings() {
    if ("ADMIN".equals(s.user.optString("role"))) {
      LinearLayout admin = u.card();
      addSetting(admin, "node_6_2783_imgpackage", "Quản lý ảnh sản phẩm", "Chọn sản phẩm và tải nhiều ảnh từ thiết bị",
          () -> go("admin-images"), false);
      u.add(content, admin);
      u.space(content, 14);
    }
    LinearLayout settings = u.col();
    settings.setTag("account-settings");
    settings.setBackground(u.bg(Color.WHITE, 20));
    settings.setClipToOutline(true);
    addSetting(settings, "node_6_2783_imgmappin", "Sổ địa chỉ", "", () -> go("addresses"), true);
    addSetting(settings, "node_6_2783_imgwallet", "Lịch sử thanh toán", "Giao dịch và khoản hoàn", () -> go("payments"), true);
    addSetting(settings, "node_6_2783_imgticketpercent", "Ưu đãi của tôi", "", () -> go("vouchers"), true);
    addSetting(settings, "node_6_2783_imgshieldcheck", "Đổi mật khẩu", "", () -> go("password"), true);
    addSetting(settings, "home_imgheart1", "Sản phẩm yêu thích", "", () -> go("favorites"), true);
    addSetting(settings, "node_6_2783_imgpackage", "Đổi trả & hoàn tiền", "", () -> go("returns"), false);
    u.add(content, settings);
    u.space(content, 14);
  }

  // Tạo một hàng cài đặt có icon, nhãn và callback khi chạm.
  private void addSetting(LinearLayout parent, String icon, String title, String detail,
      Runnable action, boolean divider) {
    LinearLayout row = u.row();
    row.setPadding(u.d(16), 0, u.d(16), 0);
    row.addView(u.icon(icon, 20));
    LinearLayout labels = u.col();
    labels.setPadding(u.d(12), 0, u.d(8), 0);
    u.add(labels, u.text(title, 13));
    if (!detail.isEmpty()) {
      u.space(labels, 3);
      u.add(labels, u.text(detail, 10, Ui.MUTED, false));
    }
    u.weight(row, labels);
    row.addView(u.icon("node_6_2783_imgchevronright", 16));
    row.setMinimumHeight(u.d(58));
    row.setContentDescription(title);
    u.action(row, action);
    u.add(parent, row);
    if (divider) {
      LinearLayout.LayoutParams line = new LinearLayout.LayoutParams(-1, u.d(1));
      line.setMargins(u.d(16), 0, u.d(16), 0);
      parent.addView(u.divider(), line);
    }
  }

  // Dựng phần thông tin/thao tác cuối màn Tài khoản.
  private void showFooter() {
    LinearLayout footer = u.row();
    u.weight(footer, u.link("Trung tâm hỗ trợ", () -> go("support")));
    footer.addView(u.link("Đăng xuất", () -> confirm("Đăng xuất NOVA?",
        "Bạn có thể đăng nhập lại bất cứ lúc nào.", () -> send("POST", "/auth/logout", null, data -> {
          session.clear();
          s.user = new JSONObject();
          s.cartCount = 0;
          tab("home");
        }))));
    u.add(content, footer);
  }
}

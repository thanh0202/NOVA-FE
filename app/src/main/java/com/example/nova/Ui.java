package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Bộ hàm dựng giao diện native dùng chung: font, màu, dp, hàng/cột, nút, thẻ, chip và định dạng tiền Việt.
 */
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.text.NumberFormat;
import java.util.Locale;

/** Shared spacing, colors and native controls taken from NOVA Figma. */
public final class Ui {
  public static final int BG = Color.rgb(247, 249, 253),
      INK = Color.rgb(16, 29, 53),
      MUTED = Color.rgb(114, 128, 151),
      BLUE = Color.rgb(0, 103, 236),
      BRAND = Color.rgb(7, 74, 165),
      TINT = Color.rgb(237, 245, 255),
      BORDER = Color.rgb(219, 228, 241);
  public final Context context;
  private Typeface regular = Typeface.create("sans-serif", Typeface.NORMAL),
      bold = Typeface.create("sans-serif-medium", Typeface.NORMAL),
      heading = Typeface.create("sans-serif", Typeface.BOLD);

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public Ui(Context context) {
    this.context = context;
    try {
      regular =
          new Typeface.Builder(context.getAssets(), "fonts/InterVariable.ttf")
              .setFontVariationSettings("'wght' 400")
              .build();
      bold =
          new Typeface.Builder(context.getAssets(), "fonts/InterVariable.ttf")
              .setFontVariationSettings("'wght' 600")
              .build();
      heading = new Typeface.Builder(context.getAssets(), "fonts/InterVariable.ttf")
          .setFontVariationSettings("'wght' 700").build();
    } catch (RuntimeException ignored) {
    }
  }

  /** Use the bundled Inter font for XML and programmatic views alike. */
  // Duyệt cây View và áp dụng font Inter phù hợp cho chữ thường/chữ đậm.
  public void applyTypography(View root) {
    if (root instanceof TextView text) {
      boolean strong = text.getTypeface() != null && text.getTypeface().isBold();
      text.setTypeface(strong ? bold : regular);
      text.setPaintFlags(text.getPaintFlags() | android.graphics.Paint.ANTI_ALIAS_FLAG
          | android.graphics.Paint.SUBPIXEL_TEXT_FLAG);
    }
    if (root instanceof ViewGroup group) {
      for (int i = 0; i < group.getChildCount(); i++) applyTypography(group.getChildAt(i));
    }
  }

  // Đổi dp sang pixel theo mật độ màn hình thiết bị.
  public int d(float value) {
    return Math.round(value * context.getResources().getDisplayMetrics().density);
  }

  // Tạo LinearLayout theo chiều dọc.
  public LinearLayout col() {
    LinearLayout l = new LinearLayout(context);
    l.setOrientation(LinearLayout.VERTICAL);
    return l;
  }

  // Tạo LinearLayout theo chiều ngang.
  public LinearLayout row() {
    LinearLayout l = new LinearLayout(context);
    l.setOrientation(LinearLayout.HORIZONTAL);
    l.setGravity(Gravity.CENTER_VERTICAL);
    return l;
  }

  // Đặt cùng một khoảng đệm cho bốn cạnh View theo dp.
  public void pad(View v, int value) {
    v.setPadding(d(value), d(value), d(value), d(value));
  }

  // Tạo TextView với nội dung, cỡ chữ, màu và font của thiết kế NOVA.
  public TextView text(String value, int size, int color, boolean strong) {
    TextView view = new TextView(context);
    view.setText(value);
    view.setTextSize(size);
    view.setTextColor(color);
    view.setTypeface(strong ? bold : regular);
    view.setLineSpacing(d(2), 1);
    view.setIncludeFontPadding(false);
    view.setPaintFlags(view.getPaintFlags() | android.graphics.Paint.ANTI_ALIAS_FLAG
        | android.graphics.Paint.SUBPIXEL_TEXT_FLAG);
    return view;
  }

  // Tạo TextView với nội dung, cỡ chữ, màu và font của thiết kế NOVA.
  public TextView text(String value, int size) {
    return text(value, size, INK, false);
  }

  // Thêm View vào bố cục với kích thước phù hợp, giữ đúng chiều cao khi đã quy định.
  public void add(LinearLayout parent, View child) {
    parent.addView(child, new LinearLayout.LayoutParams(-1, -2));
  }

  /** Fixed-height components must not be remeasured as wrap_content. */
  // Thêm View vào bố cục với kích thước phù hợp, giữ đúng chiều cao khi đã quy định.
  public void add(LinearLayout parent, View child, int heightDp) {
    parent.addView(child, new LinearLayout.LayoutParams(-1, d(heightDp)));
  }

  // Áp dụng font tiêu đề cho TextView.
  public void heading(TextView view) {
    view.setTypeface(heading);
  }

  // Gắn sự kiện chạm, trạng thái focus và hiệu ứng ripple cho toàn bộ View.
  public void action(View view, Runnable action) {
    view.setOnClickListener(ignored -> action.run());
    view.setFocusable(true);
    view.setForeground(new RippleDrawable(ColorStateList.valueOf(0x120067EC), null, null));
  }

  // Thêm một View đệm để tạo khoảng cách trong bố cục.
  public void space(LinearLayout parent, int size) {
    View v = new View(context);
    parent.addView(v, new LinearLayout.LayoutParams(1, d(size)));
  }

  // Thêm phần tử với layout_weight để chia phần chiều rộng còn lại.
  public void weight(LinearLayout parent, View child) {
    parent.addView(
        child, new LinearLayout.LayoutParams(0, child.getClass() == View.class ? 0 : -2, 1));
  }

  // Tạo nền màu với góc bo tròn theo dp.
  public GradientDrawable bg(int color, int radius) {
    GradientDrawable gd = new GradientDrawable();
    gd.setColor(color);
    gd.setCornerRadius(d(radius));
    return gd;
  }

  // Tạo nền có viền và góc bo theo thiết kế dùng chung.
  public GradientDrawable outline(int color, int radius, int stroke) {
    GradientDrawable gd = bg(color, radius);
    gd.setStroke(d(1), stroke);
    return gd;
  }

  // Tạo thẻ nền xanh nhạt cho các vùng nội dung được nhấn mạnh.
  public void blueCard(View v, int radius) {
    GradientDrawable g =
        new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[] {Color.rgb(3, 28, 70), BRAND, Color.rgb(22, 138, 241)});
    if (android.os.Build.VERSION.SDK_INT >= 29) {
      g.setColors(new int[] {0xFF031C46, BRAND, 0xFF168AF1}, new float[] {0f, .6f, 1f});
    }
    g.setCornerRadius(d(radius));
    v.setBackground(g);
  }

  // Tạo khung thẻ dùng chung để chứa một nhóm thông tin.
  public LinearLayout card() {
    LinearLayout l = col();
    pad(l, 18);
    l.setBackground(outline(Color.WHITE, 20, 0xFFE8EEF7));
    l.setElevation(d(.5f));
    return l;
  }

  // Tạo nút theo màu và kiểu chữ dùng chung của NOVA.
  public TextView button(String title, boolean primary, Runnable action) {
    TextView b = text(title, 14, primary ? Color.WHITE : BRAND, true);
    b.setGravity(Gravity.CENTER);
    b.setMinHeight(d(50));
    pad(b, 12);
    b.setBackground(context.getDrawable(primary ? R.drawable.nova_primary_button : R.drawable.nova_secondary_button));
    b.setOnClickListener(v -> action.run());
    b.setClickable(true);
    b.setFocusable(true);
    return b;
  }

  // Tạo chữ có thể chạm để thực hiện thao tác điều hướng.
  public TextView link(String label, Runnable action) {
    TextView t = text(label, 12, BRAND, true);
    t.setPadding(0, d(12), 0, d(12));
    t.setOnClickListener(v -> action.run());
    return t;
  }

  // Tạo EditText với nhãn gợi ý, inputType và kiểu nền/font dùng chung.
  public EditText field(LinearLayout parent, String label, String value, int inputType) {
    add(parent, text(label, 12, INK, true));
    space(parent, 8);
    EditText f = new EditText(context);
    f.setSingleLine(true);
    f.setTextSize(14);
    f.setTextColor(INK);
    f.setHintTextColor(MUTED);
    f.setTypeface(regular);
    f.setInputType(inputType);
    f.setText(value);
    f.setBackground(context.getDrawable(R.drawable.nova_field));
    f.setPadding(d(16), d(15), d(16), d(15));
    f.setMinHeight(d(54));
    add(parent, f);
    space(parent, 16);
    return f;
  }

  // Dựng một dòng nhãn và giá trị tương ứng.
  public void labelPair(LinearLayout parent, String key, String value, boolean total) {
    LinearLayout row = row();
    weight(row, text(key, total ? 14 : 12, total ? INK : MUTED, total));
    row.addView(text(value, total ? 18 : 12, total ? BRAND : INK, total));
    add(parent, row);
    space(parent, 12);
  }

  // Định dạng số tiền theo Locale Việt Nam.
  public static String money(long value) {
    return NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(value) + "₫";
  }

  // Tìm và nạp drawable theo tên tài nguyên vào ImageView.
  public ImageView icon(String resource, int size) {
    ImageView view = new ImageView(context);
    int id = context.getResources().getIdentifier(resource, "drawable", context.getPackageName());
    if (id != 0) view.setImageResource(id);
    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
    view.setLayoutParams(new LinearLayout.LayoutParams(d(size), d(size)));
    return view;
  }

  // Tạo lựa chọn dạng chip với trạng thái chọn và callback khi chạm.
  public TextView chip(String title, boolean selected, Runnable click) {
    TextView t = button(title, selected, click);
    t.setMinHeight(d(40));
    t.setTextSize(11);
    return t;
  }

  // Tạo đường phân cách dùng chung giữa các vùng nội dung.
  public View divider() {
    View view = new View(context);
    view.setBackgroundColor(BORDER);
    view.setLayoutParams(new LinearLayout.LayoutParams(-1, d(1)));
    return view;
  }
}

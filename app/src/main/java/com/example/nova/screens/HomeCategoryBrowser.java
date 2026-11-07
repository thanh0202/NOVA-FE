package com.example.nova.screens;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Khối Sản phẩm theo danh mục dưới phần khám phá: hiển thị mọi danh mục gốc, lọc nhóm con/thương hiệu, mở trang danh sách qua Xem thêm.
 * Bố cục liên quan: component_home_category_products.xml, item_home_category.xml.
 * Cách đọc: render() dựng màn hình; các hàm phụ gắn dữ liệu/sự kiện; go()/tab() điều hướng qua MainActivity.
 */
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.nova.ApiClient;
import com.example.nova.CategoryUi;
import com.example.nova.MainActivity;
import com.example.nova.R;
import com.example.nova.Ui;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/** Inline category/brand filtering below the highlights, sharing the catalog APIs. */
public final class HomeCategoryBrowser extends BaseScreen {
  private final LinearLayout host;
  private final List<JSONObject> categories = new ArrayList<>(), roots = new ArrayList<>();
  private JSONArray brands = new JSONArray();
  private LinearLayout layout, rootTabs, childTabs, brandTabs, grid;
  private TextView status, more;
  private int generation, selectionVersion, productRequest;
  private boolean disposed;

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public HomeCategoryBrowser(MainActivity activity, LinearLayout host) { super(activity); this.host=host; }
  // Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu.
  @Override public int layoutId() { return R.layout.component_home_category_products; }
  // Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback.
  @Override public void render() {
    generation=s.generation;
    host.removeAllViews();
    layout=inflateLayout(layoutId(),host);
    host.addView(layout,new LinearLayout.LayoutParams(-1,-2));
    rootTabs=layout.findViewById(R.id.home_product_roots);
    childTabs=layout.findViewById(R.id.home_product_children);
    brandTabs=layout.findViewById(R.id.home_product_brands);
    grid=layout.findViewById(R.id.home_category_product_grid);
    status=layout.findViewById(R.id.home_category_product_status);
    more=layout.findViewById(R.id.home_category_product_more);
    more.setOnClickListener(v -> openCatalog());
    layout.findViewById(R.id.home_category_product_all).setOnClickListener(v -> openCatalog());
  }
  // Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn.
  @Override public void onDispose() { disposed=true; productRequest++; selectionVersion++; }
  // Kiểm tra helper chưa bị dispose, generation vẫn khớp và Activity chưa kết thúc/hủy.
  private boolean active() { return !disposed && generation==s.generation && !isFinishing() && !isDestroyed(); }
  // Lấy slug điều hướng danh mục, dùng code khi dữ liệu chưa có slug.
  private String slug(JSONObject category) { return category.optString("slug",category.optString("code")); }
  // Lấy danh mục con đang chọn; nếu chưa chọn con thì dùng danh mục gốc.
  private String effectiveCategory() { return s.homeSubcategory.isEmpty()?s.homeCategory:s.homeSubcategory; }
  // Ghi bộ lọc danh mục/thương hiệu vào AppState rồi mở route products.
  private void openCatalog() {
    if (!active() || root()==null) return;
    s.category=effectiveCategory(); s.brand=s.homeBrand; s.query="";
    s.minPrice=""; s.maxPrice=""; s.inStock=false; s.sort="bestselling";
    go("products");
  }

  // Hiện trạng thái lỗi danh mục và gắn thao tác thử lại.
  public void showCategoriesError(Runnable retry) {
    if (!active()) return;
    status.setVisibility(View.VISIBLE); status.setText("Chưa tải được danh mục. Chạm để thử lại.");
    status.setTextColor(Ui.BRAND); status.setOnClickListener(v -> retry.run());
  }
  // Nhận danh mục từ API, phân nhóm gốc/con và khôi phục lựa chọn còn hợp lệ.
  public void setCategories(List<JSONObject> data) {
    if (!active()) return;
    categories.clear(); categories.addAll(data); roots.clear();
    for (JSONObject category:categories) if (category.isNull("parentId")) roots.add(category);
    if (roots.isEmpty()) roots.addAll(categories);
    List<String> preferred=List.of("phone","tablet","laptop","audio","watch","accessory");
    roots.sort(Comparator.comparingInt(category -> {
      int index=preferred.indexOf(category.optString("code")); return index<0?preferred.size():index;
    }));
    if (roots.isEmpty()) { status.setText("Chưa có danh mục sản phẩm."); return; }
    if (root()==null) { s.homeCategory=slug(roots.get(0)); s.homeSubcategory=""; s.homeBrand=""; }
    JSONObject parent=root();
    boolean validChild=s.homeSubcategory.isEmpty();
    for (JSONObject item:categories) if (item.optLong("parentId",-1)==parent.optLong("id")
        && slug(item).equals(s.homeSubcategory)) validChild=true;
    if (!validChild) { s.homeSubcategory=""; s.homeBrand=""; }
    updateSelection();
  }
  // Tìm đối tượng danh mục gốc khớp lựa chọn hiện tại.
  private JSONObject root() {
    for (JSONObject item:roots) if (slug(item).equals(s.homeCategory)) return item;
    return null;
  }
  // Tăng phiên bản lựa chọn, xóa kết quả cũ và tải lại dữ liệu cho bộ lọc mới.
  private void updateSelection() {
    // Tăng số phiên lựa chọn để yêu cầu đang tải của danh mục cũ không ghi đè lựa chọn mới.
    selectionVersion++;
    grid.removeAllViews(); more.setVisibility(View.GONE);
    renderRoots(); renderChildren(); loadBrands(); loadProducts();
  }
  // Dựng mọi danh mục gốc thành các hàng ba ô, đánh dấu mục đang chọn.
  private void renderRoots() {
    rootTabs.removeAllViews();
    LinearLayout row=null;
    for (int index=0;index<roots.size();index++) {
      // Mỗi ba danh mục mở một hàng mới; không giới hạn dữ liệu ở hai tab đầu.
      if (index%3==0) {
        row=u.row(); row.setBaselineAligned(false);
        rootTabs.addView(row,new LinearLayout.LayoutParams(-1,u.d(52)));
      }
      JSONObject category=roots.get(index);
      String value=slug(category); boolean selected=value.equals(s.homeCategory);
      LinearLayout tab=u.col(); tab.setGravity(Gravity.CENTER);
      tab.setBackgroundColor(selected?Ui.TINT:0xFFFFFFFF);
      tab.setTag("home-root-"+value); tab.setSelected(selected);
      TextView title=u.text(category.optString("name"),13,selected?Ui.BLUE:Ui.INK,true);
      title.setGravity(Gravity.CENTER); title.setPadding(u.d(6),u.d(4),u.d(6),u.d(4));
      title.setMaxLines(2); tab.addView(title,new LinearLayout.LayoutParams(-1,0,1));
      View underline=new View(this); underline.setBackgroundColor(selected?Ui.BLUE:Ui.BORDER);
      tab.addView(underline,new LinearLayout.LayoutParams(-1,u.d(2)));
      u.action(tab,() -> { s.homeCategory=value; s.homeSubcategory=""; s.homeBrand=""; updateSelection(); });
      tab.setContentDescription("Danh mục "+category.optString("name")+(selected?", đang chọn":""));
      row.addView(tab,new LinearLayout.LayoutParams(0,-1,1));
    }
    if (row!=null) for (int missing=(3-roots.size()%3)%3;missing>0;missing--)
      row.addView(new View(this),new LinearLayout.LayoutParams(0,-1,1));
  }
  // Dựng lựa chọn Tất cả và các danh mục con của nhóm gốc hiện tại.
  private void renderChildren() {
    childTabs.removeAllViews();
    JSONObject parent=root();
    if (parent==null) return;
    List<JSONObject> children=new ArrayList<>();
    for (JSONObject category:categories) if (category.optLong("parentId",-1)==parent.optLong("id")) children.add(category);
    layout.findViewById(R.id.home_product_children_scroll).setVisibility(children.isEmpty()?View.GONE:View.VISIBLE);
    if (children.isEmpty()) return;
    addChild(ApiClient.json("name","Tất cả","imageUrl",parent.optString("imageUrl")),"");
    for (JSONObject category:children) addChild(category,slug(category));
  }
  // Gắn ảnh/tên/sự kiện cho một ô nhóm con, cập nhật bộ lọc khi chạm.
  private void addChild(JSONObject category,String value) {
    boolean selected=value.equals(s.homeSubcategory);
    LinearLayout tile=inflateLayout(R.layout.item_home_category,childTabs);
    CategoryUi.bind(tile,category,images,selected);
    tile.setTag("home-child-"+value); tile.setBackground(u.bg(selected?Ui.TINT:0xFFFFFFFF,12));
    tile.setOnClickListener(v -> { s.homeSubcategory=value; s.homeBrand=""; updateSelection(); });
    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(u.d(104),-2);
    if (childTabs.getChildCount()>0) params.leftMargin=u.d(8);
    childTabs.addView(tile,params);
  }
  // Lấy các thương hiệu của danh mục hiện tại và bỏ qua phản hồi của lựa chọn cũ.
  private void loadBrands() {
    int version=selectionVersion;
    brands=new JSONArray(); brandTabs.removeAllViews();
    layout.findViewById(R.id.home_product_brands_scroll).setVisibility(View.GONE);
    api.get("/brands?category="+ApiClient.encode(effectiveCategory()),(data,error,code) -> {
      if (!active() || version!=selectionVersion) return;
      if (error!=null) {
        TextView retry=u.link("Tải lại thương hiệu",this::loadBrands);
        brandTabs.addView(retry);
        layout.findViewById(R.id.home_product_brands_scroll).setVisibility(View.VISIBLE);
        return;
      }
      List<JSONObject> sorted=new ArrayList<>();
      JSONArray source=arr(data);
      for (int i=0;i<source.length();i++) if (source.optJSONObject(i)!=null) sorted.add(source.optJSONObject(i));
      sorted.sort(Comparator.<JSONObject>comparingLong(brand -> brand.optLong("productCount")).reversed()
          .thenComparing(brand -> brand.optString("name")));
      brands=new JSONArray();
      for (JSONObject brand:sorted) brands.put(brand);
      renderBrands();
    });
  }
  // Dựng lại các chip thương hiệu theo dữ liệu và trạng thái chọn.
  private void renderBrands() {
    brandTabs.removeAllViews();
    layout.findViewById(R.id.home_product_brands_scroll).setVisibility(brands.length()==0?View.GONE:View.VISIBLE);
    if (brands.length()==0) return;
    addBrand("","Tất cả");
    for (int i=0;i<brands.length();i++) {
      JSONObject brand=brands.optJSONObject(i);
      if (brand!=null) addBrand(brand.optString("code"),brand.optString("name"));
    }
  }
  // Tạo chip thương hiệu; khi chọn sẽ bỏ kết quả cũ và tải sản phẩm tương ứng.
  private void addBrand(String value,String label) {
    boolean selected=value.equals(s.homeBrand);
    TextView chip=u.chip(label,selected,() -> {
      if (value.equals(s.homeBrand)) return;
      s.homeBrand=value; selectionVersion++;
      grid.removeAllViews(); more.setVisibility(View.GONE);
      renderBrands(); loadProducts();
    });
    chip.setTag("home-brand-"+value); chip.setSelected(selected); chip.setMinHeight(u.d(40));
    chip.setContentDescription("Thương hiệu "+label+(selected?", đang chọn":""));
    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-2,u.d(40));
    if (brandTabs.getChildCount()>0) params.leftMargin=u.d(8);
    brandTabs.addView(chip,params);
  }
  // Lấy trang sản phẩm đầu tiên theo danh mục/thương hiệu và kiểm tra phiên bản yêu cầu trước khi vẽ.
  private void loadProducts() {
    // Chụp số phiên lọc/yêu cầu tại lúc gửi để kiểm tra lại khi nhận phản hồi.
    int version=selectionVersion, request=++productRequest;
    more.setVisibility(View.GONE);
    status.setVisibility(View.VISIBLE); status.setText("Đang tải sản phẩm…");
    status.setTextColor(Ui.MUTED); status.setOnClickListener(null);
    String path="/products?category="+ApiClient.encode(effectiveCategory())+"&brand="+ApiClient.encode(s.homeBrand)
        +"&sort=featured&size=8&page=0";
    api.get(path,(data,error,code) -> {
      if (!active() || version!=selectionVersion || request!=productRequest) return;
      if (error!=null) {
        status.setText("Chưa tải được sản phẩm. Chạm để thử lại."); status.setTextColor(Ui.BRAND);
        status.setOnClickListener(v -> loadProducts()); more.setVisibility(View.GONE); return;
      }
      JSONArray products=arr(data);
      status.setVisibility(View.GONE);
      productGrid(grid,products,products.length(),true);
      more.setVisibility(products.length()>0?View.VISIBLE:View.GONE);
    });
  }
}

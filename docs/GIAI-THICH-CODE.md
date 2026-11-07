# Giải thích code FE

Gói này chỉ chứa frontend Android. Các mô tả BE bên dưới giải thích hợp đồng HTTP mà FE sử dụng; mã BE ở gói riêng.

## 1. Trình tự nên đọc

1. `app/build.gradle.kts`: URL API, SDK và chế độ GK.
2. `AppState.java`: trạng thái màn hình, bộ lọc và dữ liệu tạm.
3. `MainActivity.java`: vòng đời Activity, `switch(route)`, lịch sử Back và thanh điều hướng.
4. `BaseScreen.java`: các hàm dùng chung, inflate XML và lưới sản phẩm.
5. Một Screen cụ thể cùng XML của nó, ví dụ `HomeScreen`/`screen_home.xml`.
6. `ApiClient.java`, `SecureSession.java`, `Images.java`: HTTP, phiên và ảnh.
7. Backend: controller nhận request, service xử lý, repository thực hiện SQL/JDBC.

## 2. Java, XML và tài nguyên phối hợp thế nào?

XML khai báo vị trí và thuộc tính View; Java nạp XML bằng `inflate`, tìm View bằng `findViewById`, gắn dữ liệu và `setOnClickListener`.
`R.layout`, `R.id`, `R.drawable` là ID được Android tạo khi build từ thư mục `res`.
`Ui.java` tạo các View động cùng màu/font/khoảng cách với XML. Nhiều ô sản phẩm/danh mục chỉ được tạo sau khi API trả dữ liệu.

`app/src/debug` chứa bố cục Preview và thiết lập debug. Dữ liệu `tools:text`, `tools:src`, `tools:showIn` phục vụ Android Studio.
Các `ViewStub` mang tag `nova_preview` được `BaseScreen.inflateLayout` loại khỏi bố cục chạy thật trước khi gắn dữ liệu API.
Điều này giải thích vì sao Preview và danh sách thực tế không nhất thiết có cùng số sản phẩm.

## 3. Điều hướng và vòng đời

`MainActivity` dùng một Activity và nhiều lớp Screen. `s.route` chọn controller qua `switch`; `s.entityId` giữ ID sản phẩm/đơn/giao dịch đang xem.
`go(route, id)` đẩy đích hiện tại vào `history`, rồi dựng đích mới. `tab(route)` xóa nhánh lịch sử cũ.
Nút Back lấy phần tử trên cùng của `Deque<Destination>`; các bộ lọc riêng của Trang chủ vẫn ở `AppState`.

Mỗi lần `render`, `generation` tăng. Các callback dùng giá trị đã chụp lúc gửi để bỏ qua dữ liệu của màn trước.
Các màn có hẹn giờ dừng/tiếp tục công việc trong `onPause`, `onResume`, `onDispose` để tránh cập nhật View cũ.
`onSaveInstanceState` giữ route/bộ lọc cần phục hồi; mã khôi phục mật khẩu được giữ riêng trong bộ nhớ, không đưa vào Bundle.

## 4. Gọi HTTP và nhận JSON

Một thao tác FE đi theo đường: View → Screen → ApiClient → endpoint BE → service → repository → Neon → JSON → callback → View.
`ApiClient` chạy HTTP và giải mã JSON trong `ExecutorService`, không chặn main thread.
Sau khi có kết quả, `Handler(Looper.getMainLooper())` chuyển callback về luồng được phép sửa View.
Nếu có token, request thêm `Authorization: Bearer ...`; khi token hết hạn, app xử lý mã 401 và xóa phiên phù hợp.

Các endpoint danh sách có thể trả mảng trực tiếp hoặc đối tượng phân trang.
`BaseScreen.arr` đọc `JSONArray`, hoặc lấy `items` từ `PageResponse`, tránh hiểu nhầm đối tượng thành mảng rỗng.
`ApiClient.encode` mã hóa query UTF-8 để từ khóa tiếng Việt và dấu cách không làm sai URL.

## 5. Đăng ký, OTP và đăng nhập

Đăng ký: `RegisterScreen` kiểm tra biểu mẫu → `POST /auth/register` → `RegisterRequest` được validation → `AuthService.register`.
BE chuẩn hóa email/điện thoại, băm mật khẩu, tạo tài khoản chưa xác minh và challenge OTP trong luồng giao dịch.
`VerificationService` quản lý mã/hạn dùng/số lần thử; `EmailService` gửi email bằng SMTP.
Xác minh email thành công cập nhật tài khoản và tạo phiên. Đăng nhập chỉ cấp phiên cho tài khoản hoạt động, email đủ điều kiện và mật khẩu đúng.

`PasswordHasher` dùng PBKDF2-HMAC-SHA256 với salt ngẫu nhiên; hai tài khoản dùng cùng mật khẩu vẫn có hash khác nhau.
`matches` đọc tham số trong hash đã lưu để xác minh; không áp quy tắc tạo mật khẩu mới lên mật khẩu cũ đang đăng nhập.
`TokenFactory` sinh token ngẫu nhiên và lưu digest qua `SessionRepository`; OTP được gắn với challenge bằng HMAC.
Khi mã OTP sai, bộ đếm được commit trước khi lỗi được ném ra ngoài để tránh rollback mất số lần nhập sai.

Ở Android, `SecureSession` mã hóa token bằng AES/GCM trước khi ghi preferences, dùng khóa trong Android Keystore.
Nếu không chọn ghi nhớ, token chỉ được giữ tạm trong bộ nhớ tiến trình. App không lưu mật khẩu tài khoản.

## 6. Trang chủ và Xem thêm theo danh mục

`HomeScreen` tải Home/banner/danh mục rồi dựng các khối. Dải Khám phá danh mục ở trên là hai hàng có cuộn ngang.
`HomeCategoryBrowser` là khối riêng nằm dưới phần khám phá Bán chạy/Nổi bật.

`setCategories` tìm danh mục gốc, khôi phục nhóm con còn hợp lệ. `renderRoots` thêm mọi danh mục gốc, cứ ba mục mở một hàng mới.
Chọn nhóm con/thương hiệu cập nhật `homeSubcategory`/`homeBrand`; `selectionVersion` và `productRequest` ngăn phản hồi cũ ghi đè lựa chọn mới.
`loadProducts` gọi `/products?category=...&brand=...&sort=featured&size=8&page=0` rồi dựng lưới hai cột.
`openCatalog` chuyển danh mục hiệu lực và thương hiệu sang bộ lọc Catalog, xóa từ khóa/khoảng giá cũ rồi mở route `products`.
Nút Xem thêm mở trang danh sách; trang này không phải thao tác nối thêm trang sản phẩm ngay trong khối Home.

## 7. Tìm kiếm và SQL bộ lọc

`SearchScreen` theo dõi văn bản, hủy callback tìm kiếm cũ và chờ một khoảng trước khi gọi gợi ý để hạn chế gửi request theo từng ký tự.
Chỉ dữ liệu của từ khóa/phiên bản hiện tại được nhận. Chạm gợi ý sản phẩm mở chi tiết; xác nhận từ khóa mở `SearchResultsScreen`.
`SearchResultsScreen` dùng xử lý của `CatalogScreen`; bộ lọc được chuyển thành query HTTP.

BE dùng `CatalogFilter.from` kiểm tra page/size, khoảng giá, số nguyên, boolean, rating và alias.
`ProductSearchSql` dựng các CTE `stock`, `matching`, `prices`, `ratings`, `cards`.
Các tiêu chí giá/RAM/bộ nhớ/tồn kho đều lọc trên `matching`, rồi mới tính giá và ghép sản phẩm.
Vì vậy một SKU rẻ không thể thỏa giá tối thiểu còn SKU khác thỏa giá tối đa để làm sản phẩm lọt sai khoảng giá.
Danh mục được mở rộng qua view `v_category_descendants`; giá trị client đi bằng tham số `?`, còn `ORDER BY` lấy từ lựa chọn server cho phép.

## 8. Đổi avatar và thêm ảnh sản phẩm

`ProfileScreen` hoặc `AdminImagesScreen` gọi `ImagePicker`; bộ chọn hệ thống chỉ cấp quyền với các URI đã chọn.
`UploadImage.jpeg` đọc ảnh vào tệp cache với giới hạn, kiểm tra kích thước trước khi giải mã đầy đủ, xoay EXIF, thu nhỏ, nén JPEG và dọn bitmap/tệp tạm.
`ApiClient.uploadImages` truyền multipart trên luồng nền, có Bearer token. Trường `file` dành cho avatar, `files` cho nhiều ảnh sản phẩm.

`MediaUploadController` nhận multipart. `ImageUploadValidator` kiểm tra byte thực, định dạng, số pixel/dung lượng và tính SHA-256.
`MediaUploadService` xác thực người gọi, kiểm tra số chỗ, khóa hàng tài khoản/sản phẩm và chống trùng theo hash.
`CloudinaryImageStore` gọi HTTPS từ server bằng credential đọc từ cấu hình BE; Android không nhận API Secret.
Cloudinary giữ byte ảnh, Neon lưu URL trong `users.avatar_url` hoặc `product_images.url`.
Giới hạn số ảnh được lấy theo nhóm sản phẩm; quá số chỗ trả lỗi trước khi thêm URL vào CSDL.

## 9. Kết nối hiện có và phạm vi GK

URL mặc định của FE vẫn là `https://nova-uit.japaneast.cloudapp.azure.com/api`.
Để chạy FE với cấu hình đó không cần chạy BE trên máy. `novaApiUrl` và `novaMidtermApi` nằm trong Gradle module app.
FE có mã cho các luồng mở rộng như giỏ hàng, đơn, thanh toán, đổi trả; sự có mặt của Screen không bảo đảm backend GK có đủ endpoint cho luồng ấy.
Đọc `BuildConfig.NOVA_MIDTERM_API`, các nhánh điều kiện trong Screen và `MainActivity.isFullEditionRoute` khi phân biệt chế độ.

`application.properties` đọc `DB_JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD` và các biến SMTP/OTP/Cloudinary từ môi trường.
Mẫu `.env.example` giữ địa chỉ Neon với mật khẩu giả định. Trong gói BE tham khảo đã bỏ launcher Python, Spring Boot không tự đọc file `.env`
hoặc tự đổi `NEON_DATABASE_URL` thành JDBC; nếu tự triển khai sau này phải cấp các biến JDBC đúng cho tiến trình Java.
Các cấu hình thật và dịch vụ đang chạy được giữ ngoài gói này.

File SQL `017_home_featured.sql` chỉ đánh dấu một nhóm sản phẩm có sẵn là nổi bật sau bước kiểm tra điều kiện.
Nó không tạo schema `shop` và không phải toàn bộ CSDL của đồ án; `spring.sql.init.mode=never` cũng không tự chạy file SQL đó.

## 10. Ý nghĩa các loại comment

- Đầu file Java: mục đích file, bố cục liên quan và tầng chịu trách nhiệm.
- Trước phương thức: dữ liệu/thao tác chính và kết quả cần hiểu khi đọc hàm.
- Bên trong đoạn khó: luồng nền/main thread, generation, bộ lọc cũ, salt/hash, transaction, byte ảnh/URL ảnh.
- XML: mục đích tài nguyên; vùng layout quan trọng có thêm ghi chú về Java/ID sử dụng.
- Gradle/properties/TOML/SQL: vai trò cấu hình và ảnh hưởng tới cách build/chạy.

JSON, font, bitmap và JAR được giữ đúng định dạng; cách dùng được ghi trong bảng chức năng từng file.
Các chú thích mới giải thích code hiện có, không thay đổi thuật toán, endpoint, dữ liệu hay giao diện.

# Tra cứu phương thức FE

Các liên kết dưới đây chỉ trỏ tới mã nguồn bên trong gói NOVA_FE_GITHUB.

## ApiClient

[Mở file](../app/src/main/java/com/example/nova/ApiClient.java) — Gửi HTTP JSON hoặc multipart trên luồng nền, đính kèm Bearer token và trả kết quả về luồng giao diện Android.

| Phương thức | Chức năng |
| --- | --- |
| [complete()](../app/src/main/java/com/example/nova/ApiClient.java#L25) | Callback nhận dữ liệu, thông báo lỗi và HTTP status sau khi yêu cầu API hoàn tất. |
| [ApiClient()](../app/src/main/java/com/example/nova/ApiClient.java#L36) | Nhận SecureSession để lấy API base URL và token cho các yêu cầu HTTP. |
| [request()](../app/src/main/java/com/example/nova/ApiClient.java#L41) | Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback. |
| [revokeToken()](../app/src/main/java/com/example/nova/ApiClient.java#L47) | Gọi đăng xuất bằng token được cung cấp mà không ghi token đó vào kho phiên hiện tại. |
| [request()](../app/src/main/java/com/example/nova/ApiClient.java#L53) | Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback. |
| [uploadImages()](../app/src/main/java/com/example/nova/ApiClient.java#L118) | Đọc/nén ảnh trên luồng nền và truyền multipart có Bearer token; trả kết quả trên main thread. |
| [write()](../app/src/main/java/com/example/nova/ApiClient.java#L196) | Ghi chuỗi UTF-8 vào luồng xuất của phần thân yêu cầu. |
| [get()](../app/src/main/java/com/example/nova/ApiClient.java#L201) | Gửi GET qua lớp API dùng chung rồi xử lý dữ liệu trong callback. |
| [close()](../app/src/main/java/com/example/nova/ApiClient.java#L206) | Đánh dấu đã đóng, hủy callback/công việc nền và giải phóng tài nguyên liên quan. |
| [json()](../app/src/main/java/com/example/nova/ApiClient.java#L213) | Tạo JSONObject từ các cặp khóa/giá trị dùng cho body gửi API. |
| [readBytes()](../app/src/main/java/com/example/nova/ApiClient.java#L224) | Đọc luồng phản hồi thành byte[] với giới hạn kích thước để tránh dùng bộ nhớ quá mức. |
| [encode()](../app/src/main/java/com/example/nova/ApiClient.java#L236) | Mã hóa giá trị query bằng UTF-8 trước khi ghép URL để dấu cách/ký tự tiếng Việt không làm sai tham số. |

## AppState

[Mở file](../app/src/main/java/com/example/nova/AppState.java) — Trạng thái dùng chung của FE: route hiện tại, lịch sử Back, bộ lọc, danh mục Trang chủ, người dùng và thông tin đặt hàng tạm.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## CategoryUi

[Mở file](../app/src/main/java/com/example/nova/CategoryUi.java) — Gắn ảnh, tên và trạng thái chọn của một danh mục vào layout item_home_category.

| Phương thức | Chức năng |
| --- | --- |
| [CategoryUi()](../app/src/main/java/com/example/nova/CategoryUi.java#L15) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [bind()](../app/src/main/java/com/example/nova/CategoryUi.java#L18) | Gắn nội dung của đối tượng dữ liệu vào các View con của layout. |

## HomeBanner

[Mở file](../app/src/main/java/com/example/nova/HomeBanner.java) — Quản lý danh sách banner: tải ảnh, xử lý vuốt/chạm, chấm chỉ báo và tự chuyển khi màn hình đang hoạt động.

| Phương thức | Chức năng |
| --- | --- |
| [HomeBanner()](../app/src/main/java/com/example/nova/HomeBanner.java#L35) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [bindSwipe()](../app/src/main/java/com/example/nova/HomeBanner.java#L52) | Gắn GestureDetector để phân biệt thao tác chạm và vuốt banner. |
| [onDown()](../app/src/main/java/com/example/nova/HomeBanner.java#L55) | Xác nhận bắt đầu chuỗi sự kiện chạm để bộ nhận cử chỉ tiếp tục theo dõi. |
| [onSingleTapUp()](../app/src/main/java/com/example/nova/HomeBanner.java#L58) | Xử lý chạm ngắn lên banner hiện tại. |
| [onFling()](../app/src/main/java/com/example/nova/HomeBanner.java#L63) | Kiểm tra hướng/độ dài vận tốc vuốt rồi chuyển banner. |
| [setBanners()](../app/src/main/java/com/example/nova/HomeBanner.java#L83) | Nhận danh sách banner có dữ liệu hợp lệ và chọn banner để hiển thị. |
| [resume()](../app/src/main/java/com/example/nova/HomeBanner.java#L99) | Cho phép tự chuyển banner khi màn hình đang hoạt động. |
| [pause()](../app/src/main/java/com/example/nova/HomeBanner.java#L105) | Dừng bộ hẹn giờ tự chuyển banner khi màn hình bị che hoặc rời. |
| [schedule()](../app/src/main/java/com/example/nova/HomeBanner.java#L111) | Đặt callback tự chuyển banner sau khoảng thời gian quy định, chỉ khi màn hình còn hoạt động. |
| [select()](../app/src/main/java/com/example/nova/HomeBanner.java#L117) | Áp dụng lựa chọn và cập nhật các vùng giao diện phụ thuộc lựa chọn đó. |
| [updateDots()](../app/src/main/java/com/example/nova/HomeBanner.java#L133) | Cập nhật các chấm chỉ báo vị trí banner hiện tại. |

## ImagePicker

[Mở file](../app/src/main/java/com/example/nova/ImagePicker.java) — Mở bộ chọn ảnh hệ thống, nhận URI của đúng các ảnh người dùng đã chọn và chuyển lại cho màn hình yêu cầu.

| Phương thức | Chức năng |
| --- | --- |
| [ImagePicker()](../app/src/main/java/com/example/nova/ImagePicker.java#L25) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [open()](../app/src/main/java/com/example/nova/ImagePicker.java#L28) | Mở bộ chọn ảnh hệ thống và lưu callback cho lần chọn này. |
| [onActivityResult()](../app/src/main/java/com/example/nova/ImagePicker.java#L48) | Nhận kết quả từ Android và chuyển kết quả chọn ảnh tới ImagePicker. |
| [close()](../app/src/main/java/com/example/nova/ImagePicker.java#L72) | Đánh dấu đã đóng, hủy callback/công việc nền và giải phóng tài nguyên liên quan. |

## Images

[Mở file](../app/src/main/java/com/example/nova/Images.java) — Tải và giải mã ảnh ở luồng nền, lưu bộ đệm bitmap, kiểm tra View còn chờ đúng URL trước khi gán ảnh.

| Phương thức | Chức năng |
| --- | --- |
| [sizeOf()](../app/src/main/java/com/example/nova/Images.java#L32) | Tính dung lượng bitmap khi quản lý bộ đệm ảnh LRU. |
| [Images()](../app/src/main/java/com/example/nova/Images.java#L38) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [product()](../app/src/main/java/com/example/nova/Images.java#L44) | Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID. |
| [category()](../app/src/main/java/com/example/nova/Images.java#L52) | Tải ảnh hoặc xử lý đối tượng danh mục theo đầu vào. |
| [load()](../app/src/main/java/com/example/nova/Images.java#L57) | Kiểm tra bộ đệm rồi tải ảnh khi chưa có, gắn tag để tránh ảnh cũ bị gán nhầm View. |
| [fetch()](../app/src/main/java/com/example/nova/Images.java#L87) | Tải byte ảnh qua HTTP và giải mã bitmap theo kích thước mục tiêu. |
| [resolve()](../app/src/main/java/com/example/nova/Images.java#L135) | Chuẩn hóa URL ảnh từ API thành địa chỉ có thể tải. |
| [close()](../app/src/main/java/com/example/nova/Images.java#L151) | Đánh dấu đã đóng, hủy callback/công việc nền và giải phóng tài nguyên liên quan. |

## MainActivity

[Mở file](../app/src/main/java/com/example/nova/MainActivity.java) — Điểm điều phối Android: khôi phục trạng thái, quản lý phiên, chọn lớp Screen theo route và dựng thanh điều hướng.

| Phương thức | Chức năng |
| --- | --- |
| [state()](../app/src/main/java/com/example/nova/MainActivity.java#L36) | Trả AppState dùng chung để các Screen đọc/ghi trạng thái điều hướng và bộ lọc. |
| [ui()](../app/src/main/java/com/example/nova/MainActivity.java#L38) | Trả bộ hàm giao diện Ui của Activity. |
| [session()](../app/src/main/java/com/example/nova/MainActivity.java#L40) | Trả bộ quản lý phiên đăng nhập dùng chung. |
| [api()](../app/src/main/java/com/example/nova/MainActivity.java#L42) | Trả ApiClient dùng chung của Activity. |
| [images()](../app/src/main/java/com/example/nova/MainActivity.java#L44) | Trả bộ tải ảnh dùng chung của Activity. |
| [contentHost()](../app/src/main/java/com/example/nova/MainActivity.java#L46) | Trả vùng chứa nội dung màn hình. |
| [footerHost()](../app/src/main/java/com/example/nova/MainActivity.java#L48) | Trả vùng chứa thanh điều hướng hoặc thao tác ở chân màn hình. |
| [onCreate()](../app/src/main/java/com/example/nova/MainActivity.java#L52) | Khởi tạo thành phần và phục hồi trạng thái theo vòng đời Android. |
| [verifyStoredCustomerSession()](../app/src/main/java/com/example/nova/MainActivity.java#L92) | Gọi /me để xác minh token lưu trên máy trước khi dùng hồ sơ và mở màn hình. |
| [rejectNonCustomer()](../app/src/main/java/com/example/nova/MainActivity.java#L110) | Thu hồi token không thuộc vai trò hỗ trợ, xóa phiên cục bộ và đưa về đăng nhập. |
| [pickImages()](../app/src/main/java/com/example/nova/MainActivity.java#L121) | Mở bộ chọn ảnh hệ thống theo số lượng tối đa mà màn hình yêu cầu. |
| [onActivityResult()](../app/src/main/java/com/example/nova/MainActivity.java#L126) | Nhận kết quả từ Android và chuyển kết quả chọn ảnh tới ImagePicker. |
| [onSaveInstanceState()](../app/src/main/java/com/example/nova/MainActivity.java#L133) | Lưu trạng thái giao diện vào Bundle để phục hồi khi Android tạo lại Activity. |
| [onResume()](../app/src/main/java/com/example/nova/MainActivity.java#L161) | Tiếp tục công việc cần chạy khi màn hình trở lại foreground. |
| [onPause()](../app/src/main/java/com/example/nova/MainActivity.java#L169) | Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời. |
| [onDestroy()](../app/src/main/java/com/example/nova/MainActivity.java#L177) | Đóng các tài nguyên nền khi Activity bị hủy. |
| [onBackPressed()](../app/src/main/java/com/example/nova/MainActivity.java#L188) | Quay lại đích trước đó theo lịch sử điều hướng. |
| [loggedIn()](../app/src/main/java/com/example/nova/MainActivity.java#L201) | Kiểm tra có token phiên đang lưu; phiên vẫn cần BE xác thực khi gọi API. |
| [go()](../app/src/main/java/com/example/nova/MainActivity.java#L206) | Mở route mới và giữ đích hiện tại trong lịch sử Back. |
| [go()](../app/src/main/java/com/example/nova/MainActivity.java#L211) | Mở route mới và giữ đích hiện tại trong lịch sử Back. |
| [replace()](../app/src/main/java/com/example/nova/MainActivity.java#L225) | Thay route hiện tại để Back không quay về trạng thái thanh toán tạm đã hết hiệu lực. |
| [tab()](../app/src/main/java/com/example/nova/MainActivity.java#L232) | Chuyển tab chính và đặt lại lịch sử của nhánh điều hướng. |
| [requireLogin()](../app/src/main/java/com/example/nova/MainActivity.java#L240) | Chuyển sang đăng nhập nếu thiếu phiên, giữ lại đích để quay về sau khi xác thực. |
| [obj()](../app/src/main/java/com/example/nova/MainActivity.java#L250) | Chuẩn hóa dữ liệu thành JSONObject, dùng đối tượng rỗng khi kiểu không phù hợp. |
| [arr()](../app/src/main/java/com/example/nova/MainActivity.java#L255) | Đọc JSONArray trực tiếp hoặc items của phản hồi phân trang, tránh ép sai kiểu dữ liệu. |
| [get()](../app/src/main/java/com/example/nova/MainActivity.java#L260) | Gửi GET qua lớp API dùng chung rồi xử lý dữ liệu trong callback. |
| [send()](../app/src/main/java/com/example/nova/MainActivity.java#L282) | Gửi yêu cầu thay đổi dữ liệu bằng HTTP method, đường dẫn và JSON được cung cấp. |
| [alert()](../app/src/main/java/com/example/nova/MainActivity.java#L299) | Mở hộp thoại thông báo cho người dùng. |
| [toast()](../app/src/main/java/com/example/nova/MainActivity.java#L308) | Hiện thông báo ngắn của Android. |
| [confirm()](../app/src/main/java/com/example/nova/MainActivity.java#L313) | Mở hộp thoại xác nhận rồi chỉ chạy action khi người dùng đồng ý. |
| [isFullEditionRoute()](../app/src/main/java/com/example/nova/MainActivity.java#L323) | Kiểm tra route thuộc nhóm chức năng cần backend bản đầy đủ. |
| [unavailableFeature()](../app/src/main/java/com/example/nova/MainActivity.java#L335) | Thông báo giới hạn chức năng của chế độ API đang dùng. |
| [error()](../app/src/main/java/com/example/nova/MainActivity.java#L346) | Dựng thẻ lỗi có nút Thử lại gọi đúng tác vụ tải dữ liệu đã truyền vào. |
| [title()](../app/src/main/java/com/example/nova/MainActivity.java#L356) | Ánh xạ route hiện tại thành tiêu đề tiếng Việt. |
| [header()](../app/src/main/java/com/example/nova/MainActivity.java#L403) | Dựng vùng đầu màn hình với tiêu đề và thao tác quay lại phù hợp. |
| [setScreenTitle()](../app/src/main/java/com/example/nova/MainActivity.java#L427) | Đổi tiêu đề đang hiển thị trong phần đầu màn hình. |
| [isCustomerProfile()](../app/src/main/java/com/example/nova/MainActivity.java#L430) | Kiểm tra vai trò hồ sơ được ứng dụng hỗ trợ theo chế độ API hiện tại. |
| [navigation()](../app/src/main/java/com/example/nova/MainActivity.java#L436) | Dựng bốn tab chính, trạng thái đang chọn và badge giỏ hàng. |
| [updateCartBadge()](../app/src/main/java/com/example/nova/MainActivity.java#L470) | Cập nhật số lượng hiển thị trên badge của tab giỏ hàng. |
| [render()](../app/src/main/java/com/example/nova/MainActivity.java#L479) | Hủy controller cũ, tăng generation, dựng khung Activity và chọn đúng lớp Screen qua switch(route); generation chặn callback của màn hình trước. |

## PasswordRules

[Mở file](../app/src/main/java/com/example/nova/PasswordRules.java) — Quy tắc kiểm tra mật khẩu mới: độ dài và các nhóm ký tự bắt buộc; FE và BE dùng cùng yêu cầu.

| Phương thức | Chức năng |
| --- | --- |
| [PasswordRules()](../app/src/main/java/com/example/nova/PasswordRules.java#L13) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [isValid()](../app/src/main/java/com/example/nova/PasswordRules.java#L16) | Kiểm tra các quy tắc độ dài và nhóm ký tự bắt buộc của mật khẩu mới. |

## ProductGallery

[Mở file](../app/src/main/java/com/example/nova/ProductGallery.java) — Quản lý ảnh chi tiết sản phẩm, chọn thumbnail và đổi ảnh tương ứng biến thể/SKU.

| Phương thức | Chức năng |
| --- | --- |
| [ProductGallery()](../app/src/main/java/com/example/nova/ProductGallery.java#L24) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [selectVariant()](../app/src/main/java/com/example/nova/ProductGallery.java#L56) | Chọn ảnh tương ứng biến thể/SKU đang hiển thị. |
| [show()](../app/src/main/java/com/example/nova/ProductGallery.java#L68) | Hiển thị ảnh đang chọn và cập nhật trạng thái thumbnail. |

## QrCode

[Mở file](../app/src/main/java/com/example/nova/QrCode.java) — Dùng ZXing mã hóa chuỗi dữ liệu thanh toán thành bitmap QR để hiển thị trên màn hình.

| Phương thức | Chức năng |
| --- | --- |
| [hints()](../app/src/main/java/com/example/nova/QrCode.java#L19) | Thiết lập charset và mức sửa lỗi cho dữ liệu QR dùng bởi ZXing. |
| [encode()](../app/src/main/java/com/example/nova/QrCode.java#L27) | Dùng MultiFormatWriter tạo ma trận QR, đổi từng ô thành pixel của Bitmap. |

## SaleReminderReceiver

[Mở file](../app/src/main/java/com/example/nova/SaleReminderReceiver.java) — Đặt/hủy nhắc Flash Sale bằng AlarmManager và nhận sự kiện để hiển thị thông báo Android.

| Phương thức | Chức năng |
| --- | --- |
| [instant()](../app/src/main/java/com/example/nova/SaleReminderReceiver.java#L19) | Chuyển chuỗi thời gian API thành thời điểm dùng để đặt nhắc. |
| [alarmIntent()](../app/src/main/java/com/example/nova/SaleReminderReceiver.java#L32) | Tạo PendingIntent ổn định để có thể đặt hoặc hủy đúng thông báo nhắc. |
| [schedule()](../app/src/main/java/com/example/nova/SaleReminderReceiver.java#L47) | Lên lịch công việc hẹn giờ hoặc nhắc bằng API Android phù hợp với lớp hiện tại. |
| [cancel()](../app/src/main/java/com/example/nova/SaleReminderReceiver.java#L70) | Hủy lịch nhắc đã đặt cho đối tượng được chọn. |
| [onReceive()](../app/src/main/java/com/example/nova/SaleReminderReceiver.java#L80) | Xử lý sự kiện BroadcastReceiver và hiển thị thông báo nhắc ưu đãi. |

## SecureSession

[Mở file](../app/src/main/java/com/example/nova/SecureSession.java) — Lưu token đã mã hóa bằng AES/GCM; khóa mã hóa nằm trong Android Keystore, URL API lấy từ BuildConfig.

| Phương thức | Chức năng |
| --- | --- |
| [SecureSession()](../app/src/main/java/com/example/nova/SecureSession.java#L26) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [key()](../app/src/main/java/com/example/nova/SecureSession.java#L39) | Lấy hoặc tạo khóa AES trong Android Keystore; khóa không ghi thành chuỗi thuần trong preferences. |
| [save()](../app/src/main/java/com/example/nova/SecureSession.java#L56) | Mã hóa token bằng AES/GCM và ghi ciphertext/IV vào SharedPreferences; lựa chọn ghi nhớ quyết định có lưu lâu dài hay không. |
| [save()](../app/src/main/java/com/example/nova/SecureSession.java#L71) | Mã hóa token bằng AES/GCM và ghi ciphertext/IV vào SharedPreferences; lựa chọn ghi nhớ quyết định có lưu lâu dài hay không. |
| [token()](../app/src/main/java/com/example/nova/SecureSession.java#L80) | Giải mã token đã lưu; khi dữ liệu không hợp lệ thì trả chuỗi rỗng. |
| [clear()](../app/src/main/java/com/example/nova/SecureSession.java#L100) | Xóa token trong bộ nhớ và dữ liệu phiên đã lưu trong SharedPreferences. |
| [baseUrl()](../app/src/main/java/com/example/nova/SecureSession.java#L106) | Lấy URL backend đã cấu hình trong BuildConfig của bản Android. |

## Ui

[Mở file](../app/src/main/java/com/example/nova/Ui.java) — Bộ hàm dựng giao diện native dùng chung: font, màu, dp, hàng/cột, nút, thẻ, chip và định dạng tiền Việt.

| Phương thức | Chức năng |
| --- | --- |
| [Ui()](../app/src/main/java/com/example/nova/Ui.java#L35) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [applyTypography()](../app/src/main/java/com/example/nova/Ui.java#L54) | Duyệt cây View và áp dụng font Inter phù hợp cho chữ thường/chữ đậm. |
| [d()](../app/src/main/java/com/example/nova/Ui.java#L67) | Đổi dp sang pixel theo mật độ màn hình thiết bị. |
| [col()](../app/src/main/java/com/example/nova/Ui.java#L72) | Tạo LinearLayout theo chiều dọc. |
| [row()](../app/src/main/java/com/example/nova/Ui.java#L79) | Tạo LinearLayout theo chiều ngang. |
| [pad()](../app/src/main/java/com/example/nova/Ui.java#L87) | Đặt cùng một khoảng đệm cho bốn cạnh View theo dp. |
| [text()](../app/src/main/java/com/example/nova/Ui.java#L92) | Tạo TextView với nội dung, cỡ chữ, màu và font của thiết kế NOVA. |
| [text()](../app/src/main/java/com/example/nova/Ui.java#L106) | Tạo TextView với nội dung, cỡ chữ, màu và font của thiết kế NOVA. |
| [add()](../app/src/main/java/com/example/nova/Ui.java#L111) | Thêm View vào bố cục với kích thước phù hợp, giữ đúng chiều cao khi đã quy định. |
| [add()](../app/src/main/java/com/example/nova/Ui.java#L117) | Thêm View vào bố cục với kích thước phù hợp, giữ đúng chiều cao khi đã quy định. |
| [heading()](../app/src/main/java/com/example/nova/Ui.java#L122) | Áp dụng font tiêu đề cho TextView. |
| [action()](../app/src/main/java/com/example/nova/Ui.java#L127) | Gắn sự kiện chạm, trạng thái focus và hiệu ứng ripple cho toàn bộ View. |
| [space()](../app/src/main/java/com/example/nova/Ui.java#L134) | Thêm một View đệm để tạo khoảng cách trong bố cục. |
| [weight()](../app/src/main/java/com/example/nova/Ui.java#L140) | Thêm phần tử với layout_weight để chia phần chiều rộng còn lại. |
| [bg()](../app/src/main/java/com/example/nova/Ui.java#L146) | Tạo nền màu với góc bo tròn theo dp. |
| [outline()](../app/src/main/java/com/example/nova/Ui.java#L154) | Tạo nền có viền và góc bo theo thiết kế dùng chung. |
| [blueCard()](../app/src/main/java/com/example/nova/Ui.java#L161) | Tạo thẻ nền xanh nhạt cho các vùng nội dung được nhấn mạnh. |
| [card()](../app/src/main/java/com/example/nova/Ui.java#L174) | Tạo khung thẻ dùng chung để chứa một nhóm thông tin. |
| [button()](../app/src/main/java/com/example/nova/Ui.java#L183) | Tạo nút theo màu và kiểu chữ dùng chung của NOVA. |
| [link()](../app/src/main/java/com/example/nova/Ui.java#L196) | Tạo chữ có thể chạm để thực hiện thao tác điều hướng. |
| [field()](../app/src/main/java/com/example/nova/Ui.java#L204) | Tạo EditText với nhãn gợi ý, inputType và kiểu nền/font dùng chung. |
| [labelPair()](../app/src/main/java/com/example/nova/Ui.java#L224) | Dựng một dòng nhãn và giá trị tương ứng. |
| [money()](../app/src/main/java/com/example/nova/Ui.java#L233) | Định dạng số tiền theo Locale Việt Nam. |
| [icon()](../app/src/main/java/com/example/nova/Ui.java#L238) | Tìm và nạp drawable theo tên tài nguyên vào ImageView. |
| [chip()](../app/src/main/java/com/example/nova/Ui.java#L248) | Tạo lựa chọn dạng chip với trạng thái chọn và callback khi chạm. |
| [divider()](../app/src/main/java/com/example/nova/Ui.java#L256) | Tạo đường phân cách dùng chung giữa các vùng nội dung. |

## UploadImage

[Mở file](../app/src/main/java/com/example/nova/UploadImage.java) — Đọc URI ảnh đã chọn, giới hạn dung lượng/kích thước, xoay EXIF, thu nhỏ và nén JPEG trước khi gửi lên BE.

| Phương thức | Chức năng |
| --- | --- |
| [jpeg()](../app/src/main/java/com/example/nova/UploadImage.java#L27) | Đọc URI vào tệp tạm có giới hạn, xoay EXIF, thu nhỏ và nén JPEG; thu hồi bitmap/tệp tạm trong finally. |
| [orientation()](../app/src/main/java/com/example/nova/UploadImage.java#L93) | Đọc EXIF và tạo ma trận xoay/lật để ảnh tải lên có hướng hiển thị đúng. |

## AccountScreen

[Mở file](../app/src/main/java/com/example/nova/screens/AccountScreen.java) — Hiển thị hồ sơ tóm tắt, đăng xuất, lối vào địa chỉ, đơn hàng, thành viên và các tiện ích.

| Phương thức | Chức năng |
| --- | --- |
| [AccountScreen()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L37) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L40) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L43) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [showGuest()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L64) | Dựng giao diện tài khoản khi chưa có phiên và gắn nút đăng nhập/đăng ký. |
| [openAuth()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L90) | Lưu đích Tài khoản rồi mở màn hình đăng nhập hoặc đăng ký. |
| [showIdentity()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L96) | Hiển thị tên, email, avatar và thao tác mở hồ sơ. |
| [showMembership()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L116) | Hiển thị thông tin hạng/điểm thành viên khi chế độ backend hỗ trợ. |
| [showOrders()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L180) | Dựng danh sách hoặc các lối truy cập đơn hàng từ dữ liệu hiện tại. |
| [showSettings()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L230) | Dựng các mục cài đặt và chức năng tài khoản. |
| [addSetting()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L253) | Tạo một hàng cài đặt có icon, nhãn và callback khi chạm. |
| [showFooter()](../app/src/main/java/com/example/nova/screens/AccountScreen.java#L279) | Dựng phần thông tin/thao tác cuối màn Tài khoản. |

## AddAddressScreen

[Mở file](../app/src/main/java/com/example/nova/screens/AddAddressScreen.java) — Chọn chế độ tạo mới và dùng xử lý biểu mẫu chung của AddressFormScreen.

| Phương thức | Chức năng |
| --- | --- |
| [AddAddressScreen()](../app/src/main/java/com/example/nova/screens/AddAddressScreen.java#L15) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/AddAddressScreen.java#L17) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |

## AddressFormScreen

[Mở file](../app/src/main/java/com/example/nova/screens/AddressFormScreen.java) — Đọc thông tin hiện tại, kiểm tra biểu mẫu và lưu thay đổi. Chứa phần xử lý chung cho cả thêm và sửa.

| Phương thức | Chức năng |
| --- | --- |
| [AddressFormScreen()](../app/src/main/java/com/example/nova/screens/AddressFormScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/AddressFormScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/AddressFormScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [bindFields()](../app/src/main/java/com/example/nova/screens/AddressFormScreen.java#L56) | Gắn dữ liệu địa chỉ hiện có vào các trường nhập của biểu mẫu. |

## AddressesScreen

[Mở file](../app/src/main/java/com/example/nova/screens/AddressesScreen.java) — Nạp địa chỉ đã lưu, hiển thị mặc định và xử lý mở thêm, sửa hoặc xóa.

| Phương thức | Chức năng |
| --- | --- |
| [AddressesScreen()](../app/src/main/java/com/example/nova/screens/AddressesScreen.java#L20) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/AddressesScreen.java#L22) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/AddressesScreen.java#L26) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## AdminImagesScreen

[Mở file](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java) — Giao diện quản lý ảnh cho ADMIN: tìm sản phẩm, xem số chỗ còn lại, chọn nhiều ảnh và gửi multipart đến API quản trị.

| Phương thức | Chức năng |
| --- | --- |
| [AdminImagesScreen()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L39) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L41) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L44) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [active()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L58) | Kiểm tra phiên bản màn quản lý ảnh và vòng đời Activity trước khi dùng phản hồi API. |
| [onDispose()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L64) | Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn. |
| [expired()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L67) | Xóa trạng thái phiên đã hết hạn và yêu cầu đăng nhập lại trước khi quản lý ảnh. |
| [showSearch()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L79) | Dựng vùng tìm sản phẩm và trạng thái của danh sách quản lý ảnh. |
| [startSearch()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L102) | Chuẩn hóa từ khóa, đặt lại phân trang và bắt đầu tìm sản phẩm. |
| [loadPage()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L110) | Lấy một trang kết quả tìm sản phẩm và kiểm tra yêu cầu còn thuộc màn hình hiện tại. |
| [loadProduct()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L170) | Lấy chi tiết sản phẩm kèm bộ ảnh trước khi mở phần thêm ảnh. |
| [showProduct()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L186) | Hiển thị bộ ảnh, giới hạn số ảnh và các nút của sản phẩm đang quản lý. |
| [chooseImages()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L253) | Mở bộ chọn nhiều ảnh với số lượng tối đa bằng số chỗ ảnh còn lại. |
| [upload()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L265) | Gửi ảnh đã được kiểm tra đến điểm nhận upload và chỉ nhận URL trả về hợp lệ. |
| [showError()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L286) | Đưa lỗi vào vùng trạng thái và cập nhật thao tác phù hợp của giao diện. |
| [productStatus()](../app/src/main/java/com/example/nova/screens/AdminImagesScreen.java#L293) | Ghép tình trạng sản phẩm thành nhãn trong giao diện quản lý ảnh. |

## BaseScreen

[Mở file](../app/src/main/java/com/example/nova/screens/BaseScreen.java) — Lớp nền cho các màn hình: dùng chung MainActivity, AppState, ApiClient, Ui, Images và các hàm dựng/gắn giao diện.

| Phương thức | Chức năng |
| --- | --- |
| [BaseScreen()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L42) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L51) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L53) | Hợp đồng để lớp Screen cụ thể dựng và gắn giao diện; MainActivity gọi sau khi chọn route. |
| [onResume()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L56) | Tiếp tục công việc cần chạy khi màn hình trở lại foreground. |
| [onPause()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L58) | Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời. |
| [onDispose()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L60) | Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn. |
| [loggedIn()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L62) | Kiểm tra có token phiên đang lưu; phiên vẫn cần BE xác thực khi gọi API. |
| [requireLogin()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L64) | Chuyển sang đăng nhập nếu thiếu phiên, giữ lại đích để quay về sau khi xác thực. |
| [go()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L66) | Mở route mới và giữ đích hiện tại trong lịch sử Back. |
| [go()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L68) | Mở route mới và giữ đích hiện tại trong lịch sử Back. |
| [tab()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L70) | Chuyển tab chính và đặt lại lịch sử của nhánh điều hướng. |
| [onBackPressed()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L72) | Quay lại đích trước đó theo lịch sử điều hướng. |
| [isFinishing()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L74) | Kiểm tra Activity đang kết thúc để không tiếp tục cập nhật giao diện. |
| [isDestroyed()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L76) | Kiểm tra Activity đã bị hủy trước khi thao tác với View. |
| [requestPermissions()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L78) | Chuyển yêu cầu quyền Android tới Activity sở hữu màn hình. |
| [getLayoutInflater()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L80) | Lấy LayoutInflater của Activity để nạp các layout XML. |
| [updateCartBadge()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L82) | Cập nhật số lượng hiển thị trên badge của tab giỏ hàng. |
| [rejectNonCustomer()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L84) | Thu hồi token không thuộc vai trò hỗ trợ, xóa phiên cục bộ và đưa về đăng nhập. |
| [get()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L86) | Gửi GET qua lớp API dùng chung rồi xử lý dữ liệu trong callback. |
| [send()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L88) | Gửi yêu cầu thay đổi dữ liệu bằng HTTP method, đường dẫn và JSON được cung cấp. |
| [alert()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L90) | Mở hộp thoại thông báo cho người dùng. |
| [toast()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L92) | Hiện thông báo ngắn của Android. |
| [confirm()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L94) | Mở hộp thoại xác nhận rồi chỉ chạy action khi người dùng đồng ý. |
| [bindBody()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L96) | Nạp layout và đặt screen_body làm vùng chứa nội dung động của màn hình. |
| [obj()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L105) | Chuẩn hóa dữ liệu thành JSONObject, dùng đối tượng rỗng khi kiểu không phù hợp. |
| [arr()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L110) | Đọc JSONArray trực tiếp hoặc items của phản hồi phân trang, tránh ép sai kiểu dữ liệu. |
| [empty()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L120) | Dựng trạng thái chưa có dữ liệu với tiêu đề và mô tả. |
| [section()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L131) | Dựng tiêu đề một khối nội dung và gắn thao tác xem thêm khi được cung cấp. |
| [gap()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L140) | Thêm khoảng cách dọc giữa các khối giao diện. |
| [image()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L145) | Tạo vùng ảnh sản phẩm và chuyển URL cho bộ tải ảnh dùng chung. |
| [inflateLayout()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L155) | Inflate XML, bỏ ViewStub chỉ dành cho Preview và áp dụng font dùng chung. |
| [screen()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L167) | Nạp layout của màn hình và gắn vào vùng nội dung của Activity. |
| [productGrid()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L174) | Chia danh sách JSON thành lưới hai cột; gắn ảnh, giá và sự kiện mở đúng sản phẩm. |
| [productGrid()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L179) | Chia danh sách JSON thành lưới hai cột; gắn ảnh, giá và sự kiện mở đúng sản phẩm. |
| [addToCart()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L242) | Gửi thao tác thêm SKU vào giỏ và cập nhật thông tin giỏ từ kết quả API. |
| [addToCart()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L247) | Gửi thao tác thêm SKU vào giỏ và cập nhật thông tin giỏ từ kết quả API. |
| [otpDialog()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L270) | Dựng hộp thoại nhập mã OTP theo callback của thao tác yêu cầu. |
| [formatAddress()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L290) | Ghép các trường địa chỉ thành một chuỗi dễ đọc. |
| [date()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L298) | Định dạng dữ liệu ngày giờ để hiển thị trên giao diện. |
| [statusLabel()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L307) | Đổi mã trạng thái nghiệp vụ thành nhãn hiển thị cho người dùng. |
| [timeline()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L331) | Dựng các mốc thời gian/trạng thái từ dữ liệu API. |
| [reviewForm()](../app/src/main/java/com/example/nova/screens/BaseScreen.java#L350) | Dựng biểu mẫu đánh giá và gửi nội dung người dùng đã nhập. |

## CancelOrderResultScreen

[Mở file](../app/src/main/java/com/example/nova/screens/CancelOrderResultScreen.java) — Nạp trạng thái sau khi hủy và thông tin hoàn tiền nếu có.

| Phương thức | Chức năng |
| --- | --- |
| [CancelOrderResultScreen()](../app/src/main/java/com/example/nova/screens/CancelOrderResultScreen.java#L19) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/CancelOrderResultScreen.java#L21) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/CancelOrderResultScreen.java#L24) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## CancelOrderScreen

[Mở file](../app/src/main/java/com/example/nova/screens/CancelOrderScreen.java) — Kiểm tra trạng thái, nhận lý do và gửi yêu cầu hủy.

| Phương thức | Chức năng |
| --- | --- |
| [CancelOrderScreen()](../app/src/main/java/com/example/nova/screens/CancelOrderScreen.java#L24) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/CancelOrderScreen.java#L26) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/CancelOrderScreen.java#L29) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## CartScreen

[Mở file](../app/src/main/java/com/example/nova/screens/CartScreen.java) — Nạp giỏ, cập nhật số lượng, xóa món hoặc xóa giỏ và chuyển sang đặt hàng.

| Phương thức | Chức năng |
| --- | --- |
| [CartScreen()](../app/src/main/java/com/example/nova/screens/CartScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/CartScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/CartScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## CatalogScreen

[Mở file](../app/src/main/java/com/example/nova/screens/CatalogScreen.java) — Nạp lưới sản phẩm theo danh mục, hỗ trợ chuyển nhóm, từ khóa, sắp xếp và lọc.

| Phương thức | Chức năng |
| --- | --- |
| [CatalogScreen()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L27) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L29) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L32) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [showCategories()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L78) | Đọc cây danh mục, đặt tiêu đề/ngữ cảnh hiện tại và dựng lựa chọn chuyển nhóm. |
| [findById()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L130) | Tìm đối tượng danh mục có ID cần tìm trong danh sách đã tải. |
| [hasChildren()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L138) | Kiểm tra danh mục có nhóm con để xác định nhánh điều hướng. |
| [slug()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L146) | Lấy slug điều hướng danh mục, dùng code khi dữ liệu chưa có slug. |
| [addCategory()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L151) | Tạo lựa chọn danh mục với trạng thái đang chọn và callback. |
| [chooseCategory()](../app/src/main/java/com/example/nova/screens/CatalogScreen.java#L162) | Ghi danh mục vào bộ lọc, bỏ thương hiệu cũ và dựng lại kết quả. |

## CategoryScreen

[Mở file](../app/src/main/java/com/example/nova/screens/CategoryScreen.java) — Chọn danh mục cha, xem nhóm con, thương hiệu, phân khúc giá và ô sản phẩm nổi bật.

| Phương thức | Chức năng |
| --- | --- |
| [CategoryScreen()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L39) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [select()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L66) | Áp dụng lựa chọn và cập nhật các vùng giao diện phụ thuộc lựa chọn đó. |
| [showBrands()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L137) | Dựng các thương hiệu phù hợp với danh mục đang chọn. |
| [labels()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L142) | Lấy danh sách nhãn từ dữ liệu JSON để dựng lựa chọn. |
| [addFeaturedProducts()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L149) | Thêm các ô sản phẩm nổi bật vào panel danh mục. |
| [shortProductName()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L182) | Rút gọn tên sản phẩm cho vùng ô nhỏ mà vẫn giữ phần nhận diện. |
| [panel()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L190) | Tạo panel nội dung với nền và khoảng cách của màn Danh mục. |
| [addChoices()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L204) | Dựng các chip lựa chọn và chuyển lựa chọn thành tham số khi mở danh sách sản phẩm. |
| [products()](../app/src/main/java/com/example/nova/screens/CategoryScreen.java#L231) | Ghi danh mục/thương hiệu/khoảng giá vào AppState rồi mở trang danh sách sản phẩm. |

## ChangeEmailScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ChangeEmailScreen.java) — Nhập địa chỉ mới và mật khẩu hiện tại, gửi yêu cầu rồi mở bước xác thực OTP.

| Phương thức | Chức năng |
| --- | --- |
| [ChangeEmailScreen()](../app/src/main/java/com/example/nova/screens/ChangeEmailScreen.java#L21) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ChangeEmailScreen.java#L23) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ChangeEmailScreen.java#L26) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## ChangePasswordScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ChangePasswordScreen.java) — Kiểm tra mật khẩu hiện tại, xác nhận mật khẩu mới rồi gửi cập nhật.

| Phương thức | Chức năng |
| --- | --- |
| [ChangePasswordScreen()](../app/src/main/java/com/example/nova/screens/ChangePasswordScreen.java#L22) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ChangePasswordScreen.java#L24) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ChangePasswordScreen.java#L27) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## ChatScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ChatScreen.java) — Nạp lịch sử, gửi tin nhắn và làm mới hội thoại.

| Phương thức | Chức năng |
| --- | --- |
| [ChatScreen()](../app/src/main/java/com/example/nova/screens/ChatScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ChatScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ChatScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## CheckoutScreen

[Mở file](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java) — Ghép địa chỉ, phương thức, voucher và ghi chú. Yêu cầu báo giá trước khi gửi đơn.

| Phương thức | Chức năng |
| --- | --- |
| [CheckoutScreen()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [beforeTextChanged()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L69) | Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó. |
| [onTextChanged()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L71) | Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu. |
| [afterTextChanged()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L73) | Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi. |
| [beforeTextChanged()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L81) | Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó. |
| [onTextChanged()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L84) | Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu. |
| [afterTextChanged()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L92) | Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi. |
| [quoteBody()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L135) | Ghép địa chỉ, phương thức, voucher và ghi chú thành JSON dùng để yêu cầu báo giá. |
| [refreshCheckoutQuote()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L146) | Gọi API báo giá và bỏ qua phản hồi nếu thông tin đặt hàng đã đổi trong lúc chờ. |
| [showCheckoutQuote()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L170) | Hiển thị các dòng tiền và tổng thanh toán từ báo giá do BE trả. |
| [sameQuote()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L197) | So sánh dữ liệu đặt hàng hiện tại với bản đã dùng để nhận báo giá. |
| [placeCheckout()](../app/src/main/java/com/example/nova/screens/CheckoutScreen.java#L205) | Kiểm tra báo giá/địa chỉ, gửi tạo đơn với khóa chống lặp và chuyển đến kết quả. |

## FavoritesScreen

[Mở file](../app/src/main/java/com/example/nova/screens/FavoritesScreen.java) — Nạp lưới các sản phẩm khách đã lưu và cho phép mở chi tiết.

| Phương thức | Chức năng |
| --- | --- |
| [FavoritesScreen()](../app/src/main/java/com/example/nova/screens/FavoritesScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/FavoritesScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/FavoritesScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## FlashSaleScreen

[Mở file](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java) — Nạp khung giờ, giá, số lượng còn lại, đếm ngược và xử lý nhắc ưu đãi.

| Phương thức | Chức năng |
| --- | --- |
| [FlashSaleScreen()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [flashSlot()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L56) | Dựng một khung giờ Flash Sale gồm sản phẩm, thời hạn và thao tác liên quan. |
| [startCountdown()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L145) | Lên lịch cập nhật bộ đếm theo thời điểm bắt đầu/kết thúc chương trình. |
| [run()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L151) | Bước công việc hẹn giờ: cập nhật trạng thái hiện tại rồi lên lịch tiếp nếu còn hợp lệ. |
| [onViewAttachedToWindow()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L190) | Tiếp tục công việc khi View được gắn trở lại cửa sổ. |
| [onViewDetachedFromWindow()](../app/src/main/java/com/example/nova/screens/FlashSaleScreen.java#L193) | Dừng callback/hẹn giờ khi View rời cửa sổ. |

## ForgotPasswordScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ForgotPasswordScreen.java) — Gửi email để yêu cầu mã khôi phục.

| Phương thức | Chức năng |
| --- | --- |
| [ForgotPasswordScreen()](../app/src/main/java/com/example/nova/screens/ForgotPasswordScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ForgotPasswordScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ForgotPasswordScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## HomeCategoryBrowser

[Mở file](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java) — Khối Sản phẩm theo danh mục dưới phần khám phá: hiển thị mọi danh mục gốc, lọc nhóm con/thương hiệu, mở trang danh sách qua Xem thêm.

| Phương thức | Chức năng |
| --- | --- |
| [HomeCategoryBrowser()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L35) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L37) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L39) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [onDispose()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L54) | Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn. |
| [active()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L56) | Kiểm tra helper chưa bị dispose, generation vẫn khớp và Activity chưa kết thúc/hủy. |
| [slug()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L58) | Lấy slug điều hướng danh mục, dùng code khi dữ liệu chưa có slug. |
| [effectiveCategory()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L60) | Lấy danh mục con đang chọn; nếu chưa chọn con thì dùng danh mục gốc. |
| [openCatalog()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L62) | Ghi bộ lọc danh mục/thương hiệu vào AppState rồi mở route products. |
| [showCategoriesError()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L70) | Hiện trạng thái lỗi danh mục và gắn thao tác thử lại. |
| [setCategories()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L76) | Nhận danh mục từ API, phân nhóm gốc/con và khôi phục lựa chọn còn hợp lệ. |
| [root()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L95) | Tìm đối tượng danh mục gốc khớp lựa chọn hiện tại. |
| [updateSelection()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L100) | Tăng phiên bản lựa chọn, xóa kết quả cũ và tải lại dữ liệu cho bộ lọc mới. |
| [renderRoots()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L107) | Dựng mọi danh mục gốc thành các hàng ba ô, đánh dấu mục đang chọn. |
| [renderChildren()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L134) | Dựng lựa chọn Tất cả và các danh mục con của nhóm gốc hiện tại. |
| [addChild()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L146) | Gắn ảnh/tên/sự kiện cho một ô nhóm con, cập nhật bộ lọc khi chạm. |
| [loadBrands()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L157) | Lấy các thương hiệu của danh mục hiện tại và bỏ qua phản hồi của lựa chọn cũ. |
| [renderBrands()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L180) | Dựng lại các chip thương hiệu theo dữ liệu và trạng thái chọn. |
| [addBrand()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L191) | Tạo chip thương hiệu; khi chọn sẽ bỏ kết quả cũ và tải sản phẩm tương ứng. |
| [loadProducts()](../app/src/main/java/com/example/nova/screens/HomeCategoryBrowser.java#L206) | Lấy trang sản phẩm đầu tiên theo danh mục/thương hiệu và kiểm tra phiên bản yêu cầu trước khi vẽ. |

## HomeScreen

[Mở file](../app/src/main/java/com/example/nova/screens/HomeScreen.java) — Nạp banner, danh mục hai hàng, hàng mới về, bán chạy và nổi bật. Hiện Flash Sale khi có chương trình hoạt động từ API tương ứng.

| Phương thức | Chức năng |
| --- | --- |
| [onResume()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L51) | Tiếp tục công việc cần chạy khi màn hình trở lại foreground. |
| [onPause()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L58) | Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời. |
| [onDispose()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L66) | Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn. |
| [HomeScreen()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L72) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L74) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L78) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [loadFlashSales()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L118) | Tải các khung Flash Sale và làm mới theo mốc thời gian của dữ liệu. |
| [activeFlashItems()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L141) | Lọc các mục Flash Sale còn hiệu lực tại thời điểm hiện tại. |
| [nextFlashRefresh()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L162) | Tính thời điểm cần tải lại khi một chương trình bắt đầu hoặc kết thúc. |
| [showDiscoverTabs()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L176) | Dựng hai tab Bán chạy/Nổi bật và cập nhật danh sách khi người dùng chuyển tab. |
| [loadNewArrivals()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L216) | Tải nhóm hàng mới về và dựng các thẻ sản phẩm tương ứng. |
| [openCategory()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L238) | Ghi ngữ cảnh danh mục, bỏ bộ lọc không còn phù hợp rồi mở danh sách sản phẩm. |
| [loadHomeCategories()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L250) | Lấy danh mục từ API và chuyển cùng dữ liệu cho dải khám phá/khối sản phẩm theo danh mục. |
| [populateHomeCategories()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L290) | Dựng dải danh mục hai hàng, gắn ảnh và cho phép vuốt ngang. |
| [renderFlashCards()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L320) | Dựng các thẻ Flash Sale với giá, lượng còn lại và thao tác tương ứng. |
| [renderFullDiscovery()](../app/src/main/java/com/example/nova/screens/HomeScreen.java#L432) | Dựng các khối khám phá dùng cho chế độ API đầy đủ. |

## LoginScreen

[Mở file](../app/src/main/java/com/example/nova/screens/LoginScreen.java) — Nhập email và mật khẩu, ghi nhớ phiên, xử lý tài khoản chưa xác thực và điều hướng sang đăng ký hoặc quên mật khẩu.

| Phương thức | Chức năng |
| --- | --- |
| [LoginScreen()](../app/src/main/java/com/example/nova/screens/LoginScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/LoginScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/LoginScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## MembershipScreen

[Mở file](../app/src/main/java/com/example/nova/screens/MembershipScreen.java) — Nạp hạng, điểm tích lũy, tiến độ lên hạng và lịch sử điểm.

| Phương thức | Chức năng |
| --- | --- |
| [MembershipScreen()](../app/src/main/java/com/example/nova/screens/MembershipScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/MembershipScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/MembershipScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## NotificationsScreen

[Mở file](../app/src/main/java/com/example/nova/screens/NotificationsScreen.java) — Nạp danh sách, cập nhật đã đọc và mở đích liên quan khi có dữ liệu điều hướng.

| Phương thức | Chức năng |
| --- | --- |
| [NotificationsScreen()](../app/src/main/java/com/example/nova/screens/NotificationsScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/NotificationsScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/NotificationsScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## OrderDetailScreen

[Mở file](../app/src/main/java/com/example/nova/screens/OrderDetailScreen.java) — Hiển thị thông tin đơn và mở các thao tác phù hợp với trạng thái như thanh toán, theo dõi, hủy, đổi trả hoặc viết đánh giá.

| Phương thức | Chức năng |
| --- | --- |
| [OrderDetailScreen()](../app/src/main/java/com/example/nova/screens/OrderDetailScreen.java#L21) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/OrderDetailScreen.java#L23) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/OrderDetailScreen.java#L26) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [cancelUnpaid()](../app/src/main/java/com/example/nova/screens/OrderDetailScreen.java#L110) | Xác nhận và gửi hủy đơn chưa thanh toán, sau đó mở kết quả từ API. |

## OrderSuccessScreen

[Mở file](../app/src/main/java/com/example/nova/screens/OrderSuccessScreen.java) — Hiển thị mã đơn, số tiền và phương thức. Mở chi tiết hoặc quay lại mua sắm.

| Phương thức | Chức năng |
| --- | --- |
| [OrderSuccessScreen()](../app/src/main/java/com/example/nova/screens/OrderSuccessScreen.java#L17) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/OrderSuccessScreen.java#L19) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/OrderSuccessScreen.java#L22) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## OrderTrackingScreen

[Mở file](../app/src/main/java/com/example/nova/screens/OrderTrackingScreen.java) — Hiển thị mã vận đơn, địa chỉ và lịch sử trạng thái vận chuyển.

| Phương thức | Chức năng |
| --- | --- |
| [OrderTrackingScreen()](../app/src/main/java/com/example/nova/screens/OrderTrackingScreen.java#L18) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/OrderTrackingScreen.java#L20) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/OrderTrackingScreen.java#L23) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## OrdersScreen

[Mở file](../app/src/main/java/com/example/nova/screens/OrdersScreen.java) — Nạp danh sách đơn và lọc theo trạng thái.

| Phương thức | Chức năng |
| --- | --- |
| [OrdersScreen()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L28) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L30) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L33) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [showTabs()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L47) | Dựng các tab trạng thái đơn hàng và cập nhật bộ lọc khi chọn. |
| [showOrders()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L67) | Dựng danh sách hoặc các lối truy cập đơn hàng từ dữ liệu hiện tại. |
| [orderCard()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L93) | Dựng một thẻ đơn hàng với mã, trạng thái, món hàng và tổng tiền. |
| [orderItem()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L125) | Dựng một dòng sản phẩm trong thẻ đơn hàng. |
| [addProgress()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L145) | Thêm bước tiến độ theo trạng thái đơn. |
| [month()](../app/src/main/java/com/example/nova/screens/OrdersScreen.java#L168) | Định dạng tháng của thời điểm tạo đơn để nhóm/hiển thị lịch sử. |

## OtpScreen

[Mở file](../app/src/main/java/com/example/nova/screens/OtpScreen.java) — Xác nhận mã cho đăng ký, khôi phục mật khẩu hoặc đổi email. Xử lý gửi lại mã theo luồng.

| Phương thức | Chức năng |
| --- | --- |
| [OtpScreen()](../app/src/main/java/com/example/nova/screens/OtpScreen.java#L24) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/OtpScreen.java#L26) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/OtpScreen.java#L29) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [beforeTextChanged()](../app/src/main/java/com/example/nova/screens/OtpScreen.java#L59) | Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó. |
| [onTextChanged()](../app/src/main/java/com/example/nova/screens/OtpScreen.java#L61) | Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu. |
| [afterTextChanged()](../app/src/main/java/com/example/nova/screens/OtpScreen.java#L66) | Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi. |
| [updateDigits()](../app/src/main/java/com/example/nova/screens/OtpScreen.java#L154) | Đồng bộ chuỗi mã OTP thành các ô chữ số hiển thị. |

## PaymentDetailScreen

[Mở file](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java) — Hiển thị số tiền, hướng dẫn, QR hoặc cổng thanh toán và theo dõi trạng thái giao dịch.

| Phương thức | Chức năng |
| --- | --- |
| [run()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L41) | Bước công việc hẹn giờ: cập nhật trạng thái hiện tại rồi lên lịch tiếp nếu còn hợp lệ. |
| [PaymentDetailScreen()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L49) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L51) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L54) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [onPause()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L95) | Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời. |
| [onResume()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L98) | Tiếp tục công việc cần chạy khi màn hình trở lại foreground. |
| [onDispose()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L106) | Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn. |
| [stopWork()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L112) | Hủy các callback kiểm tra trạng thái và bộ đếm của giao dịch cũ. |
| [isCurrent()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L120) | Kiểm tra phiên bản yêu cầu vẫn khớp giao dịch/màn hình đang mở. |
| [isCurrentScreen()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L125) | Kiểm tra route, ID giao dịch và vòng đời trước khi cập nhật View. |
| [pending()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L131) | Kiểm tra giao dịch còn thuộc trạng thái đang chờ xử lý. |
| [requestStatus()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L134) | Lấy trạng thái thanh toán mới nhất khi màn hình/giao dịch còn hợp lệ. |
| [retryPayment()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L139) | Yêu cầu tạo hoặc thử lại giao dịch theo trạng thái hiện tại. |
| [request()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L146) | Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback. |
| [acceptPayment()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L182) | Nhận dữ liệu thanh toán mới, cập nhật giao diện hoặc chuyển sang kết quả. |
| [updateCountdown()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L229) | Tính thời gian còn lại của giao dịch và cập nhật nhãn/hành động khi hết hạn. |
| [timestamp()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L258) | Đọc thời điểm từ JSON thành giá trị dùng cho bộ đếm thanh toán. |
| [setBusy()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L267) | Đổi trạng thái bận để các nút không gửi thao tác trùng khi đang chờ API. |
| [showError()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L275) | Đưa lỗi vào vùng trạng thái và cập nhật thao tác phù hợp của giao diện. |
| [openGateway()](../app/src/main/java/com/example/nova/screens/PaymentDetailScreen.java#L281) | Kiểm tra URL thanh toán và mở trình xử lý liên kết của thiết bị. |

## PaymentFailedScreen

[Mở file](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java) — Hiển thị lỗi hoặc hết hạn và cho phép kiểm tra hoặc tạo lại giao dịch khi phù hợp.

| Phương thức | Chức năng |
| --- | --- |
| [PaymentFailedScreen()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L26) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L28) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L31) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [onPause()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L55) | Tạm dừng công việc theo vòng đời để tránh tiếp tục cập nhật màn hình đã rời. |
| [onResume()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L58) | Tiếp tục công việc cần chạy khi màn hình trở lại foreground. |
| [onDispose()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L65) | Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn. |
| [stopWork()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L71) | Hủy các callback kiểm tra trạng thái và bộ đếm của giao dịch cũ. |
| [isCurrent()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L78) | Kiểm tra phiên bản yêu cầu vẫn khớp giao dịch/màn hình đang mở. |
| [isCurrentScreen()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L83) | Kiểm tra route, ID giao dịch và vòng đời trước khi cập nhật View. |
| [requestStatus()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L89) | Lấy trạng thái thanh toán mới nhất khi màn hình/giao dịch còn hợp lệ. |
| [request()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L95) | Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback. |
| [updateButtons()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L146) | Bật/tắt các nút kiểm tra hoặc thử lại theo trạng thái thanh toán. |
| [showError()](../app/src/main/java/com/example/nova/screens/PaymentFailedScreen.java#L156) | Đưa lỗi vào vùng trạng thái và cập nhật thao tác phù hợp của giao diện. |

## PaymentMethodsScreen

[Mở file](../app/src/main/java/com/example/nova/screens/PaymentMethodsScreen.java) — Nạp lựa chọn từ API và lưu phương thức cho phiên đặt hàng.

| Phương thức | Chức năng |
| --- | --- |
| [PaymentMethodsScreen()](../app/src/main/java/com/example/nova/screens/PaymentMethodsScreen.java#L20) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/PaymentMethodsScreen.java#L22) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/PaymentMethodsScreen.java#L25) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## PaymentsScreen

[Mở file](../app/src/main/java/com/example/nova/screens/PaymentsScreen.java) — Nạp giao dịch, số tiền, trạng thái và mở chi tiết từng giao dịch.

| Phương thức | Chức năng |
| --- | --- |
| [PaymentsScreen()](../app/src/main/java/com/example/nova/screens/PaymentsScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/PaymentsScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/PaymentsScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## ProductFiltersScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ProductFiltersScreen.java) — Kiểm tra và áp dụng thương hiệu, khoảng giá và tùy chọn còn hàng khi chế độ hỗ trợ.

| Phương thức | Chức năng |
| --- | --- |
| [ProductFiltersScreen()](../app/src/main/java/com/example/nova/screens/ProductFiltersScreen.java#L20) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ProductFiltersScreen.java#L22) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ProductFiltersScreen.java#L25) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## ProductReviewsScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ProductReviewsScreen.java) — Tải nhận xét, lọc theo số sao và mở sửa đánh giá thuộc tài khoản hiện tại.

| Phương thức | Chức năng |
| --- | --- |
| [ProductReviewsScreen()](../app/src/main/java/com/example/nova/screens/ProductReviewsScreen.java#L21) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ProductReviewsScreen.java#L23) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ProductReviewsScreen.java#L26) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [drawReviews()](../app/src/main/java/com/example/nova/screens/ProductReviewsScreen.java#L49) | Dựng danh sách đánh giá với nội dung/số sao và thao tác thuộc tài khoản hiện tại. |

## ProductScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ProductScreen.java) — Hiện ảnh, mô tả, giá và biến thể. Bản đầy đủ có thao tác yêu thích, giỏ hàng, mua ngay và đánh giá.

| Phương thức | Chức năng |
| --- | --- |
| [ProductScreen()](../app/src/main/java/com/example/nova/screens/ProductScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ProductScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ProductScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [renderMidterm()](../app/src/main/java/com/example/nova/screens/ProductScreen.java#L126) | Dựng chi tiết sản phẩm bằng hợp đồng API GK và chỉ hiển thị thao tác được chế độ này hỗ trợ. |
| [text()](../app/src/main/java/com/example/nova/screens/ProductScreen.java#L178) | Chuyển dữ liệu thông số thành chuỗi hiển thị phù hợp trên màn chi tiết. |

## ProfileScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ProfileScreen.java) — Đọc và lưu họ tên, điện thoại, ngày sinh, giới tính; chọn ảnh đại diện từ thiết bị và tải lên backend GK. Hiển thị email và mở màn đổi email.

| Phương thức | Chức năng |
| --- | --- |
| [ProfileScreen()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L33) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L35) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L38) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [setActionsEnabled()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L148) | Bật/tắt thao tác hồ sơ trong lúc đang tải ảnh hoặc lưu dữ liệu. |
| [showAvatar()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L157) | Nạp URL avatar hoặc ảnh mặc định vào vùng ảnh hồ sơ. |
| [chooseBirthday()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L163) | Mở DatePickerDialog để người dùng chọn ngày sinh. |
| [showBirthday()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L179) | Hiển thị ngày sinh đã chọn theo định dạng giao diện. |
| [showGender()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L185) | Hiển thị nhãn giới tính theo giá trị đang chọn. |
| [optional()](../app/src/main/java/com/example/nova/screens/ProfileScreen.java#L195) | Đọc giá trị trường tùy chọn và chuẩn hóa trường hợp null/rỗng. |

## RegisterScreen

[Mở file](../app/src/main/java/com/example/nova/screens/RegisterScreen.java) — Kiểm tra biểu mẫu, gửi yêu cầu tạo tài khoản và mở bước xác thực email bằng OTP.

| Phương thức | Chức năng |
| --- | --- |
| [RegisterScreen()](../app/src/main/java/com/example/nova/screens/RegisterScreen.java#L25) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/RegisterScreen.java#L27) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/RegisterScreen.java#L30) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [passwordToggle()](../app/src/main/java/com/example/nova/screens/RegisterScreen.java#L81) | Gắn thao tác ẩn/hiện mật khẩu và giữ vị trí con trỏ nhập. |

## ResetPasswordScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ResetPasswordScreen.java) — Kiểm tra độ mạnh và xác nhận mật khẩu mới sau bước OTP.

| Phương thức | Chức năng |
| --- | --- |
| [ResetPasswordScreen()](../app/src/main/java/com/example/nova/screens/ResetPasswordScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ResetPasswordScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ResetPasswordScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [bindPasswordVisibility()](../app/src/main/java/com/example/nova/screens/ResetPasswordScreen.java#L77) | Đồng bộ nút ẩn/hiện với kiểu hiển thị ô mật khẩu. |

## ReturnDetailScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ReturnDetailScreen.java) — Hiển thị lý do, trạng thái, tiến trình và lối mở đơn liên quan.

| Phương thức | Chức năng |
| --- | --- |
| [ReturnDetailScreen()](../app/src/main/java/com/example/nova/screens/ReturnDetailScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ReturnDetailScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ReturnDetailScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## ReturnRequestScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ReturnRequestScreen.java) — Chọn hình thức, món, số lượng và lý do rồi gửi yêu cầu.

| Phương thức | Chức năng |
| --- | --- |
| [ReturnRequestScreen()](../app/src/main/java/com/example/nova/screens/ReturnRequestScreen.java#L26) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ReturnRequestScreen.java#L28) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ReturnRequestScreen.java#L31) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## ReturnsScreen

[Mở file](../app/src/main/java/com/example/nova/screens/ReturnsScreen.java) — Liệt kê yêu cầu đổi trả và các khoản hoàn tiền.

| Phương thức | Chức năng |
| --- | --- |
| [ReturnsScreen()](../app/src/main/java/com/example/nova/screens/ReturnsScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/ReturnsScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/ReturnsScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## SearchResultsScreen

[Mở file](../app/src/main/java/com/example/nova/screens/SearchResultsScreen.java) — Gọi phần xử lý lưới chung của CatalogScreen ở chế độ kết quả từ khóa.

| Phương thức | Chức năng |
| --- | --- |
| [SearchResultsScreen()](../app/src/main/java/com/example/nova/screens/SearchResultsScreen.java#L15) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/SearchResultsScreen.java#L17) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |

## SearchScreen

[Mở file](../app/src/main/java/com/example/nova/screens/SearchScreen.java) — Khi ô trống, hiện lịch sử và danh mục. Khi gõ, nạp từ khóa và sản phẩm gợi ý có ảnh, giá. Chạm sản phẩm để mở chi tiết hoặc chuyển sang kết quả đầy đủ.

| Phương thức | Chức năng |
| --- | --- |
| [SearchScreen()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L50) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L52) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L55) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |
| [beforeTextChanged()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L79) | Callback trước khi ô nhập đổi chữ; chỉ xử lý khi màn hình cần theo dõi trạng thái trước đó. |
| [onTextChanged()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L81) | Nhận thay đổi văn bản để cập nhật trạng thái hoặc lên lịch tải dữ liệu. |
| [afterTextChanged()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L85) | Đồng bộ nội dung vừa nhập vào trạng thái sau khi Editable thay đổi. |
| [onDispose()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L93) | Giải phóng công việc của màn hình cũ và làm mất hiệu lực các phản hồi đến muộn. |
| [updateQuery()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L99) | Chuẩn hóa từ khóa mới, hủy lịch cũ và lên lịch tải gợi ý. |
| [cancelScheduledSearch()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L121) | Gỡ callback tìm kiếm đã lên lịch để không gửi từ khóa cũ. |
| [loadSuggestions()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L129) | Gọi API gợi ý sau khoảng chờ nhập, chỉ nhận kết quả khớp từ khóa/phiên bản mới nhất. |
| [productsFrom()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L160) | Đọc danh sách sản phẩm từ dạng phản hồi API được hỗ trợ. |
| [showKeywords()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L181) | Dựng các gợi ý từ khóa để chạm và chuyển thành tìm kiếm. |
| [addKeyword()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L197) | Thêm một lựa chọn từ khóa cùng sự kiện mở kết quả. |
| [productRow()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L206) | Dựng dòng sản phẩm gợi ý gồm ảnh, tên và giá. |
| [loadHistory()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L230) | Lấy lịch sử tìm kiếm của tài khoản đang đăng nhập. |
| [loadCategories()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L271) | Lấy các lựa chọn danh mục để lọc/phân hướng tìm kiếm. |
| [textRow()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L295) | Dựng dòng chữ có thể chạm cho lịch sử/gợi ý tìm kiếm. |
| [showResults()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L310) | Ghi từ khóa hiện tại và mở route kết quả tìm kiếm. |
| [openCategory()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L320) | Ghi ngữ cảnh danh mục, bỏ bộ lọc không còn phù hợp rồi mở danh sách sản phẩm. |
| [setCatalogQuery()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L327) | Đặt từ khóa/danh mục vào bộ lọc danh sách và xóa điều kiện cũ không còn phù hợp. |
| [saveHistory()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L337) | Gửi từ khóa đã tìm vào lịch sử của tài khoản khi có phiên. |
| [hideKeyboard()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L345) | Ẩn bàn phím sau khi xác nhận tìm kiếm hoặc chọn một kết quả. |
| [rows()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L351) | Chuẩn hóa dữ liệu danh sách phục vụ vùng gợi ý/lịch sử. |
| [visible()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L354) | Đặt View hiện hoặc ẩn theo việc có nội dung cần hiển thị. |
| [status()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L359) | Cập nhật dòng trạng thái tải/rỗng/lỗi của vùng tìm kiếm. |
| [clean()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L366) | Cắt và chuẩn hóa khoảng trắng của từ khóa người dùng nhập. |
| [key()](../app/src/main/java/com/example/nova/screens/SearchScreen.java#L371) | Tạo khóa so sánh từ khóa/danh mục để nhận diện kết quả tìm kiếm còn phù hợp. |

## SelectAddressScreen

[Mở file](../app/src/main/java/com/example/nova/screens/SelectAddressScreen.java) — Ghi nhận địa chỉ cho đơn đang đặt rồi quay lại thanh toán.

| Phương thức | Chức năng |
| --- | --- |
| [SelectAddressScreen()](../app/src/main/java/com/example/nova/screens/SelectAddressScreen.java#L20) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/SelectAddressScreen.java#L22) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/SelectAddressScreen.java#L26) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## SupportScreen

[Mở file](../app/src/main/java/com/example/nova/screens/SupportScreen.java) — Nạp bài hướng dẫn, mở nội dung và điều hướng tới chat.

| Phương thức | Chức năng |
| --- | --- |
| [SupportScreen()](../app/src/main/java/com/example/nova/screens/SupportScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/SupportScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/SupportScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## TiersScreen

[Mở file](../app/src/main/java/com/example/nova/screens/TiersScreen.java) — Hiển thị ngưỡng điểm và quyền lợi theo từng hạng.

| Phương thức | Chức năng |
| --- | --- |
| [TiersScreen()](../app/src/main/java/com/example/nova/screens/TiersScreen.java#L29) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/TiersScreen.java#L31) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/TiersScreen.java#L35) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

## VouchersScreen

[Mở file](../app/src/main/java/com/example/nova/screens/VouchersScreen.java) — Nạp voucher, hiển thị điều kiện sử dụng và chọn mã cho phiên đặt hàng.

| Phương thức | Chức năng |
| --- | --- |
| [VouchersScreen()](../app/src/main/java/com/example/nova/screens/VouchersScreen.java#L20) | Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng. |
| [layoutId()](../app/src/main/java/com/example/nova/screens/VouchersScreen.java#L22) | Trả ID layout XML mà màn hình sẽ nạp; Java dùng ID này để tìm các View cần gắn dữ liệu. |
| [render()](../app/src/main/java/com/example/nova/screens/VouchersScreen.java#L26) | Nạp bố cục, gắn dữ liệu và sự kiện cho màn hình; các yêu cầu API bên trong hoàn tất qua callback. |

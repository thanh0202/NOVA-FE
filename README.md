# Android NOVA2

Ứng dụng Android native Java/XML, package `com.example.nova`.

## Mở dự án

Mở thư mục `NOVA_FE_GITHUB` trong Android Studio, Sync Gradle, chọn máy ảo/điện thoại và Run module `app`.
Android Studio tạo lại `local.properties` theo SDK trên máy; file này không đưa lên Git.

## Kết nối backend được giữ nguyên

API mặc định: `https://nova-uit.japaneast.cloudapp.azure.com/api`, chế độ GK.
URL và cờ chế độ nằm trong `app/build.gradle.kts`; mã gọi API và xử lý dữ liệu được giữ nguyên.
Backend đọc/ghi CSDL Neon và tải ảnh lên Cloudinary. Android không chứa mật khẩu CSDL hay API Secret Cloudinary.

Build dùng API mặc định:

```powershell
.\gradlew.bat assembleDebug
```

Khi chạy backend trên cùng máy với máy giả lập Android:

```powershell
.\gradlew.bat assembleDebug '-PnovaApiUrl=http://10.0.2.2:8080/api' '-PnovaMidtermApi=true'
```

Với URL HTTPS riêng, đặt `-PnovaApiUrl=https://api.example.com/api` và chọn `novaMidtermApi` phù hợp.
APK được tạo ở `app/build/outputs/apk/debug/app-debug.apk`; các bản APK không nằm trong Git.

## Mã nguồn và công cụ

- `app/src/main/java`: màn hình, điều hướng, gọi API, chọn/tải ảnh.
- `app/src/main/res`, `assets`: bố cục, icon, ảnh, font và giấy phép.
- `app/src/debug`: tài nguyên Preview/debug.
- `gradle`, `gradlew`, `gradlew.bat`: cấu hình và công cụ build; phải giữ `gradle-wrapper.jar`.

Trang chủ giữ khối sản phẩm theo đủ sáu danh mục chính và chức năng Xem thêm mở trang sản phẩm đúng danh mục/thương hiệu.
Đổi ảnh đại diện: Tài khoản → Thông tin cá nhân → Đổi ảnh đại diện.
Backend được tách thành gói NOVA_BE_GITHUB. FE này tiếp tục gọi URL API hiện có; không cần đặt thư mục BE cạnh FE.

## Tài liệu riêng của FE

- [Cây thư mục có giải thích từng file](CAU-TRUC-FE.md)
- [Chức năng từng file](docs/CHUC-NANG-TUNG-FILE.md)
- [Tra cứu phương thức](docs/TRA-CUU-HAM.md)
- [Giải thích code](docs/GIAI-THICH-CODE.md)

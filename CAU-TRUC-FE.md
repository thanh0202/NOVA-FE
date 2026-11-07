# Cây thư mục FE NOVA — giải thích từng file

Đây là gói NOVA_FE_GITHUB độc lập. Mỗi dòng file và thư mục đều có phần `# chức năng` ngay phía sau.

**571 file · 61 Java · 412 XML.**

Mở chính thư mục này trong Android Studio. FE giữ 45 XML màn hình và các XML icon/nền/Preview đi kèm; URL API giữ nguyên.

```text
NOVA_FE_GITHUB/  # Frontend Android native Java/XML.
├── app/  # Module ứng dụng Android.
│   ├── src/  # Mã nguồn và tài nguyên của dự án.
│   │   ├── debug/  # Preview và cấu hình biến thể debug.
│   │   │   └── res/  # Tài nguyên Android hoặc tài nguyên được đóng gói.
│   │   │       ├── drawable/  # Icon vector, ảnh, nền và viền giao diện.
│   │   │       │   └── preview_payment_qr.xml  # Tài nguyên debug/Preview preview_payment_qr; hỗ trợ xem bố cục hoặc cấu hình thử nghiệm theo biến thể debug.
│   │   │       ├── drawable-nodpi/  # Drawable theo mật độ/cấu hình thiết bị.
│   │   │       │   └── preview_home_banner.jpg  # Ảnh bitmap preview_home_banner cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │   │       ├── layout/  # Bố cục XML của màn hình và các thành phần.
│   │   │       │   ├── preview_account_account_body.xml  # Hồ sơ, thẻ thành viên, trạng thái đơn và nhóm tiện ích mẫu.
│   │   │       │   ├── preview_address_office.xml  # Thẻ địa chỉ văn phòng trong Sổ địa chỉ.
│   │   │       │   ├── preview_address_select.xml  # Thẻ địa chỉ có nút chọn giao hàng.
│   │   │       │   ├── preview_address_select_office.xml  # Địa chỉ văn phòng có nút chọn giao hàng.
│   │   │       │   ├── preview_addresses_addresses_list.xml  # Hai thẻ trong danh sách địa chỉ đã lưu.
│   │   │       │   ├── preview_cancel_order_result_screen_body.xml  # Thông tin hoàn tiền trong kết quả hủy đơn.
│   │   │       │   ├── preview_cart_cart_items.xml  # Danh sách mặt hàng mẫu trong giỏ.
│   │   │       │   ├── preview_cart_cart_summary.xml  # Khối tạm tính và tổng tiền giỏ hàng.
│   │   │       │   ├── preview_cart_second.xml  # Dòng sản phẩm thứ hai của giỏ hàng.
│   │   │       │   ├── preview_catalog_catalog_categories.xml  # Dải ô danh mục con ở danh sách sản phẩm.
│   │   │       │   ├── preview_catalog_catalog_grid.xml  # Lưới sản phẩm theo danh mục.
│   │   │       │   ├── preview_categories_categories_detail.xml  # Nội dung bên phải gồm danh mục con, thương hiệu và khoảng giá.
│   │   │       │   ├── preview_categories_categories_sidebar.xml  # Cột danh mục cha bên trái.
│   │   │       │   ├── preview_chat_screen_body.xml  # Mẫu tin nhắn trong cuộc trò chuyện.
│   │   │       │   ├── preview_checkout_checkout_summary.xml  # Khối tổng tiền của bước đặt hàng.
│   │   │       │   ├── preview_favorites_screen_body.xml  # Các tab và lưới sản phẩm yêu thích.
│   │   │       │   ├── preview_flash_sale_flash_detail.xml  # Banner, bộ đếm và sản phẩm ưu đãi.
│   │   │       │   ├── preview_flash_sale_flash_tabs.xml  # Các khung giờ Flash Sale.
│   │   │       │   ├── preview_home_category_products.xml  # Tài nguyên debug/Preview preview_home_category_products; hỗ trợ xem bố cục hoặc cấu hình thử nghiệm theo biến thể debug.
│   │   │       │   ├── preview_home_home_banner_controls.xml  # Dãy chỉ báo trang bên dưới banner.
│   │   │       │   ├── preview_home_home_category_columns.xml  # Dải hai hàng ô danh mục.
│   │   │       │   ├── preview_home_home_discovery.xml  # Khu bán chạy và nổi bật.
│   │   │       │   ├── preview_home_home_flash.xml  # Khối ưu đãi chớp nhoáng đang hoạt động.
│   │   │       │   ├── preview_home_home_new_arrivals.xml  # Lưới hàng mới về.
│   │   │       │   ├── preview_membership_membership_history.xml  # Các dòng lịch sử tích điểm.
│   │   │       │   ├── preview_membership_tiers_screen_body.xml  # Các thẻ quyền lợi theo hạng.
│   │   │       │   ├── preview_navigation_0.xml  # Thanh điều hướng mẫu không đánh dấu tab.
│   │   │       │   ├── preview_navigation_1.xml  # Thanh điều hướng mẫu đánh dấu Trang chủ.
│   │   │       │   ├── preview_navigation_2.xml  # Thanh điều hướng mẫu đánh dấu Danh mục.
│   │   │       │   ├── preview_navigation_3.xml  # Thanh điều hướng mẫu đánh dấu Giỏ hàng.
│   │   │       │   ├── preview_navigation_4.xml  # Thanh điều hướng mẫu đánh dấu Tài khoản.
│   │   │       │   ├── preview_notifications_screen_body.xml  # Danh sách thông báo mẫu.
│   │   │       │   ├── preview_order_detail_screen_body.xml  # Các khối trạng thái, thông tin nhận hàng, sản phẩm, thanh toán.
│   │   │       │   ├── preview_order_tracking_screen_body.xml  # Dòng thời gian giao hàng.
│   │   │       │   ├── preview_orders_orders_list.xml  # Thẻ đơn hàng mẫu.
│   │   │       │   ├── preview_orders_orders_tabs.xml  # Các lựa chọn lọc trạng thái đơn.
│   │   │       │   ├── preview_otp_otp_boxes.xml  # Dãy sáu ô ký tự OTP minh họa.
│   │   │       │   ├── preview_payment_history_screen_body.xml  # Thẻ lịch sử giao dịch.
│   │   │       │   ├── preview_payment_methods_method_options.xml  # Danh sách cách thanh toán mẫu.
│   │   │       │   ├── preview_product_product_gallery_thumbnails.xml  # Ảnh thu nhỏ bên dưới ảnh sản phẩm.
│   │   │       │   ├── preview_product_product_variants.xml  # Các tùy chọn phiên bản sản phẩm.
│   │   │       │   ├── preview_product_reviews_screen_body.xml  # Các nhận xét và điểm sao mẫu.
│   │   │       │   ├── preview_product_second.xml  # Thẻ iPhone dùng làm sản phẩm mẫu thứ hai.
│   │   │       │   ├── preview_product_second_audio.xml  # Thẻ AirPods dùng trong lưới âm thanh.
│   │   │       │   ├── preview_product_specifications.xml  # Thông số kỹ thuật mẫu trong chi tiết sản phẩm.
│   │   │       │   ├── preview_return_detail_screen_body.xml  # Trạng thái và tiến trình đổi trả.
│   │   │       │   ├── preview_return_request_return_request_items.xml  # Món hàng mẫu để chọn khi yêu cầu đổi trả.
│   │   │       │   ├── preview_returns_screen_body.xml  # Các thẻ yêu cầu đổi trả.
│   │   │       │   ├── preview_screen_account.xml  # Khung xem trước của màn Tài khoản, nạp screen_account.xml.
│   │   │       │   ├── preview_screen_add_address.xml  # Khung xem trước của màn Thêm địa chỉ, nạp screen_add_address.xml.
│   │   │       │   ├── preview_screen_address_form.xml  # Khung xem trước của màn Sửa địa chỉ, nạp screen_address_form.xml.
│   │   │       │   ├── preview_screen_addresses.xml  # Khung xem trước của màn Sổ địa chỉ, nạp screen_addresses.xml.
│   │   │       │   ├── preview_screen_cancel_order.xml  # Khung xem trước của màn Hủy đơn hàng, nạp screen_cancel_order.xml.
│   │   │       │   ├── preview_screen_cancel_order_result.xml  # Khung xem trước của màn Kết quả hủy đơn, nạp screen_cancel_order_result.xml.
│   │   │       │   ├── preview_screen_cart.xml  # Khung xem trước của màn Giỏ hàng, nạp screen_cart.xml.
│   │   │       │   ├── preview_screen_catalog.xml  # Khung xem trước của màn Sản phẩm theo danh mục, nạp screen_catalog.xml.
│   │   │       │   ├── preview_screen_categories.xml  # Khung xem trước của màn Danh mục, nạp screen_categories.xml.
│   │   │       │   ├── preview_screen_change_email.xml  # Khung xem trước của màn Đổi email, nạp screen_change_email.xml.
│   │   │       │   ├── preview_screen_change_password.xml  # Khung xem trước của màn Đổi mật khẩu, nạp screen_change_password.xml.
│   │   │       │   ├── preview_screen_chat.xml  # Khung xem trước của màn Chat với NOVA, nạp screen_chat.xml.
│   │   │       │   ├── preview_screen_checkout.xml  # Khung xem trước của màn Thanh toán giỏ hàng, nạp screen_checkout.xml.
│   │   │       │   ├── preview_screen_favorites.xml  # Khung xem trước của màn Yêu thích, nạp screen_favorites.xml.
│   │   │       │   ├── preview_screen_flash_sale.xml  # Khung xem trước của màn Flash Sale, nạp screen_flash_sale.xml.
│   │   │       │   ├── preview_screen_forgot_password.xml  # Khung xem trước của màn Quên mật khẩu, nạp screen_forgot_password.xml.
│   │   │       │   ├── preview_screen_home.xml  # Khung xem trước của màn Trang chủ, nạp screen_home.xml.
│   │   │       │   ├── preview_screen_login.xml  # Khung xem trước của màn Đăng nhập, nạp screen_login.xml.
│   │   │       │   ├── preview_screen_membership.xml  # Khung xem trước của màn Thành viên NOVA, nạp screen_membership.xml.
│   │   │       │   ├── preview_screen_membership_tiers.xml  # Khung xem trước của màn Quyền lợi thành viên, nạp screen_membership_tiers.xml.
│   │   │       │   ├── preview_screen_notifications.xml  # Khung xem trước của màn Thông báo, nạp screen_notifications.xml.
│   │   │       │   ├── preview_screen_order_detail.xml  # Khung xem trước của màn Chi tiết đơn hàng, nạp screen_order_detail.xml.
│   │   │       │   ├── preview_screen_order_success.xml  # Khung xem trước của màn Đặt hàng thành công, nạp screen_order_success.xml.
│   │   │       │   ├── preview_screen_order_tracking.xml  # Khung xem trước của màn Theo dõi giao hàng, nạp screen_order_tracking.xml.
│   │   │       │   ├── preview_screen_orders.xml  # Khung xem trước của màn Đơn hàng của tôi, nạp screen_orders.xml.
│   │   │       │   ├── preview_screen_otp.xml  # Khung xem trước của màn Xác thực OTP, nạp screen_otp.xml.
│   │   │       │   ├── preview_screen_payment.xml  # Khung xem trước của màn Thanh toán đơn hàng, nạp screen_payment.xml.
│   │   │       │   ├── preview_screen_payment_failed.xml  # Khung xem trước của màn Thanh toán không thành công, nạp screen_payment_failed.xml.
│   │   │       │   ├── preview_screen_payment_history.xml  # Khung xem trước của màn Lịch sử giao dịch, nạp screen_payment_history.xml.
│   │   │       │   ├── preview_screen_payment_methods.xml  # Khung xem trước của màn Phương thức thanh toán, nạp screen_payment_methods.xml.
│   │   │       │   ├── preview_screen_product.xml  # Khung xem trước của màn Chi tiết sản phẩm, nạp screen_product.xml.
│   │   │       │   ├── preview_screen_product_filters.xml  # Khung xem trước của màn Bộ lọc, nạp screen_product_filters.xml.
│   │   │       │   ├── preview_screen_product_reviews.xml  # Khung xem trước của màn Đánh giá sản phẩm, nạp screen_product_reviews.xml.
│   │   │       │   ├── preview_screen_profile.xml  # Khung xem trước của màn Thông tin cá nhân, nạp screen_profile.xml.
│   │   │       │   ├── preview_screen_register.xml  # Khung xem trước của màn Đăng ký, nạp screen_register.xml.
│   │   │       │   ├── preview_screen_reset_password.xml  # Khung xem trước của màn Tạo mật khẩu mới, nạp screen_reset_password.xml.
│   │   │       │   ├── preview_screen_return_detail.xml  # Khung xem trước của màn Chi tiết yêu cầu, nạp screen_return_detail.xml.
│   │   │       │   ├── preview_screen_return_request.xml  # Khung xem trước của màn Yêu cầu đổi trả, nạp screen_return_request.xml.
│   │   │       │   ├── preview_screen_returns.xml  # Khung xem trước của màn Đổi trả và hoàn tiền, nạp screen_returns.xml.
│   │   │       │   ├── preview_screen_search.xml  # Khung xem trước của màn Tìm kiếm, nạp screen_search.xml.
│   │   │       │   ├── preview_screen_search_results.xml  # Khung xem trước của màn Kết quả tìm kiếm, nạp screen_search_results.xml.
│   │   │       │   ├── preview_screen_select_address.xml  # Khung xem trước của màn Chọn địa chỉ giao hàng, nạp screen_select_address.xml.
│   │   │       │   ├── preview_screen_support.xml  # Khung xem trước của màn Trung tâm hỗ trợ, nạp screen_support.xml.
│   │   │       │   ├── preview_screen_vouchers.xml  # Khung xem trước của màn Ưu đãi của bạn, nạp screen_vouchers.xml.
│   │   │       │   ├── preview_search_keywords.xml  # Các từ khóa gợi ý khi đang gõ.
│   │   │       │   ├── preview_search_product_second.xml  # Dòng sản phẩm minh họa thứ hai.
│   │   │       │   ├── preview_search_results_catalog_grid.xml  # Lưới minh họa cho kết quả từ khóa.
│   │   │       │   ├── preview_search_screen_body.xml  # Danh sách sản phẩm gợi ý có ảnh và giá.
│   │   │       │   ├── preview_select_address_addresses_list.xml  # Danh sách địa chỉ với nút chọn để giao hàng.
│   │   │       │   ├── preview_support_support_articles.xml  # Các bài hỗ trợ và câu hỏi mẫu.
│   │   │       │   ├── preview_voucher_shipping.xml  # Thẻ voucher miễn phí giao hàng.
│   │   │       │   └── preview_vouchers_screen_body.xml  # Danh sách voucher minh họa.
│   │   │       └── xml/  # Cấu hình Android dạng XML.
│   │   │           └── network_security_config.xml  # Cấu hình debug cho phép HTTP cục bộ với 10.0.2.2, localhost hoặc 127.0.0.1 để kiểm thử.
│   │   └── main/  # Phần được dùng khi build/chạy ứng dụng.
│   │       ├── assets/  # Tài nguyên được đọc trực tiếp qua AssetManager.
│   │       │   └── fonts/  # Font Inter và giấy phép.
│   │       │       ├── InterVariable.ttf  # Font Inter biến thiên do Ui.java nạp để hiển thị chữ.
│   │       │       └── OFL.txt  # Giấy phép phân phối của font Inter.
│   │       ├── java/  # Mã nguồn Java theo package.
│   │       │   └── com/  # Nhóm thư mục/package com.
│   │       │       └── example/  # Nhóm thư mục/package example.
│   │       │           └── nova/  # Nhóm thư mục/package nova.
│   │       │               ├── screens/  # Các lớp xử lý giao diện theo route Android.
│   │       │               │   ├── AccountScreen.java  # Hiển thị hồ sơ tóm tắt, đăng xuất, lối vào địa chỉ, đơn hàng, thành viên và các tiện ích.
│   │       │               │   ├── AddAddressScreen.java  # Chọn chế độ tạo mới và dùng xử lý biểu mẫu chung của AddressFormScreen.
│   │       │               │   ├── AddressesScreen.java  # Nạp địa chỉ đã lưu, hiển thị mặc định và xử lý mở thêm, sửa hoặc xóa.
│   │       │               │   ├── AddressFormScreen.java  # Đọc thông tin hiện tại, kiểm tra biểu mẫu và lưu thay đổi. Chứa phần xử lý chung cho cả thêm và sửa.
│   │       │               │   ├── AdminImagesScreen.java  # Giao diện quản lý ảnh cho ADMIN: tìm sản phẩm, xem số chỗ còn lại, chọn nhiều ảnh và gửi multipart đến API quản trị.
│   │       │               │   ├── BaseScreen.java  # Lớp nền cho các màn hình: dùng chung MainActivity, AppState, ApiClient, Ui, Images và các hàm dựng/gắn giao diện.
│   │       │               │   ├── CancelOrderResultScreen.java  # Nạp trạng thái sau khi hủy và thông tin hoàn tiền nếu có.
│   │       │               │   ├── CancelOrderScreen.java  # Kiểm tra trạng thái, nhận lý do và gửi yêu cầu hủy.
│   │       │               │   ├── CartScreen.java  # Nạp giỏ, cập nhật số lượng, xóa món hoặc xóa giỏ và chuyển sang đặt hàng.
│   │       │               │   ├── CatalogScreen.java  # Nạp lưới sản phẩm theo danh mục, hỗ trợ chuyển nhóm, từ khóa, sắp xếp và lọc.
│   │       │               │   ├── CategoryScreen.java  # Chọn danh mục cha, xem nhóm con, thương hiệu, phân khúc giá và ô sản phẩm nổi bật.
│   │       │               │   ├── ChangeEmailScreen.java  # Nhập địa chỉ mới và mật khẩu hiện tại, gửi yêu cầu rồi mở bước xác thực OTP.
│   │       │               │   ├── ChangePasswordScreen.java  # Kiểm tra mật khẩu hiện tại, xác nhận mật khẩu mới rồi gửi cập nhật.
│   │       │               │   ├── ChatScreen.java  # Nạp lịch sử, gửi tin nhắn và làm mới hội thoại.
│   │       │               │   ├── CheckoutScreen.java  # Ghép địa chỉ, phương thức, voucher và ghi chú. Yêu cầu báo giá trước khi gửi đơn.
│   │       │               │   ├── FavoritesScreen.java  # Nạp lưới các sản phẩm khách đã lưu và cho phép mở chi tiết.
│   │       │               │   ├── FlashSaleScreen.java  # Nạp khung giờ, giá, số lượng còn lại, đếm ngược và xử lý nhắc ưu đãi.
│   │       │               │   ├── ForgotPasswordScreen.java  # Gửi email để yêu cầu mã khôi phục.
│   │       │               │   ├── HomeCategoryBrowser.java  # Khối Sản phẩm theo danh mục dưới phần khám phá: hiển thị mọi danh mục gốc, lọc nhóm con/thương hiệu, mở trang danh sách qua Xem thêm.
│   │       │               │   ├── HomeScreen.java  # Nạp banner, danh mục hai hàng, hàng mới về, bán chạy và nổi bật. Hiện Flash Sale khi có chương trình hoạt động từ API tương ứng.
│   │       │               │   ├── LoginScreen.java  # Nhập email và mật khẩu, ghi nhớ phiên, xử lý tài khoản chưa xác thực và điều hướng sang đăng ký hoặc quên mật khẩu.
│   │       │               │   ├── MembershipScreen.java  # Nạp hạng, điểm tích lũy, tiến độ lên hạng và lịch sử điểm.
│   │       │               │   ├── NotificationsScreen.java  # Nạp danh sách, cập nhật đã đọc và mở đích liên quan khi có dữ liệu điều hướng.
│   │       │               │   ├── OrderDetailScreen.java  # Hiển thị thông tin đơn và mở các thao tác phù hợp với trạng thái như thanh toán, theo dõi, hủy, đổi trả hoặc viết đánh giá.
│   │       │               │   ├── OrdersScreen.java  # Nạp danh sách đơn và lọc theo trạng thái.
│   │       │               │   ├── OrderSuccessScreen.java  # Hiển thị mã đơn, số tiền và phương thức. Mở chi tiết hoặc quay lại mua sắm.
│   │       │               │   ├── OrderTrackingScreen.java  # Hiển thị mã vận đơn, địa chỉ và lịch sử trạng thái vận chuyển.
│   │       │               │   ├── OtpScreen.java  # Xác nhận mã cho đăng ký, khôi phục mật khẩu hoặc đổi email. Xử lý gửi lại mã theo luồng.
│   │       │               │   ├── PaymentDetailScreen.java  # Hiển thị số tiền, hướng dẫn, QR hoặc cổng thanh toán và theo dõi trạng thái giao dịch.
│   │       │               │   ├── PaymentFailedScreen.java  # Hiển thị lỗi hoặc hết hạn và cho phép kiểm tra hoặc tạo lại giao dịch khi phù hợp.
│   │       │               │   ├── PaymentMethodsScreen.java  # Nạp lựa chọn từ API và lưu phương thức cho phiên đặt hàng.
│   │       │               │   ├── PaymentsScreen.java  # Nạp giao dịch, số tiền, trạng thái và mở chi tiết từng giao dịch.
│   │       │               │   ├── ProductFiltersScreen.java  # Kiểm tra và áp dụng thương hiệu, khoảng giá và tùy chọn còn hàng khi chế độ hỗ trợ.
│   │       │               │   ├── ProductReviewsScreen.java  # Tải nhận xét, lọc theo số sao và mở sửa đánh giá thuộc tài khoản hiện tại.
│   │       │               │   ├── ProductScreen.java  # Hiện ảnh, mô tả, giá và biến thể. Bản đầy đủ có thao tác yêu thích, giỏ hàng, mua ngay và đánh giá.
│   │       │               │   ├── ProfileScreen.java  # Đọc và lưu họ tên, điện thoại, ngày sinh, giới tính; chọn ảnh đại diện từ thiết bị và tải lên backend GK. Hiển thị email và mở màn đổi email.
│   │       │               │   ├── RegisterScreen.java  # Kiểm tra biểu mẫu, gửi yêu cầu tạo tài khoản và mở bước xác thực email bằng OTP.
│   │       │               │   ├── ResetPasswordScreen.java  # Kiểm tra độ mạnh và xác nhận mật khẩu mới sau bước OTP.
│   │       │               │   ├── ReturnDetailScreen.java  # Hiển thị lý do, trạng thái, tiến trình và lối mở đơn liên quan.
│   │       │               │   ├── ReturnRequestScreen.java  # Chọn hình thức, món, số lượng và lý do rồi gửi yêu cầu.
│   │       │               │   ├── ReturnsScreen.java  # Liệt kê yêu cầu đổi trả và các khoản hoàn tiền.
│   │       │               │   ├── SearchResultsScreen.java  # Gọi phần xử lý lưới chung của CatalogScreen ở chế độ kết quả từ khóa.
│   │       │               │   ├── SearchScreen.java  # Khi ô trống, hiện lịch sử và danh mục. Khi gõ, nạp từ khóa và sản phẩm gợi ý có ảnh, giá. Chạm sản phẩm để mở chi tiết hoặc chuyển sang kết quả đầy đủ.
│   │       │               │   ├── SelectAddressScreen.java  # Ghi nhận địa chỉ cho đơn đang đặt rồi quay lại thanh toán.
│   │       │               │   ├── SupportScreen.java  # Nạp bài hướng dẫn, mở nội dung và điều hướng tới chat.
│   │       │               │   ├── TiersScreen.java  # Hiển thị ngưỡng điểm và quyền lợi theo từng hạng.
│   │       │               │   └── VouchersScreen.java  # Nạp voucher, hiển thị điều kiện sử dụng và chọn mã cho phiên đặt hàng.
│   │       │               ├── ApiClient.java  # Gửi HTTP JSON hoặc multipart trên luồng nền, đính kèm Bearer token và trả kết quả về luồng giao diện Android.
│   │       │               ├── AppState.java  # Trạng thái dùng chung của FE: route hiện tại, lịch sử Back, bộ lọc, danh mục Trang chủ, người dùng và thông tin đặt hàng tạm.
│   │       │               ├── CategoryUi.java  # Gắn ảnh, tên và trạng thái chọn của một danh mục vào layout item_home_category.
│   │       │               ├── HomeBanner.java  # Quản lý danh sách banner: tải ảnh, xử lý vuốt/chạm, chấm chỉ báo và tự chuyển khi màn hình đang hoạt động.
│   │       │               ├── ImagePicker.java  # Mở bộ chọn ảnh hệ thống, nhận URI của đúng các ảnh người dùng đã chọn và chuyển lại cho màn hình yêu cầu.
│   │       │               ├── Images.java  # Tải và giải mã ảnh ở luồng nền, lưu bộ đệm bitmap, kiểm tra View còn chờ đúng URL trước khi gán ảnh.
│   │       │               ├── MainActivity.java  # Điểm điều phối Android: khôi phục trạng thái, quản lý phiên, chọn lớp Screen theo route và dựng thanh điều hướng.
│   │       │               ├── PasswordRules.java  # Quy tắc kiểm tra mật khẩu mới: độ dài và các nhóm ký tự bắt buộc; FE và BE dùng cùng yêu cầu.
│   │       │               ├── ProductGallery.java  # Quản lý ảnh chi tiết sản phẩm, chọn thumbnail và đổi ảnh tương ứng biến thể/SKU.
│   │       │               ├── QrCode.java  # Dùng ZXing mã hóa chuỗi dữ liệu thanh toán thành bitmap QR để hiển thị trên màn hình.
│   │       │               ├── SaleReminderReceiver.java  # Đặt/hủy nhắc Flash Sale bằng AlarmManager và nhận sự kiện để hiển thị thông báo Android.
│   │       │               ├── SecureSession.java  # Lưu token đã mã hóa bằng AES/GCM; khóa mã hóa nằm trong Android Keystore, URL API lấy từ BuildConfig.
│   │       │               ├── Ui.java  # Bộ hàm dựng giao diện native dùng chung: font, màu, dp, hàng/cột, nút, thẻ, chip và định dạng tiền Việt.
│   │       │               └── UploadImage.java  # Đọc URI ảnh đã chọn, giới hạn dung lượng/kích thước, xoay EXIF, thu nhỏ và nén JPEG trước khi gửi lên BE.
│   │       ├── res/  # Tài nguyên Android hoặc tài nguyên được đóng gói.
│   │       │   ├── drawable/  # Icon vector, ảnh, nền và viền giao diện.
│   │       │   │   ├── admin_imggroup.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup10.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup11.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup12.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup13.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup14.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup2.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup3.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup4.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup5.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup6.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup7.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup8.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── admin_imggroup9.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imganhsang.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgarrowright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgarrowupright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgbell.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgbiutng.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgbiutng1.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgbiutng2.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgbiutng3.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgduhiuthnghiu.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgheadphones.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgheart.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgheart1.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imglaptop.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgnhsnphm.png  # Ảnh bitmap home_imgnhsnphm cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── home_imgnhsnphm1.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgnhsnphm2.png  # Ảnh bitmap home_imgnhsnphm2 cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── home_imgnhsnphm3.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgnhsnphm4.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgnhsnphm5.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgnhsnphm6.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgnhsnphm7.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgnhsnphm8.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgplus.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgqudo.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgscanline.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgsearch.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgsmartphone.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgthongbaomi.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── home_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── home_imgwatch.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── ic_category_accessory.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_category_audio.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_category_generic.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── ic_category_laptop.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_category_phone.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_category_tablet.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_category_watch.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_nova_bell.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_nova_chevron_down.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── ic_nova_form_back.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── ic_nova_heart.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── ic_nova_search.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_1039_imgarrowleft.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgaudiolines.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgaudiolines1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgbatteryfull.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgbatteryfull1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgbluetooth.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgbluetooth1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgbngmau.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgchbaonh.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgchevronright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgellipsis.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imghaoquang.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgheart.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgnhdanhgia.jpg  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgnhdanhgia1.jpg  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgnhsnphm.png  # Ảnh bitmap node_6_1039_imgnhsnphm cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── node_6_1039_imgplus.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgshare2.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgsquarepen.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgthemvaogi.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgthumbsup.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgtruck.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1039_imgzap.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgbiutng.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgbiutng1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgbiutng2.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgbiutng3.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgbiutng4.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgdangmban.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgduhiunova.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imghanhdng.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm1.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm10.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm11.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm2.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm3.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm4.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm5.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm6.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm7.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm8.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnhsnphm9.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgnutgt.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgquayli.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgqudo.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgshieldcheck.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1602_imgvector.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgarrowleft.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgarrowright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imghalo.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imginfo.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgiosbatteryfull.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgiossignal.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgioswifisignal.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgkeyround.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgmail.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgorbitaccent.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_165_imgshieldcheck.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgarrowleft.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgarrowright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgduhiunova.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgminus.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgnhsnphm.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgnhsnphm1.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgplus.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgshieldcheck.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgticketpercent.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgtrash2.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_1952_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_19_imgarrowright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_19_imgbrandsymbol.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_19_imgcheck.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_19_imgeyeoff.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_19_imgiosbatteryfull.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_19_imgiossignal.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_19_imgioswifisignal.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_19_imglockkeyhole.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_19_imgmail.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_19_imgorbitalring.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_19_imgorbitalring1.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_19_imgshieldcheck.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_19_imgtechnologytile.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2044_imgarrowleft.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2044_imgbanknote.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgchevronright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgchevronup.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgcreditcard.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgduhiunova.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imglockkeyhole.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgmappin.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgochn.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgochn1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgtruck.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2044_imgwallet.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgarrowupright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgchevronright.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imghouse.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgiosbatteryfull.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgiossignal.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgioswifisignal.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imglayoutgrid.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgluminousorb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgmappin.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgmemberportrait.jpg  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_2783_imgnfc.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgnotifications.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgorbitaldetail.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgpackage.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgshieldcheck.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgshoppingbag.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgticketpercent.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgtruck.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imguserround.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_2783_imgwallet.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_279_imgbackarrow.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_279_imgbattery.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_279_imgcellularsignal.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_279_imglock.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_279_imglockkeyhole.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_279_imgpasswordvisibility.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_279_imgsecurityshield.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_279_imgsecurityshield1.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── node_6_279_imgwifi.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imganhsang.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgbiutng.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgbiutng1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgbiutng2.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgbiutng3.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgdimsang.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgduhiunova.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imggem.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgheadset.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgnhdidin.jpg  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgquayli.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgqudo.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgqudodim.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgsparkles.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgticketpercent.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgtinddim.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgtruck.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_3237_imgzap.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imganhsang.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgarrowright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgarrowupright.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgbell.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgbiutng.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgbiutng1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgbiutng2.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgbiutng3.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgduhiuthnghiu.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgheadphones.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgheart.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgheart1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imglaptop.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm1.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm2.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm3.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm4.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm5.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm6.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm7.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgnhsnphm8.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgplus.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgqudo.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgscanline.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgsearch.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgsmartphone.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgthongbaomi.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_339_imgwatch.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgarrowleft.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgbiutng.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgbiutng1.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgbiutng2.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgbiutng3.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin1.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin10.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin11.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin12.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin13.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin14.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin15.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin2.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin3.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin4.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin5.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin6.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin7.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin8.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgnhdidin9.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── node_6_560_imgtrngthaithitb.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── nova_account_avatar.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── nova_auth_hero.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_banner.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_blue_card.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_blue_price.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_card.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_category.xml  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── nova_category_card.xml  # Hiệu ứng phản hồi khi chạm lên một thành phần giao diện.
│   │       │   │   ├── nova_category_photo.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_category_sidebar.xml  # Các trạng thái drawable được chọn theo trạng thái View.
│   │       │   │   ├── nova_category_tint.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_field.xml  # Các trạng thái drawable được chọn theo trạng thái View.
│   │       │   │   ├── nova_hero_gradient.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_hero_pill.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_home_action.xml  # Hiệu ứng phản hồi khi chạm lên một thành phần giao diện.
│   │       │   │   ├── nova_home_category_surface.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_home_category_touch.xml  # Hiệu ứng phản hồi khi chạm lên một thành phần giao diện.
│   │       │   │   ├── nova_home_search.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_icon_action.xml  # Hiệu ứng phản hồi khi chạm lên một thành phần giao diện.
│   │       │   │   ├── nova_logo.xml  # Icon vector: pathData và màu dựng hình độc lập độ phân giải.
│   │       │   │   ├── nova_nav_badge.xml  # item_bottom_navigation.xml
│   │       │   │   ├── nova_nav_selected.xml  # MainActivity.java
│   │       │   │   ├── nova_primary_button.xml  # Hiệu ứng phản hồi khi chạm lên một thành phần giao diện.
│   │       │   │   ├── nova_product_surface.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_recovery_back.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_recovery_symbol.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_search.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── nova_secondary_button.xml  # Hiệu ứng phản hồi khi chạm lên một thành phần giao diện.
│   │       │   │   ├── nova_segment_surface.xml  # Nền hình học: màu, viền, bo góc hoặc kích thước của thành phần giao diện.
│   │       │   │   ├── product_1.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── product_2.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── product_3.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── product_4.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── product_5.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── product_6.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── product_7.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   └── product_8.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   ├── drawable-nodpi/  # Drawable theo mật độ/cấu hình thiết bị.
│   │       │   │   ├── account_member_glow.png  # Ảnh bitmap account_member_glow cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── category_photo_accessory.png  # Ảnh bitmap category_photo_accessory cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── category_photo_audio.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── category_photo_laptop.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── category_photo_phone.png  # Ảnh bitmap category_photo_phone cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── category_photo_tablet.jpg  # Ảnh bitmap category_photo_tablet cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── category_photo_watch.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   ├── figma_badge_check.png  # Ảnh bitmap figma_badge_check cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── figma_calendar_days.png  # Ảnh bitmap figma_calendar_days cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── figma_smartphone.png  # Ảnh bitmap figma_smartphone cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── figma_user_round.png  # Ảnh bitmap figma_user_round cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   │   ├── nova01_hero_glow.png  # Chưa thấy tham chiếu trực tiếp theo tên.
│   │       │   │   └── nova01_otp_mail.png  # Ảnh bitmap nova01_otp_mail cho giao diện/tài nguyên thiết kế; các URL ảnh sản phẩm thực tế vẫn được API trả về.
│   │       │   ├── drawable-xxxhdpi/  # Drawable theo mật độ/cấu hình thiết bị.
│   │       │   │   ├── nova_nav_account.png  # Icon Tài khoản từ Figma trong ô 22dp, xuất mật độ 4×.
│   │       │   │   ├── nova_nav_cart.png  # Icon Giỏ hàng từ Figma trong ô 22dp, xuất mật độ 4×.
│   │       │   │   ├── nova_nav_categories.png  # Icon Danh mục từ Figma trong ô 22dp, xuất mật độ 4×.
│   │       │   │   └── nova_nav_home.png  # Icon Trang chủ từ Figma trong ô 22dp, xuất mật độ 4×.
│   │       │   ├── layout/  # Bố cục XML của màn hình và các thành phần.
│   │       │   │   ├── activity_main.xml  # Khung chứa tiêu đề, nội dung cuộn và vùng chân trang do MainActivity điều phối.
│   │       │   │   ├── component_account_identity.xml  # Khối ảnh đại diện mặc định, tên, email và nút sửa hồ sơ trên màn Tài khoản. Dùng chung cho giao diện khi chạy và Preview.
│   │       │   │   ├── component_address_form.xml  # Các trường nhập và nút lưu dùng chung cho thêm và sửa địa chỉ.
│   │       │   │   ├── component_auth_brand.xml  # Logo, câu giới thiệu và nút Khám phá trên màn đăng nhập.
│   │       │   │   ├── component_auth_form.xml  # Các trường email, mật khẩu và thao tác đăng nhập, đăng ký, quên mật khẩu.
│   │       │   │   ├── component_bottom_navigation.xml  # Khung thanh điều hướng 72dp, có bốn vị trí tab và đường phân cách phía trên.
│   │       │   │   ├── component_cart_item.xml  # Một dòng trong giỏ hàng, gồm ảnh, tên, giá, số lượng và nút xóa.
│   │       │   │   ├── component_flash_banner.xml  # Tiêu đề chương trình Flash Sale và bộ đếm thời gian.
│   │       │   │   ├── component_home_category_products.xml  # Bố cục component_home_category_products với thẻ gốc LinearLayout; Java tìm các View qua ID để gắn dữ liệu và sự kiện.
│   │       │   │   ├── component_product_actions.xml  # Thanh nút Thêm vào giỏ và Mua ngay của màn chi tiết sản phẩm khi dùng bản đầy đủ.
│   │       │   │   ├── dialog_otp.xml  # Mẫu hộp thoại OTP do hàm dùng chung trong BaseScreen nạp. Luồng hiện tại mở màn screen_otp.xml.
│   │       │   │   ├── item_address.xml  # Thẻ địa chỉ dùng chung cho Sổ địa chỉ và Chọn địa chỉ giao hàng.
│   │       │   │   ├── item_bottom_navigation.xml  # Bố cục một tab gồm vùng icon, nhãn, nền khi chọn và badge giỏ hàng.
│   │       │   │   ├── item_category_product.xml  # Ô sản phẩm nổi bật trong màn Danh mục, có ảnh và tên rút gọn.
│   │       │   │   ├── item_category_sidebar.xml  # Một ô danh mục cha trong cột bên trái của màn Danh mục.
│   │       │   │   ├── item_home_category.xml  # Ô ảnh và tên danh mục trên Trang chủ.
│   │       │   │   ├── item_product_card.xml  # Thẻ lưới sản phẩm dùng trong Trang chủ, danh mục, tìm kiếm và yêu thích.
│   │       │   │   ├── item_search_product.xml  # Một dòng sản phẩm gợi ý có ảnh, tên, giá hiện tại và giá cũ.
│   │       │   │   ├── item_voucher.xml  # Một thẻ voucher gồm mã, mô tả, đơn tối thiểu, hạn dùng và thao tác chọn.
│   │       │   │   ├── screen_account.xml  # Hiển thị hồ sơ tóm tắt, đăng xuất, lối vào địa chỉ, đơn hàng, thành viên và các tiện ích.
│   │       │   │   ├── screen_add_address.xml  # Chọn chế độ tạo mới và dùng xử lý biểu mẫu chung của AddressFormScreen.
│   │       │   │   ├── screen_address_form.xml  # Đọc thông tin hiện tại, kiểm tra biểu mẫu và lưu thay đổi. Chứa phần xử lý chung cho cả thêm và sửa.
│   │       │   │   ├── screen_addresses.xml  # Nạp địa chỉ đã lưu, hiển thị mặc định và xử lý mở thêm, sửa hoặc xóa.
│   │       │   │   ├── screen_admin_images.xml  # Bố cục screen_admin_images với thẻ gốc LinearLayout; Java tìm các View qua ID để gắn dữ liệu và sự kiện.
│   │       │   │   ├── screen_cancel_order.xml  # Kiểm tra trạng thái, nhận lý do và gửi yêu cầu hủy.
│   │       │   │   ├── screen_cancel_order_result.xml  # Nạp trạng thái sau khi hủy và thông tin hoàn tiền nếu có.
│   │       │   │   ├── screen_cart.xml  # Nạp giỏ, cập nhật số lượng, xóa món hoặc xóa giỏ và chuyển sang đặt hàng.
│   │       │   │   ├── screen_catalog.xml  # Nạp lưới sản phẩm theo danh mục, hỗ trợ chuyển nhóm, từ khóa, sắp xếp và lọc.
│   │       │   │   ├── screen_categories.xml  # Chọn danh mục cha, xem nhóm con, thương hiệu, phân khúc giá và ô sản phẩm nổi bật.
│   │       │   │   ├── screen_change_email.xml  # Nhập địa chỉ mới và mật khẩu hiện tại, gửi yêu cầu rồi mở bước xác thực OTP.
│   │       │   │   ├── screen_change_password.xml  # Kiểm tra mật khẩu hiện tại, xác nhận mật khẩu mới rồi gửi cập nhật.
│   │       │   │   ├── screen_chat.xml  # Nạp lịch sử, gửi tin nhắn và làm mới hội thoại.
│   │       │   │   ├── screen_checkout.xml  # Ghép địa chỉ, phương thức, voucher và ghi chú. Yêu cầu báo giá trước khi gửi đơn.
│   │       │   │   ├── screen_favorites.xml  # Nạp lưới các sản phẩm khách đã lưu và cho phép mở chi tiết.
│   │       │   │   ├── screen_flash_sale.xml  # Nạp khung giờ, giá, số lượng còn lại, đếm ngược và xử lý nhắc ưu đãi.
│   │       │   │   ├── screen_forgot_password.xml  # Gửi email để yêu cầu mã khôi phục.
│   │       │   │   ├── screen_home.xml  # Nạp banner, danh mục hai hàng, hàng mới về, bán chạy và nổi bật. Hiện Flash Sale khi có chương trình hoạt động từ API tương ứng.
│   │       │   │   ├── screen_login.xml  # Nhập email và mật khẩu, ghi nhớ phiên, xử lý tài khoản chưa xác thực và điều hướng sang đăng ký hoặc quên mật khẩu.
│   │       │   │   ├── screen_membership.xml  # Nạp hạng, điểm tích lũy, tiến độ lên hạng và lịch sử điểm.
│   │       │   │   ├── screen_membership_tiers.xml  # Hiển thị ngưỡng điểm và quyền lợi theo từng hạng.
│   │       │   │   ├── screen_notifications.xml  # Nạp danh sách, cập nhật đã đọc và mở đích liên quan khi có dữ liệu điều hướng.
│   │       │   │   ├── screen_order_detail.xml  # Hiển thị thông tin đơn và mở các thao tác phù hợp với trạng thái như thanh toán, theo dõi, hủy, đổi trả hoặc viết đánh giá.
│   │       │   │   ├── screen_order_success.xml  # Hiển thị mã đơn, số tiền và phương thức. Mở chi tiết hoặc quay lại mua sắm.
│   │       │   │   ├── screen_order_tracking.xml  # Hiển thị mã vận đơn, địa chỉ và lịch sử trạng thái vận chuyển.
│   │       │   │   ├── screen_orders.xml  # Nạp danh sách đơn và lọc theo trạng thái.
│   │       │   │   ├── screen_otp.xml  # Xác nhận mã cho đăng ký, khôi phục mật khẩu hoặc đổi email. Xử lý gửi lại mã theo luồng.
│   │       │   │   ├── screen_payment.xml  # Hiển thị số tiền, hướng dẫn, QR hoặc cổng thanh toán và theo dõi trạng thái giao dịch.
│   │       │   │   ├── screen_payment_failed.xml  # Hiển thị lỗi hoặc hết hạn và cho phép kiểm tra hoặc tạo lại giao dịch khi phù hợp.
│   │       │   │   ├── screen_payment_history.xml  # Nạp giao dịch, số tiền, trạng thái và mở chi tiết từng giao dịch.
│   │       │   │   ├── screen_payment_methods.xml  # Nạp lựa chọn từ API và lưu phương thức cho phiên đặt hàng.
│   │       │   │   ├── screen_product.xml  # Hiện ảnh, mô tả, giá và biến thể. Bản đầy đủ có thao tác yêu thích, giỏ hàng, mua ngay và đánh giá.
│   │       │   │   ├── screen_product_filters.xml  # Kiểm tra và áp dụng thương hiệu, khoảng giá và tùy chọn còn hàng khi chế độ hỗ trợ.
│   │       │   │   ├── screen_product_reviews.xml  # Tải nhận xét, lọc theo số sao và mở sửa đánh giá thuộc tài khoản hiện tại.
│   │       │   │   ├── screen_profile.xml  # Đọc và lưu họ tên, điện thoại, ngày sinh, giới tính; chọn ảnh đại diện từ thiết bị và tải lên backend GK. Hiển thị email và mở màn đổi email.
│   │       │   │   ├── screen_register.xml  # Kiểm tra biểu mẫu, gửi yêu cầu tạo tài khoản và mở bước xác thực email bằng OTP.
│   │       │   │   ├── screen_reset_password.xml  # Kiểm tra độ mạnh và xác nhận mật khẩu mới sau bước OTP.
│   │       │   │   ├── screen_return_detail.xml  # Hiển thị lý do, trạng thái, tiến trình và lối mở đơn liên quan.
│   │       │   │   ├── screen_return_request.xml  # Chọn hình thức, món, số lượng và lý do rồi gửi yêu cầu.
│   │       │   │   ├── screen_returns.xml  # Liệt kê yêu cầu đổi trả và các khoản hoàn tiền.
│   │       │   │   ├── screen_search.xml  # Khi ô trống, hiện lịch sử và danh mục. Khi gõ, nạp từ khóa và sản phẩm gợi ý có ảnh, giá. Chạm sản phẩm để mở chi tiết hoặc chuyển sang kết quả đầy đủ.
│   │       │   │   ├── screen_search_results.xml  # Gọi phần xử lý lưới chung của CatalogScreen ở chế độ kết quả từ khóa.
│   │       │   │   ├── screen_select_address.xml  # Ghi nhận địa chỉ cho đơn đang đặt rồi quay lại thanh toán.
│   │       │   │   ├── screen_support.xml  # Nạp bài hướng dẫn, mở nội dung và điều hướng tới chat.
│   │       │   │   └── screen_vouchers.xml  # Nạp voucher, hiển thị điều kiện sử dụng và chọn mã cho phiên đặt hàng.
│   │       │   ├── values/  # Chữ, màu, kích thước, style và ID tài nguyên.
│   │       │   │   ├── account_security_strings.xml  # Nhãn và thông báo cho thông tin cá nhân và thao tác bảo mật tài khoản.
│   │       │   │   ├── additional_screen_strings.xml  # Văn bản cho các màn bổ sung như địa chỉ, thanh toán và đơn hàng.
│   │       │   │   ├── card_strings.xml  # Nhãn dùng chung của thẻ địa chỉ và voucher.
│   │       │   │   ├── category_ids.xml  # Khai báo ID tài nguyên dùng cho phần giao diện danh mục.
│   │       │   │   ├── colors.xml  # Bảng màu dùng chung của NOVA.
│   │       │   │   ├── dimens.xml  # Khoảng cách, lề và bán kính bo góc dùng chung.
│   │       │   │   ├── recovery_strings.xml  # Văn bản cho khôi phục tài khoản và tạo mật khẩu mới.
│   │       │   │   ├── screen_strings.xml  # Nhãn, tiêu đề và thông báo của các màn hình.
│   │       │   │   ├── screen_styles.xml  # Style chữ, nút, ô nhập và lựa chọn dùng chung trong XML.
│   │       │   │   ├── search_strings.xml  # Nhãn thanh tìm kiếm, lịch sử, gợi ý và các nút liên quan.
│   │       │   │   └── styles.xml  # Theme gốc cho ứng dụng.
│   │       │   ├── values-v27/  # Tài nguyên thay thế theo phiên bản/cấu hình Android.
│   │       │   │   └── styles.xml  # Biến thể theme cho thiết bị từ API 27.
│   │       │   ├── values-v35/  # Tài nguyên thay thế theo phiên bản/cấu hình Android.
│   │       │   │   └── styles.xml  # Biến thể theme từ API 35 với cấu hình thanh điều hướng và edge-to-edge.
│   │       │   └── xml/  # Cấu hình Android dạng XML.
│   │       │       └── network_security_config.xml  # Chặn kết nối HTTP thuần trong cấu hình chính và tin cậy chứng chỉ hệ thống.
│   │       └── AndroidManifest.xml  # Khai báo MainActivity khởi động, quyền mạng, thông báo, bộ nhận nhắc ưu đãi và theme.
│   ├── .gitignore  # Quy tắc bỏ qua tệp sinh ra ở module app.
│   └── build.gradle.kts  # Cấu hình Android, BuildConfig, URL API, chế độ GK hoặc đầy đủ, Java, APK và thư viện QR.
├── docs/  # Tài liệu riêng của gói.
│   ├── CHUC-NANG-TUNG-FILE.md  # Bảng chức năng toàn bộ file trong gói FE.
│   ├── GIAI-THICH-CODE.md  # Giải thích luồng xử lý và cách đọc mã FE.
│   └── TRA-CUU-HAM.md  # Tra cứu phương thức Java và liên kết tới đúng dòng mã nguồn FE.
├── gradle/  # Cấu hình và công cụ build Gradle.
│   ├── wrapper/  # Gradle Wrapper để build mà không cài Gradle riêng.
│   │   ├── gradle-wrapper.jar  # Chương trình khởi động Gradle Wrapper.
│   │   └── gradle-wrapper.properties  # Chỉ định bản phân phối Gradle mà Wrapper sử dụng.
│   ├── gradle-daemon-jvm.properties  # Tiêu chí JVM để Gradle chọn runtime cho daemon.
│   └── libs.versions.toml  # Danh mục phiên bản và alias plugin hoặc thư viện.
├── .gitignore  # Loại khỏi Git các tệp IDE, kết quả build và cấu hình riêng của máy.
├── build.gradle.kts  # Khai báo plugin ở cấp dự án để module app sử dụng.
├── CAU-TRUC-FE.md  # Cây thư mục FE đầy đủ; mỗi file có chú thích chức năng ngay trên dòng tương ứng.
├── gradle.properties  # Cấu hình Gradle và các cờ build chung.
├── gradlew  # Chạy Gradle Wrapper trên shell Unix.
├── gradlew.bat  # Chạy Gradle Wrapper trên Windows.
├── README.md  # Hướng dẫn mở/đọc gói FE độc lập và các liên kết tài liệu của gói.
└── settings.gradle.kts  # Khai báo tên dự án, kho plugin và thư viện, module app.
```

[Bảng chức năng từng file](docs/CHUC-NANG-TUNG-FILE.md) · [Tra cứu phương thức](docs/TRA-CUU-HAM.md) · [Giải thích code](docs/GIAI-THICH-CODE.md)

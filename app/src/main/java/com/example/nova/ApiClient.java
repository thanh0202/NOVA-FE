package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Gửi HTTP JSON hoặc multipart trên luồng nền, đính kèm Bearer token và trả kết quả về luồng giao diện Android.
 */
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.*;

/** REST transport. All network and JSON decoding happens off the main thread. */
public final class ApiClient {
  public interface Callback {
    // Callback nhận dữ liệu, thông báo lỗi và HTTP status sau khi yêu cầu API hoàn tất.
    void complete(Object data, String error, int status);
  }

  private final SecureSession session;
  // HTTP và đọc JSON chạy ngoài main thread để thao tác chạm/cuộn không bị chặn.
  private final ExecutorService executor = Executors.newFixedThreadPool(4);
  // Chỉ callback gắn dữ liệu vào View mới được chuyển về main thread.
  private final Handler main = new Handler(Looper.getMainLooper());
  private volatile boolean closed;

  // Nhận SecureSession để lấy API base URL và token cho các yêu cầu HTTP.
  public ApiClient(SecureSession session) {
    this.session = session;
  }

  // Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback.
  public void request(String method, String path, JSONObject body, Callback callback) {
    request(method, path, body, null, callback);
  }

  /** Revoke an unsupported login without putting its token into the app's session storage. */
  // Gọi đăng xuất bằng token được cung cấp mà không ghi token đó vào kho phiên hiện tại.
  public void revokeToken(String token) {
    if (token == null || token.isEmpty()) return;
    request("POST", "/auth/logout", null, token, (data, error, status) -> {});
  }

  // Thực hiện yêu cầu HTTP, xử lý mã trạng thái, dữ liệu trả về và vòng đời callback.
  private void request(String method, String path, JSONObject body, String explicitToken, Callback callback) {
    final String token = explicitToken == null ? session.token() : explicitToken;
    if (closed) return;
    executor.execute(
        () -> {
          Object result = null;
          String error = null;
          int status = 0;
          HttpURLConnection connection = null;
          try {
            connection = (HttpURLConnection) new URL(session.baseUrl() + path).openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Accept", "application/json");
            // Empty POST actions (logout, reminders, read notifications) also use JSON.
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // Token xác thực danh tính phía BE; app không gửi mật khẩu CSDL hay khóa Cloudinary.
            if (!token.isEmpty()) connection.setRequestProperty("Authorization", "Bearer " + token);
            if (body != null) {
              connection.setDoOutput(true);
              try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body.toString().getBytes(StandardCharsets.UTF_8));
              }
            }
            status = connection.getResponseCode();
            InputStream stream =
                status < 400 ? connection.getInputStream() : connection.getErrorStream();
            String raw = "";
            if (stream != null)
              try (InputStream input = stream) {
                raw = new String(readBytes(input), StandardCharsets.UTF_8);
              }
            if (!raw.trim().isEmpty()) {
              try {
                result = new JSONTokener(raw).nextValue();
              } catch (JSONException ignored) {
              }
            }
            if (status >= 400) {
              error =
                  result instanceof JSONObject
                      ? ((JSONObject) result).optString("message", "Không thể hoàn tất yêu cầu.")
                      : "Máy chủ trả về lỗi " + status;
              if (status == 401 && explicitToken == null && token.equals(session.token())) session.clear();
            }
          } catch (Exception e) {
            error = "Không thể kết nối NOVA. Vui lòng kiểm tra kết nối mạng và thử lại sau.";
          } finally {
            if (connection != null) connection.disconnect();
          }
          final Object value = result;
          final String issue = error;
          final int code = status;
          main.post(() -> {
            if (closed) return;
            if (issue == null && explicitToken == null && !token.isEmpty() && !token.equals(session.token()))
              callback.complete(null, "Phiên đăng nhập đã thay đổi. Vui lòng mở lại màn hình.", 401);
            else callback.complete(value, issue, code);
          });
        });
  }

  /** Decode/resize each chosen photo off-main, then stream authenticated multipart to NOVA. */
  // Đọc/nén ảnh trên luồng nền và truyền multipart có Bearer token; trả kết quả trên main thread.
  public void uploadImages(Context context, List<Uri> images, String path, String field,
                           int maxDimension, Callback callback) {
    if (closed) return;
    final Context app = context.getApplicationContext();
    final List<Uri> selected = new ArrayList<>(images);
    final String token = session.token();
    executor.execute(() -> {
      Object result = null;
      String error = null;
      int status = 0;
      HttpURLConnection connection = null;
      try {
        if (selected.isEmpty() || selected.size() > 10 || !field.matches("[A-Za-z]+"))
          throw new IllegalArgumentException("Chọn từ 1 đến 10 ảnh để tải lên.");
        if (token.isEmpty()) throw new IllegalArgumentException("Vui lòng đăng nhập lại để tải ảnh.");
        // Boundary phân tách các phần của multipart; trường file/files phải khớp @RequestPart phía BE.
        String boundary = "nova-" + UUID.randomUUID();
        connection = (HttpURLConnection) new URL(session.baseUrl() + path).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(120000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setChunkedStreamingMode(16384);
        connection.setDoOutput(true);
        try (OutputStream output = connection.getOutputStream()) {
          int index = 1;
          for (Uri uri : selected) {
            byte[] photo;
            // Nén từng ảnh đã được chọn; nội dung gốc trên thiết bị không bị ghi đè.
            try { photo = UploadImage.jpeg(app, uri, maxDimension); }
            catch (IOException invalid) {
              throw new IllegalArgumentException("Không đọc được ảnh thứ " + index
                  + ". Hãy chọn lại ảnh JPG, PNG hoặc WebP nhỏ hơn 30 MB.");
            }
            if (Thread.currentThread().isInterrupted()) throw new InterruptedIOException();
            write(output, "--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + field
                + "\"; filename=\"photo-" + index++ + ".jpg\"\r\nContent-Type: image/jpeg\r\n\r\n");
            output.write(photo);
            write(output, "\r\n");
          }
          write(output, "--" + boundary + "--\r\n");
        }
        status = connection.getResponseCode();
        InputStream stream = status < 400 ? connection.getInputStream() : connection.getErrorStream();
        String raw = "";
        if (stream != null) try (InputStream input = stream) {
          raw = new String(readBytes(input), StandardCharsets.UTF_8);
        }
        if (!raw.trim().isEmpty()) try { result = new JSONTokener(raw).nextValue(); }
        catch (JSONException ignored) { }
        if (status >= 400) {
          error = result instanceof JSONObject
              ? ((JSONObject) result).optString("message", "Chưa tải được ảnh. Vui lòng thử lại.")
              : "Chưa tải được ảnh. Máy chủ trả về lỗi " + status;
          if (status == 401 && token.equals(session.token())) session.clear();
        } else if (!(result instanceof JSONObject)) {
          error = "Máy chủ chưa trả về kết quả tải ảnh. Vui lòng làm mới để kiểm tra.";
        }
      } catch (IllegalArgumentException invalid) { error = invalid.getMessage(); }
      catch (Exception unavailable) {
        error = "Chưa nhận được kết quả tải ảnh. Hãy kiểm tra mạng và làm mới trước khi thử lại.";
      } finally { if (connection != null) connection.disconnect(); }
      final Object value = result;
      final String issue = error;
      final int code = status;
      main.post(() -> {
        if (closed) return;
        if (issue == null && !token.equals(session.token()))
          callback.complete(null, "Phiên đăng nhập đã thay đổi. Vui lòng mở lại màn hình.", 401);
        else callback.complete(value, issue, code);
      });
    });
  }

  // Ghi chuỗi UTF-8 vào luồng xuất của phần thân yêu cầu.
  private static void write(OutputStream output, String value) throws IOException {
    output.write(value.getBytes(StandardCharsets.UTF_8));
  }

  // Gửi GET qua lớp API dùng chung rồi xử lý dữ liệu trong callback.
  public void get(String path, Callback callback) {
    request("GET", path, null, callback);
  }

  // Đánh dấu đã đóng, hủy callback/công việc nền và giải phóng tài nguyên liên quan.
  public void close() {
    closed = true;
    executor.shutdownNow();
    main.removeCallbacksAndMessages(null);
  }

  // Tạo JSONObject từ các cặp khóa/giá trị dùng cho body gửi API.
  public static JSONObject json(Object... pairs) {
    JSONObject value = new JSONObject();
    try {
      for (int i = 0; i < pairs.length; i += 2) value.put(String.valueOf(pairs[i]), pairs[i + 1]);
    } catch (JSONException e) {
      throw new IllegalArgumentException(e);
    }
    return value;
  }

  // Đọc luồng phản hồi thành byte[] với giới hạn kích thước để tránh dùng bộ nhớ quá mức.
  private static byte[] readBytes(InputStream input) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[8192];
    int n;
    while ((n = input.read(buffer)) != -1) {
      if (out.size() + n > 2 * 1024 * 1024) throw new IOException("Response is too large");
      out.write(buffer, 0, n);
    }
    return out.toByteArray();
  }

  // Mã hóa giá trị query bằng UTF-8 trước khi ghép URL để dấu cách/ký tự tiếng Việt không làm sai tham số.
  public static String encode(String text) {
    try {
      return URLEncoder.encode(text, "UTF-8");
    } catch (UnsupportedEncodingException impossible) {
      throw new AssertionError(impossible);
    }
  }
}

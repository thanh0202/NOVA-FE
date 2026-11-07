package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Tải và giải mã ảnh ở luồng nền, lưu bộ đệm bitmap, kiểm tra View còn chờ đúng URL trước khi gán ảnh.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Images {
  private final Context context;
  private final SecureSession session;
  private final ExecutorService workers = Executors.newFixedThreadPool(3);
  // Ảnh đầu trang được tải riêng để tránh chờ ảnh sản phẩm từ CDN.
  private final ExecutorService artworkWorkers = Executors.newFixedThreadPool(2);
  private final Handler main = new Handler(Looper.getMainLooper());
  private volatile boolean closed;
  private final LruCache<String, Bitmap> cache =
      new LruCache<>(24 * 1024 * 1024) {
        // Tính dung lượng bitmap khi quản lý bộ đệm ảnh LRU.
        protected int sizeOf(String k, Bitmap b) {
          return b.getByteCount();
        }
      };

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public Images(Context context, SecureSession session) {
    this.context = context;
    this.session = session;
  }

  // Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID.
  public void product(ImageView view, String path, long productId) {
    // IDs belong to the current database; an old bundled photo with the same ID may show another model.
    view.setImageResource(R.drawable.nova_logo);
    load(view, path, workers);
  }

  /** Ảnh danh mục và banner đi theo URL do API cung cấp. */
  // Tải ảnh hoặc xử lý đối tượng danh mục theo đầu vào.
  public void category(ImageView view, String path) {
    load(view, path, artworkWorkers);
  }

  // Kiểm tra bộ đệm rồi tải ảnh khi chưa có, gắn tag để tránh ảnh cũ bị gán nhầm View.
  private void load(ImageView view, String path, ExecutorService executor) {
    String resolved = resolve(path);
    String key = resolved + "#nova-hd";
    view.setTag(key);
    if (resolved.isEmpty() || closed) return;
    Bitmap cached = cache.get(key);
    if (cached != null) {
      view.setImageBitmap(cached);
      return;
    }
    executor.execute(
        () -> {
          String candidate = resolved.startsWith(
              "https://cdn2.cellphones.com.vn/200x/media/catalog/product/")
              ? resolved.replace("/200x/media/catalog/product/", "/600x/media/catalog/product/")
              : resolved;
          Bitmap bitmap = fetch(candidate);
          if (bitmap == null && !candidate.equals(resolved) && !closed) bitmap = fetch(resolved);
          if (bitmap != null && !closed) {
            Bitmap photo = bitmap;
            cache.put(key, photo);
            main.post(() -> {
              if (!closed && key.equals(view.getTag())) view.setImageBitmap(photo);
            });
          }
        });
  }

  /** Fetch larger product photos, preserving the original URL as the failure fallback. */
  // Tải byte ảnh qua HTTP và giải mã bitmap theo kích thước mục tiêu.
  private Bitmap fetch(String resolved) {
          HttpURLConnection c = null;
          try {
            c = (HttpURLConnection) new URL(resolved).openConnection();
            c.setConnectTimeout(8000);
            c.setReadTimeout(10000);
            // Recheck each redirect so an HTTPS image cannot redirect to a local/file URL.
            c.setInstanceFollowRedirects(false);
            for (int redirects = 0; redirects < 4; redirects++) {
              int status = c.getResponseCode();
              if (status < 300 || status >= 400) break;
              String next = resolve(new URL(c.getURL(), c.getHeaderField("Location")).toString());
              c.disconnect();
              if (next.isEmpty()) return null;
              c = (HttpURLConnection) new URL(next).openConnection();
              c.setConnectTimeout(8000); c.setReadTimeout(10000);
              c.setInstanceFollowRedirects(false);
            }
            if (c.getResponseCode() != 200 || c.getContentLengthLong() > 10 * 1024 * 1024) return null;
            try (InputStream stream = c.getInputStream()) {
              ByteArrayOutputStream bytes = new ByteArrayOutputStream();
              byte[] buffer = new byte[8192];
              int size;
              while ((size = stream.read(buffer)) != -1) {
                if (closed || bytes.size() + size > 10 * 1024 * 1024) return null;
                bytes.write(buffer, 0, size);
              }
              byte[] raw = bytes.toByteArray();
              BitmapFactory.Options bounds = new BitmapFactory.Options();
              bounds.inJustDecodeBounds = true;
              BitmapFactory.decodeByteArray(raw, 0, raw.length, bounds);
              BitmapFactory.Options options = new BitmapFactory.Options();
              options.inPreferredConfig = Bitmap.Config.ARGB_8888;
              options.inScaled = false;
              options.inSampleSize = 1;
              while (bounds.outWidth / options.inSampleSize > 1600
                  || bounds.outHeight / options.inSampleSize > 1600) options.inSampleSize *= 2;
              return BitmapFactory.decodeByteArray(raw, 0, raw.length, options);
            }
          } catch (Exception ignored) {
          } finally {
            if (c != null) c.disconnect();
          }
    return null;
  }

  /** /media/... resolves against the server origin, including when API has a path prefix. */
  // Chuẩn hóa URL ảnh từ API thành địa chỉ có thể tải.
  private String resolve(String path) {
    if (path == null || path.trim().isEmpty()) return "";
    try {
      URL base = new URL(session.baseUrl());
      URL target = new URL(base, path.trim());
      if (target.getUserInfo() != null) return "";
      boolean sameOrigin = target.getProtocol().equals(base.getProtocol())
          && target.getHost().equalsIgnoreCase(base.getHost())
          && target.getPort() == base.getPort();
      if (!target.getProtocol().equals("https")
          && !(sameOrigin && target.getProtocol().equals("http"))) return "";
      return target.toString();
    } catch (Exception invalid) { return ""; }
  }

  // Đánh dấu đã đóng, hủy callback/công việc nền và giải phóng tài nguyên liên quan.
  public void close() {
    closed = true;
    workers.shutdownNow();
    artworkWorkers.shutdownNow();
    main.removeCallbacksAndMessages(null);
    cache.evictAll();
  }
}

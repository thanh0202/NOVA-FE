package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Đọc URI ảnh đã chọn, giới hạn dung lượng/kích thước, xoay EXIF, thu nhỏ và nén JPEG trước khi gửi lên BE.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Bounded, background-only decoding. Original camera photos never enter the request unchanged. */
final class UploadImage {
  private static final long SOURCE_LIMIT = 30L * 1024 * 1024;
  private static final int UPLOAD_LIMIT = 5 * 1024 * 1024;

  // Đọc URI vào tệp tạm có giới hạn, xoay EXIF, thu nhỏ và nén JPEG; thu hồi bitmap/tệp tạm trong finally.
  static byte[] jpeg(Context context, Uri uri, int maxDimension) throws IOException {
    File original = File.createTempFile("nova-photo-", ".tmp", context.getCacheDir());
    Bitmap decoded = null, oriented = null, scaled = null, opaque = null;
    try {
      try (InputStream input = context.getContentResolver().openInputStream(uri);
           FileOutputStream output = new FileOutputStream(original)) {
        if (input == null) throw new IOException("Không đọc được ảnh đã chọn.");
        byte[] buffer = new byte[16384];
        long total = 0;
        int count;
        while ((count = input.read(buffer)) != -1) {
          if (Thread.currentThread().isInterrupted()) throw new IOException("Đã dừng tải ảnh.");
          total += count;
          if (total > SOURCE_LIMIT) throw new IOException("Ảnh gốc cần nhỏ hơn 30 MB.");
          output.write(buffer, 0, count);
        }
      }
      BitmapFactory.Options bounds = new BitmapFactory.Options();
      bounds.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(original.getAbsolutePath(), bounds);
      if (bounds.outWidth < 1 || bounds.outHeight < 1
          || bounds.outWidth > 60000 || bounds.outHeight > 60000
          || (long) bounds.outWidth * bounds.outHeight > 120_000_000L) {
        throw new IOException("Ảnh không hợp lệ hoặc quá lớn. Hãy chọn ảnh JPG, PNG hoặc WebP.");
      }
      int edge = Math.max(256, Math.min(1600, maxDimension));
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      options.inScaled = false;
      options.inSampleSize = 1;
      while (Math.max(bounds.outWidth, bounds.outHeight) / options.inSampleSize > edge * 3 / 2)
        options.inSampleSize *= 2;
      decoded = BitmapFactory.decodeFile(original.getAbsolutePath(), options);
      if (decoded == null) throw new IOException("Thiết bị không đọc được ảnh này. Hãy chọn ảnh khác.");
      Matrix transform = orientation(original);
      oriented = transform.isIdentity() ? decoded
          : Bitmap.createBitmap(decoded, 0, 0, decoded.getWidth(), decoded.getHeight(), transform, true);
      if (oriented != decoded) { decoded.recycle(); decoded = null; }
      float ratio = Math.min(1f, (float) edge / Math.max(oriented.getWidth(), oriented.getHeight()));
      scaled = ratio == 1f ? oriented : Bitmap.createScaledBitmap(oriented,
          Math.max(1, Math.round(oriented.getWidth() * ratio)),
          Math.max(1, Math.round(oriented.getHeight() * ratio)), true);
      if (scaled != oriented) {
        if (decoded == oriented) decoded = null;
        oriented.recycle(); oriented = null;
      }
      opaque = Bitmap.createBitmap(scaled.getWidth(), scaled.getHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(opaque);
      canvas.drawColor(Color.WHITE);
      canvas.drawBitmap(scaled, 0, 0, null);
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      if (!opaque.compress(Bitmap.CompressFormat.JPEG, 85, bytes) || bytes.size() > UPLOAD_LIMIT)
        throw new IOException("Không thể nén ảnh xuống dưới 5 MB. Hãy chọn ảnh nhỏ hơn.");
      return bytes.toByteArray();
    } catch (SecurityException inaccessible) {
      throw new IOException("Quyền đọc ảnh đã hết hạn. Vui lòng chọn lại ảnh.");
    } finally {
      if (opaque != null) opaque.recycle();
      if (scaled != null && scaled != opaque) scaled.recycle();
      if (oriented != null && oriented != scaled && oriented != opaque) oriented.recycle();
      if (decoded != null && decoded != oriented && decoded != scaled && decoded != opaque) decoded.recycle();
      original.delete();
    }
  }

  // Đọc EXIF và tạo ma trận xoay/lật để ảnh tải lên có hướng hiển thị đúng.
  private static Matrix orientation(File original) {
    Matrix matrix = new Matrix();
    try {
      int value = new ExifInterface(original.getAbsolutePath()).getAttributeInt(
          ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      switch (value) {
        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1, 1);
        case ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180);
        case ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1, -1);
        case ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.setRotate(90); matrix.postScale(-1, 1); }
        case ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90);
        case ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.setRotate(270); matrix.postScale(-1, 1); }
        case ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270);
        default -> { }
      }
    } catch (IOException ignored) { } // Files without EXIF are already in their decoded orientation.
    return matrix;
  }
}

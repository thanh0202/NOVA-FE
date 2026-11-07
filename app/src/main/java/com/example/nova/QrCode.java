package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Dùng ZXing mã hóa chuỗi dữ liệu thanh toán thành bitmap QR để hiển thị trên màn hình.
 */
import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import java.util.HashMap;
import java.util.Map;

/** Encodes the exact provider/server payload locally; no QR data is sent to a third party. */
public final class QrCode {
  // Thiết lập charset và mức sửa lỗi cho dữ liệu QR dùng bởi ZXing.
  private static Map<EncodeHintType, Object> hints() {
    Map<EncodeHintType, Object> hints = new HashMap<>();
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    hints.put(EncodeHintType.MARGIN, 2);
    return hints;
  }

  // Dùng MultiFormatWriter tạo ma trận QR, đổi từng ô thành pixel của Bitmap.
  public static Bitmap encode(String payload) throws Exception {
    BitMatrix matrix =
        new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, 512, 512, hints());
    int[] pixels = new int[512 * 512];
    for (int y = 0; y < 512; y++)
      for (int x = 0; x < 512; x++)
        pixels[y * 512 + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
    return Bitmap.createBitmap(pixels, 512, 512, Bitmap.Config.ARGB_8888);
  }
}

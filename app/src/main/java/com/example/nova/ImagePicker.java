package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Mở bộ chọn ảnh hệ thống, nhận URI của đúng các ảnh người dùng đã chọn và chuyển lại cho màn hình yêu cầu.
 */
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/** System document picker: grants access only to the photos the user chooses. */
public final class ImagePicker {
  private static final int REQUEST = 6801;
  public interface Callback { void complete(List<Uri> images, String error); }
  private final Activity activity;
  private Callback pending;
  private int limit;

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public ImagePicker(Activity activity) { this.activity = activity; }

  // Mở bộ chọn ảnh hệ thống và lưu callback cho lần chọn này.
  public void open(boolean multiple, int maxCount, Callback callback) {
    if (pending != null) {
      callback.complete(Collections.emptyList(), "Hãy hoàn tất lần chọn ảnh đang mở.");
      return;
    }
    limit = multiple ? Math.max(1, Math.min(10, maxCount)) : 1;
    pending = callback;
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    try { activity.startActivityForResult(intent, REQUEST); }
    catch (ActivityNotFoundException unavailable) {
      pending = null;
      callback.complete(Collections.emptyList(), "Thiết bị chưa có ứng dụng chọn ảnh.");
    }
  }

  // Nhận kết quả từ Android và chuyển kết quả chọn ảnh tới ImagePicker.
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != REQUEST) return false;
    Callback callback = pending;
    pending = null;
    if (callback == null) return true; // The old screen was destroyed while the picker was open.
    if (resultCode != Activity.RESULT_OK || data == null) {
      callback.complete(Collections.emptyList(), null);
      return true;
    }
    LinkedHashSet<Uri> selected = new LinkedHashSet<>();
    if (data.getClipData() != null) {
      for (int i = 0; i < data.getClipData().getItemCount(); i++) {
        Uri uri = data.getClipData().getItemAt(i).getUri();
        if (uri != null) selected.add(uri);
        if (selected.size() > limit) break;
      }
    } else if (data.getData() != null) selected.add(data.getData());
    if (selected.size() > limit) {
      callback.complete(Collections.emptyList(), "Chỉ chọn tối đa " + limit + " ảnh mỗi lần.");
    } else callback.complete(new ArrayList<>(selected), null);
    return true;
  }

  // Đánh dấu đã đóng, hủy callback/công việc nền và giải phóng tài nguyên liên quan.
  public void close() { pending = null; }
}

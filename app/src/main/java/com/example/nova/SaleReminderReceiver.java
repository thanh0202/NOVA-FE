package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Đặt/hủy nhắc Flash Sale bằng AlarmManager và nhận sự kiện để hiển thị thông báo Android.
 */
import android.app.*;
import android.content.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.json.JSONObject;

/** Local device reminders; server inbox remains authoritative across devices. */
public final class SaleReminderReceiver extends BroadcastReceiver {
  private static final String CHANNEL = "nova_flash_sales";

  // Chuyển chuỗi thời gian API thành thời điểm dùng để đặt nhắc.
  public static long instant(String raw) {
    try {
      return Instant.parse(raw).toEpochMilli();
    } catch (Exception e) {
      try {
        return LocalDateTime.parse(raw).toInstant(ZoneOffset.UTC).toEpochMilli();
      } catch (Exception ignored) {
        return 0;
      }
    }
  }

  // Tạo PendingIntent ổn định để có thể đặt hoặc hủy đúng thông báo nhắc.
  private static PendingIntent alarmIntent(Context c, long id, String title, long productId) {
    Intent intent =
        new Intent(c, SaleReminderReceiver.class)
            .setAction("vn.nova.FLASH_" + id)
            .putExtra("id", id)
            .putExtra("title", title)
            .putExtra("productId", productId);
    return PendingIntent.getBroadcast(
        c,
        (int) (id ^ (id >>> 32)),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
  }

  // Lên lịch công việc hẹn giờ hoặc nhắc bằng API Android phù hợp với lớp hiện tại.
  public static void schedule(Context c, long id, String title, long productId, String startsAt) {
    long time = instant(startsAt);
    if (time <= System.currentTimeMillis()) return;
    try {
      c.getSharedPreferences("nova_reminders", Context.MODE_PRIVATE)
          .edit()
          .putString(
              String.valueOf(id),
              new JSONObject()
                  .put("id", id)
                  .put("title", title)
                  .put("productId", productId)
                  .put("startsAt", startsAt)
                  .toString())
          .apply();
      AlarmManager alarm = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
      alarm.setAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP, time, alarmIntent(c, id, title, productId));
    } catch (Exception ignored) {
    }
  }

  // Hủy lịch nhắc đã đặt cho đối tượng được chọn.
  public static void cancel(Context c, long id) {
    ((AlarmManager) c.getSystemService(Context.ALARM_SERVICE)).cancel(alarmIntent(c, id, "", 0));
    c.getSharedPreferences("nova_reminders", Context.MODE_PRIVATE)
        .edit()
        .remove(String.valueOf(id))
        .apply();
  }

  @Override
  // Xử lý sự kiện BroadcastReceiver và hiển thị thông báo nhắc ưu đãi.
  public void onReceive(Context c, Intent intent) {
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      for (Object raw :
          c.getSharedPreferences("nova_reminders", Context.MODE_PRIVATE).getAll().values())
        try {
          JSONObject r = new JSONObject(String.valueOf(raw));
          schedule(
              c,
              r.optLong("id"),
              r.optString("title"),
              r.optLong("productId"),
              r.optString("startsAt"));
        } catch (Exception ignored) {
        }
      return;
    }
    NotificationManager manager =
        (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.createNotificationChannel(
        new NotificationChannel(
            CHANNEL, "Nhắc Flash Sale", NotificationManager.IMPORTANCE_DEFAULT));
    Intent open =
        new Intent(c, MainActivity.class)
            .putExtra("productId", intent.getLongExtra("productId", 0));
    PendingIntent tap =
        PendingIntent.getActivity(
            c,
            (int) intent.getLongExtra("id", 0),
            open,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    Notification n =
        new Notification.Builder(c, CHANNEL)
            .setSmallIcon(R.drawable.nova_logo)
            .setContentTitle("Flash Sale đã bắt đầu!")
            .setContentText(intent.getStringExtra("title"))
            .setContentIntent(tap)
            .setAutoCancel(true)
            .build();
    try {
      manager.notify((int) intent.getLongExtra("id", 0), n);
    } catch (SecurityException ignored) {
    }
    c.getSharedPreferences("nova_reminders", Context.MODE_PRIVATE)
        .edit()
        .remove(String.valueOf(intent.getLongExtra("id", 0)))
        .apply();
  }
}

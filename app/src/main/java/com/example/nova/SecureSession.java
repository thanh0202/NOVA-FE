package com.example.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Lưu token đã mã hóa bằng AES/GCM; khóa mã hóa nằm trong Android Keystore, URL API lấy từ BuildConfig.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/** Tokens are encrypted using an unexportable device key. Passwords are never stored. */
public final class SecureSession {
  private static String temporaryToken = "";
  private static final String ALIAS = "nova_session_v1";
  private final SharedPreferences preferences;

  // Nhận Activity/context và các thành phần dùng chung để đối tượng hoạt động trong ứng dụng.
  public SecureSession(Context context) {
    preferences = context.getSharedPreferences("nova_private", Context.MODE_PRIVATE);
    // Retire the old editable endpoint and keep sessions scoped to this build's API.
    String previousApi = preferences.getString(
        "session_api_base", preferences.getString("api_base", BuildConfig.API_BASE_URL));
    if (!BuildConfig.API_BASE_URL.equals(previousApi)) clear();
    preferences.edit()
        .remove("api_base")
        .putString("session_api_base", BuildConfig.API_BASE_URL)
        .apply();
  }

  // Lấy hoặc tạo khóa AES trong Android Keystore; khóa không ghi thành chuỗi thuần trong preferences.
  private SecretKey key() throws Exception {
    KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
    ks.load(null);
    if (ks.containsAlias(ALIAS))
      return ((KeyStore.SecretKeyEntry) ks.getEntry(ALIAS, null)).getSecretKey();
    KeyGenerator generator =
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
    generator.init(
        new KeyGenParameterSpec.Builder(
                ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build());
    return generator.generateKey();
  }

  // Mã hóa token bằng AES/GCM và ghi ciphertext/IV vào SharedPreferences; lựa chọn ghi nhớ quyết định có lưu lâu dài hay không.
  public void save(String token) throws Exception {
    temporaryToken = "";
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key());
    preferences
        .edit()
        .putString("iv", Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP))
        .putString(
            "token",
            Base64.encodeToString(
                cipher.doFinal(token.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP))
        .apply();
  }

  // Mã hóa token bằng AES/GCM và ghi ciphertext/IV vào SharedPreferences; lựa chọn ghi nhớ quyết định có lưu lâu dài hay không.
  public void save(String token, boolean remember) throws Exception {
    if (remember) save(token);
    else {
      clear();
      temporaryToken = token;
    }
  }

  // Giải mã token đã lưu; khi dữ liệu không hợp lệ thì trả chuỗi rỗng.
  public String token() {
    if (!temporaryToken.isEmpty()) return temporaryToken;
    try {
      String encrypted = preferences.getString("token", "");
      if (encrypted.isEmpty()) return "";
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(
          Cipher.DECRYPT_MODE,
          key(),
          new GCMParameterSpec(
              128, Base64.decode(preferences.getString("iv", ""), Base64.NO_WRAP)));
      return new String(
          cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)), StandardCharsets.UTF_8);
    } catch (Exception e) {
      clear();
      return "";
    }
  }

  // Xóa token trong bộ nhớ và dữ liệu phiên đã lưu trong SharedPreferences.
  public void clear() {
    temporaryToken = "";
    preferences.edit().remove("token").remove("iv").apply();
  }

  // Lấy URL backend đã cấu hình trong BuildConfig của bản Android.
  public String baseUrl() {
    return BuildConfig.API_BASE_URL;
  }
}

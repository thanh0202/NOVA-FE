// NOVA: Cấu hình Android, BuildConfig, URL API, chế độ GK hoặc đầy đủ, Java, APK và thư viện QR.
plugins {
    alias(libs.plugins.android.application)
}

val azureApiUrl = "https://nova-uit.japaneast.cloudapp.azure.com/api"
val novaApiUrl = providers.gradleProperty("novaApiUrl").getOrElse(azureApiUrl).trim().trimEnd('/')
// An explicit URL can still select a separate full-featured backend.
val novaMidtermApi = providers.gradleProperty("novaMidtermApi").map(String::toBoolean)
    .getOrElse(novaApiUrl == azureApiUrl)
fun javaString(value: String): String = "\"" + value.trim().trimEnd('/')
    .replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
    namespace = "com.example.nova"
    compileSdk = 37
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.example.nova"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        buildConfigField("String", "API_BASE_URL", javaString(novaApiUrl))
        buildConfigField("boolean", "NOVA_MIDTERM_API", novaMidtermApi.toString())
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("com.google.zxing:core:3.5.3")
}

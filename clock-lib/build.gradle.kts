plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

// 库模块（com.android.library）：用于打包到应用中或作为依赖发布（如 Maven 工件）。
// 库的版本由 android { ... } 块外定义的 version 变量控制
version = "1.0.0"

android {
    namespace = "com.github.kevinvane.clocklib"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // 应用模块（com.android.application）：使用 versionCode 和 versionName 来标识构建
        // versionCode = 1
        // versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}

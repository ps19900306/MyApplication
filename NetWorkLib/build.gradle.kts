plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.yola.networklib"
    compileSdk = libs.versions.compileSdk.get().toInt()

    // TODO 没有构建不知道为什么 修复点1：buildFeatures 的正确语法
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = libs.versions.compileSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // 修复点2：确保 BASE_URL 的转义正确
        buildConfigField("String", "BASE_URL", "\"https://www.wanandroid.com/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 可选：覆盖 debug 的 BASE_URL（如果需要）
            buildConfigField("String", "BASE_URL", "\"https://www.wanandroid.com/\"")
        }
        // 修复点3：建议显式定义 debug 模式（可选）
        debug {
            buildConfigField("String", "BASE_URL", "\"https://www.wanandroid.com/\"")
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
    implementation (libs.retrofit2.kotlin.coroutines.adapter)
}
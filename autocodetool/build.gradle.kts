plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.navigation.safe.args)
}

android {
    namespace = "com.nwq.autocodetool"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nwq.autocodetool"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        // 修复点2：确保 BASE_URL 的转义正确
        buildConfigField("String", "BASE_URL", "\"https://www.wanandroid.com/\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            signingConfig = signingConfigs.getByName("debug")
        }
        // 修复点3：建议显式定义 debug 模式（可选）
        debug {
            buildConfigField("String", "BASE_URL", "\"http://localhost:8080/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    implementation(project(":BaseUtils"))
    implementation(project(":LogUitls"))
    implementation(project(":opencvIdentification"))
    implementation(project(":exculdeModule"))
    implementation(project(":NetWorkLib"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    implementation(libs.opencv)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //kapt(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)

    //图形库 暂时先不用
    //implementation(libs.dev.graphview)
    //图片选择器
    implementation(libs.pictureselector)
    implementation(libs.compress)

}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.kapt) apply false // KAPT 插件
    alias(libs.plugins.navigation.safe.args) apply false // KAPT 插件
}

dependencies {
    // 添加依赖项

}
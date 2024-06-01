// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
////    alias(libs.plugins.androidApplication) apply false
////    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
////
////    //the dependency for the Google services Gradle plugin
////    alias(libs.plugins.googleServices) apply false
////
////    alias(libs.plugins.hilt) apply false
//
//}

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
    }
    dependencies {

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
    }
}


plugins {
    id("com.android.application") version "8.0.2"  apply false
    id("com.android.library")  version "8.0.2" apply false
    id("org.jetbrains.kotlin.android")  version "1.8.20" apply false
    id("com.google.dagger.hilt.android")  version "2.46.1" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false

}
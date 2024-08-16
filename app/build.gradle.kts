import java.io.FileInputStream
import java.util.Properties

plugins {
//    alias(libs.plugins.androidApplication)
//    alias(libs.plugins.jetbrainsKotlinAndroid)
//
//    //the dependency for the Google services Gradle plugin
//    alias(libs.plugins.googleServices)
//
////    //kapt plugin
////    alias(libs.plugins.kotlin.kapt)
//
//    //hilt
//    alias(libs.plugins.hilt)
//
//    //navigation
//    alias(libs.plugins.navigation)

    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("androidx.navigation.safeargs.kotlin")
    id ("dagger.hilt.android.plugin")
    //The Google services Gradle plugin
    id("com.google.gms.google-services")


}

android {
    namespace = "com.kosiso.foodshare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kosiso.foodshare"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val props = Properties()
        props.load(FileInputStream("local.properties"))
        buildConfigField ("String", "API_KEY", "\"${props.getProperty("API_KEY")}\"")


        //buildConfigField ("String", "API_KEY", "\"${System.getProperty("API_KEY")}\"")


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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation ("androidx.core:core-ktx:1.8.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.activity:activity:1.8.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.fragment:fragment-ktx:1.7.0")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")


    implementation ("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")

    // The Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    // The dependencies for Firebase products
    implementation("com.google.firebase:firebase-analytics")
    // The Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // The dependency for the Google Play services library
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // The dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")
    // Firebase Storage Dependency
    implementation ("com.google.firebase:firebase-storage")

    //Geofirestore
    implementation ("com.github.imperiumlabs:GeoFirestore-Android:v1.5.0")


    // material
    implementation ("com.google.android.material:material:1.12.0-alpha03")

    //splash screen api
    //implementation("androidx.core:core-splashscreen:1.0.0-alpha02")


    // Architectural Components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Room
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")

    // Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:2.6.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Coroutine Lifecycle Scopes
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Navigation Components
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    kapt ("com.github.bumptech.glide:compiler:4.11.0")

    // Google Maps Location Services
    implementation ("com.google.android.gms:play-services-location:21.1.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")

    // Dagger Core
    implementation ("com.google.dagger:dagger:2.48.1")
    kapt ("com.google.dagger:dagger-compiler:2.48.1")

    // Dagger Android
    api ("com.google.dagger:dagger-android:2.48.1")
    api ("com.google.dagger:dagger-android-support:2.48.1")
    kapt ("com.google.dagger:dagger-android-processor:2.48.1")

    // Activity KTX for viewModels()
    implementation ("androidx.activity:activity-ktx:1.8.2")

    //Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:2.48.1")
    kapt ("com.google.dagger:hilt-android-compiler:2.48.1")

    // Easy Permissions
    implementation ("pub.devrel:easypermissions:3.0.0")



//    implementation "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
//    kapt "androidx.hilt:hilt-compiler:1.0.0-alpha01"





//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    implementation(libs.androidx.activity)
//    implementation(libs.androidx.constraintlayout)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//
//    // Firebase BoM
//    //The dependencies for Firebase products used
//    implementation (platform(libs.firebase.bom))
//    implementation (libs.firebase.analytics)
//    // The dependency for the Firebase Authentication library
//    implementation(libs.firebase.auth)
//    // The dependency for the Google Play services library
//    implementation (libs.play.services)
//
//    // Material Design
//    implementation(libs.material)
//
//    // Architectural Components
//    implementation(libs.lifecycle.viewmodel.ktx)
//
//    // Room
//    implementation(libs.room.runtime)
//    //kapt(libs.room.compiler)
//
//    // Kotlin Extensions and Coroutines support for Room
//    implementation(libs.room.ktx)
//
//    // Coroutines
//    implementation(libs.coroutines.core)
//    implementation(libs.coroutines.android)
//
//    // Coroutine Lifecycle Scopes
//    implementation(libs.lifecycle.runtime.ktx)
//
//    // Navigation Components
//    implementation(libs.navigation.fragment.ktx)
//    implementation(libs.navigation.ui.ktx)
//
//    // Glide
//    implementation(libs.glide)
//    //kapt(libs.glide.compiler)
//
//    // Google Maps Location Services
//    implementation(libs.play.services.location)
//    implementation(libs.play.services.maps)
//
//    // Dagger Core
//    implementation(libs.dagger)
//    //kapt(libs.dagger.compiler)
//
//    // Dagger Android
//    implementation(libs.dagger.android)
//    implementation(libs.dagger.android.support)
//    //kapt(libs.dagger.android.processor)
//
//    // Activity KTX for viewModels()
//    implementation(libs.activity.ktx)
//
//    // Dagger - Hilt
//    implementation(libs.hilt.android)
//    //kapt(libs.hilt.android.compiler)

}
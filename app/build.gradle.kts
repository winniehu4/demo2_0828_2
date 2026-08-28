plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.tp.demo2"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.tp.demo2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // TradPlus
   implementation("com.tradplusad:tradplus:16.6.0.1")

    /*// Admob
    implementation("com.google.android.gms:play-services-ads:25.4.0")
    implementation("com.tradplusad:tradplus-googlex:2.16.6.20.1")*/
/*// GMA Next Gen
    implementation("com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk:1.0.1")
    implementation("com.tradplusad:tradplus-gma-nextgen:2.16.4.10.1")*/
// GMA Next Gen
    implementation("com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk:1.2.1")
    implementation("com.tradplusad:tradplus-gma-nextgen:2.16.6.10.1")
// Meta
    implementation("com.facebook.android:audience-network-sdk:6.21.0")
    implementation("com.tradplusad:tradplus-facebook:1.16.4.0.1")
// Pangle
    implementation("com.tradplusad:tradplus-pangle:19.16.4.0.1")
    implementation("com.pangle.global:pag-sdk:8.1.0.3")


// Inmobi
    implementation("com.tradplusad:tradplus-inmobix:23.16.4.0.1")
    implementation("com.inmobi.monetization:inmobi-ads-kotlin:11.3.0")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
//optional dependency for better targeting
    implementation("androidx.browser:browser:1.8.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.android.gms:play-services-appset:16.0.2")
//optional dependency for better targeting
    implementation("com.google.android.gms:play-services-tasks:18.0.2")
//optional dependency for better targeting
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
    implementation("com.squareup.okio:okio:3.7.0")
    implementation("androidx.media3:media3-exoplayer:1.4.1")
// Mintegral
    implementation("com.tradplusad:tradplus-mintegralx_overseas:18.16.6.0.1")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.mbridge.msdk.oversea:mbridge_android_sdk:17.1.61")

    // Moloco
    implementation("com.moloco.sdk:moloco-sdk:4.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.20")
    implementation("com.tradplusad:tradplus-moloco:82.16.6.0.1")

// Liftoff
    implementation("com.tradplusad:tradplus-vunglex:7.16.6.0.1")
    implementation("com.vungle:vungle-ads:7.7.6")
// Yandex
    implementation("com.yandex.android:mobileads:8.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("com.tradplusad:tradplus-yandex:50.16.4.0.1")
// Bigo
    implementation("com.bigossp:bigo-ads:5.9.0")
    implementation("com.tradplusad:tradplus-bigo:57.16.4.0.1")
// Applovin
    implementation("com.applovin:applovin-sdk:13.6.3")
    implementation("com.tradplusad:tradplus-applovin:9.16.4.0.1")
    implementation("com.google.android.gms:play-services-ads-identifier:18.2.0")
// TP Exchange
// 请注意保持与主包版本同步更新
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.tradplusad:tp_exchange:40.16.4.0.1")

//比价
    //implementation(files("libs/compare_price-release.aar"))
// zMaticoo
    implementation("com.tradplusad:tradplus-zmaticoo:55.16.4.0.1")
    implementation("io.github.maticooads:maticoo-android-sdk:2.0.6.0")

// TaurusX
    implementation("com.taurusx.tax:ads:1.18.3")
    implementation("com.tradplusad:tradplus-taurusx:74.16.5.0.1")
// Columbus
// columbus needs Minimum SDK version 19+; Compile SDK version 34+.
    implementation("com.tradplusad:tradplus-columbus:76.16.5.0.1")
    implementation("com.mi.ads:columbus-sdk:4.0.9.0")


// KwaiAds
    implementation("com.tradplusad:tradplus-kwai:75.16.7.0.1")
    implementation("io.github.kwainetwork:adImpl:1.2.21")
    implementation("io.github.kwainetwork:adApi:1.2.21")
// media3-exoplayer适配版本为「1.0.0-alpha01 - 1.2.0」
    implementation("androidx.media3:media3-exoplayer:1.0.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.annotation:annotation:1.2.0")
// 最低支持 kotlin1.4.10
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10")
//最低支持play-services-ads-identifier:18.0.1
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")

/*// Tapjoy
    implementation("com.tapjoy:tapjoy-android-sdk:13.1.2@aar")
    implementation("com.tradplusad:tradplus-tapjoy:6.16.6.10.1")
    implementation("com.google.android.gms:play-services-ads-identifier:17.0.0")*/



        // ... other project dependencies
        //TradPlus Tools
    //implementation("com.tradplusad:tradplus-tool:1.1.7")
    implementation("androidx.recyclerview:recyclerview:1.2.0")
    implementation("androidx.appcompat:appcompat:1.6.1")


    implementation(fileTree("libs") { include("*.aar") })

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
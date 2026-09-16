plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    

    // Firebase
    id("com.google.gms.google-services")
    id("com.google.firebase.appdistribution")
}

val appVersionCode = project.findProperty("versionCode")?.toString()?.toIntOrNull() ?: 1
val appVersionName = project.findProperty("versionName")?.toString() ?: "1.0.0.1"

android {
    namespace = "com.mobile.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.mobile.app"
        minSdk = 26
        targetSdk = 34
        versionCode = appVersionCode
        versionName = appVersionName
        
        buildConfigField("String", "API_BASE_URL", "\"https://mobileapplication-qtau.onrender.com/\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = "release_password"
            keyAlias = "release_key"
            keyPassword = "release_password"
        }
    }

    buildTypes {
        release {
            buildConfigField("String", "API_BASE_URL", "\"https://mobileapplication-qtau.onrender.com/\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.10.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.4.0")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.5.1")
    implementation("androidx.paging:paging-compose:3.5.1")

    // CameraX
    val cameraxVersion = "1.6.2"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-video:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.60.1")
    ksp("com.google.dagger:hilt-compiler:2.60.1")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:5.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.5.0")
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ML Kit Barcode Scanning
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")
    implementation("com.google.firebase:firebase-analytics-ktx:22.5.0")
    implementation("com.google.firebase:firebase-appdistribution-api:16.0.0-beta20")
    
    // Room
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("app.cash.turbine:turbine:1.2.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.09.00"))
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

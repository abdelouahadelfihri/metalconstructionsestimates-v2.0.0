plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.metalconstructionsestimates"
    compileSdk = 34

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }

    defaultConfig {
        manifestPlaceholders.putAll(
            mapOf(
                "smallScreens" to "true",
                "normalScreens" to "true",
                "largeScreens" to "false",
                "xlargeScreens" to "false"
            )
        )
        applicationId = "com.example.metalconstructionsestimates"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dataBinding {
        enable = true
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.5")
    implementation("com.google.api-client:google-api-client:1.31.5")
    implementation("com.google.android.gms:play-services-base:18.1.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0")
    implementation("com.google.api-client:google-api-client-android:1.34.1")
    implementation("com.google.http-client:google-http-client-android:1.41.5")
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
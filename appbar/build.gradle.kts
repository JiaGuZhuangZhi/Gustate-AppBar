import com.android.build.gradle.internal.utils.createPublishingInfoForApp

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.gustate.appbar"
    compileSdk = 35

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    defaultConfig {
        applicationId = "com.gustate.appbar"
        minSdk = 28
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
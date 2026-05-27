plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "ir.lifeplus.gamenethelper"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "ir.lifeplus.gamenethelper"
        minSdk = 23
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.10")
    implementation("androidx.xr.runtime:runtime:1.0.0-alpha05")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    //===> Gson
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("androidx.room:room-runtime:2.7.1")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.7.1")
    //===> SparkChart
    implementation("com.robinhood.spark:spark:1.2.0")
    //===> MPChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
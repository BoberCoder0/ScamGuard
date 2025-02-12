plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    }

android {
    namespace = "com.example.testapp2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.testapp2"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
        //dataBinding = true //что что не работает? убери
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.mediarouter)
    implementation(libs.poi.ooxml)
    implementation(libs.appcompat)
    implementation(libs.firebase.auth)
    /*implementation(project(":app"))*/
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    // Firebase Firestore
    implementation ("com.google.firebase:firebase-firestore:24.1.0")
    // Firebase Auth (если ещё не добавлено)
    implementation ("com.google.firebase:firebase-auth:21.0.5")
    // Добавь это, если ещё нет зависимости для Firebase BOM (Bill of Materials)
    implementation (platform("com.google.firebase:firebase-bom:32.0.0"))
    // Firebase SDK для базовых функций
    implementation ("com.google.firebase:firebase-core:21.1.0")
}
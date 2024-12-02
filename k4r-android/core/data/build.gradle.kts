plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.non.k4r.core.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(libs.inject.javax.inject)

    // Dependency injection
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.room.runtime) // Room 核心库
    ksp(libs.androidx.room.compiler) // Room 编译器
    implementation(libs.androidx.room.ktx) // Kotlin 扩展
    // JSON serialization library, works with the Kotlin serialization plugin
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
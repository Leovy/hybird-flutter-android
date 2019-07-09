import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.nikeorever.hybird.flutteroid"
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro", "coroutines.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType < KotlinCompile<KotlinJvmOptions> > {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to "*.jar"))
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.core:core-ktx:1.0.2")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation(kotlin("reflect", KotlinCompilerVersion.VERSION))

    // Core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M2")
    // For android extension
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0-M2")

    // Android Jetpack
    val lifecycleVersion = "2.2.0-alpha01"
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleVersion")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")

    // Android KTX
    // Core
    implementation("androidx.core:core-ktx:1.0.2")
    // For viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-alpha02")
    // For textInfo.text = "up complete"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-alpha02")

    val workVersion = "2.1.0-beta02"
    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.6.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.0")

    // Google AutoService
    implementation("com.google.auto.service:auto-service-annotations:1.0-rc5")
    implementation("com.google.auto:auto-common:0.10") {
        exclude("com.google.guava", "guava")
    }
    kapt("com.google.auto.service:auto-service:1.0-rc5")

    //Flutter module
    implementation(project(":flutter"))
}

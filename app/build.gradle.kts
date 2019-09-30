import com.nikeorever.gradle.FlutterModuleApplicationPlugin
import com.nikeorever.gradle.NikeoTransformerPlugin
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    kotlin("kapt")
}

apply {
    plugin<FlutterModuleApplicationPlugin>()
    plugin<NikeoTransformerPlugin>()
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
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation(kotlin("reflect", KotlinCompilerVersion.VERSION))

    // Core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
    // For android extension
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.1")

    // Android KTX
    // Core
    implementation("androidx.core:core-ktx:1.1.0")
    testImplementation("com.android.tools.build:gradle:3.5.0")
}

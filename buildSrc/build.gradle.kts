import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions


plugins {
    kotlin("jvm") version "1.3.40"
}

tasks.withType <KotlinCompile<KotlinJvmOptions>> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("com.android.tools.build", "gradle", "3.4.1")
}

buildscript {
    dependencies {
        classpath("com.android.tools.build", "gradle", "3.4.1")
    }

    repositories {
        google()
    }
}

repositories {
    jcenter()
    google()
}




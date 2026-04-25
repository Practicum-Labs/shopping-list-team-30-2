import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
}

android {

    val properties = Properties()
    val gradlePropertiesFile = rootProject.file("gradle.properties")

    if (gradlePropertiesFile.exists()) {
        properties.load(gradlePropertiesFile.inputStream())
    }

    val localProperties = Properties()
    val userGradleFile = File(System.getProperty("user.home"), ".gradle/gradle.properties")
    if (userGradleFile.exists()) {
        localProperties.load(userGradleFile.inputStream())
    }

    val storeFilePath = (localProperties.getProperty("RELEASE_STORE_FILE")
        ?: properties.getProperty("RELEASE_STORE_FILE")) ?: ""
    val storePasswordProp = (localProperties.getProperty("RELEASE_STORE_PASSWORD")
        ?: properties.getProperty("RELEASE_STORE_PASSWORD")) ?: ""
    val keyAliasProp = (localProperties.getProperty("RELEASE_KEY_ALIAS")
        ?: properties.getProperty("RELEASE_KEY_ALIAS")) ?: ""
    val keyPasswordProp = (localProperties.getProperty("RELEASE_KEY_PASSWORD")
        ?: properties.getProperty("RELEASE_KEY_PASSWORD")) ?: ""

    signingConfigs {
        register("release") {
            if (storeFilePath.isNotEmpty() &&
                storePasswordProp.isNotEmpty() &&
                keyAliasProp.isNotEmpty() &&
                keyPasswordProp.isNotEmpty()
            ) {
                storeFile = file(storeFilePath)
                storePassword = storePasswordProp
                keyAlias = keyAliasProp
                keyPassword = keyPasswordProp
                enableV1Signing = true
                enableV2Signing = true
                println("Release signing has been configured: ${file(storeFilePath).absolutePath}")
            } else {
                println("Release signing credentials not found, using debug signature")
            }
        }
    }

    namespace = "ru.ya.practicum.shopper"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "ru.ya.practicum.shopper"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    reports {
        html.required.set(true)
    }
}
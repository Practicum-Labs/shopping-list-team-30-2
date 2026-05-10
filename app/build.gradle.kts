import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    val properties = Properties()
    val gradlePropertiesFile = rootProject.file("gradle.properties")

    if (gradlePropertiesFile.exists()) {
        properties.load(gradlePropertiesFile.inputStream())
    }

    val keystoreBase64 = System.getenv("SIGNING_KEYSTORE_BASE64")
    val storePasswordFromEnv = System.getenv("SIGNING_STORE_PASSWORD")
    val keyAliasFromEnv = System.getenv("SIGNING_KEY_ALIAS")
    val keyPasswordFromEnv = System.getenv("SIGNING_KEY_PASSWORD")

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
            val useCI = !keystoreBase64.isNullOrEmpty()

            if (useCI) {
                val keystoreBytes = Base64.getDecoder().decode(keystoreBase64)
                val tempKeystoreFile = File.createTempFile("temp_keystore", ".jks")
                tempKeystoreFile.deleteOnExit()
                tempKeystoreFile.writeBytes(keystoreBytes)

                storeFile = tempKeystoreFile
                storePassword = storePasswordFromEnv ?: ""
                keyAlias = keyAliasFromEnv ?: ""
                keyPassword = keyPasswordFromEnv ?: ""

                println("Release signing configured (from CI environment)")
            } else {
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

        val developProperties = Properties()
        val developPropertiesFile = rootProject.file("develop.properties")
        if (developPropertiesFile.exists()) {
            developProperties.load(developPropertiesFile.inputStream())
        }

        buildConfigField("String", "TEST_USER_EMAIL", "\"${developProperties.getProperty("TEST_USER_EMAIL", "")}\"")
        buildConfigField("String", "TEST_USER_PASSWORD", "\"${developProperties.getProperty("TEST_USER_PASSWORD", "")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            type = "String",
            name = "BASE_URL",
            value = "\"https://practicumopbackend-production.up.railway.app/\""
        )
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

            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"https://practicumopbackend-production.up.railway.app/\""
            )
        }

        debug {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"https://practicumopbackend-production.up.railway.app/\""
            )
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
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.koin)
    implementation(libs.koin.androidx.compose)
    implementation(libs.play.services.auth)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.firebase.auth.ktx.v2321)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    detektPlugins(libs.detekt.formatting)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    reports {
        html.required.set(true)
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}
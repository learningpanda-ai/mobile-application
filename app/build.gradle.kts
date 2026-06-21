import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}
fun devRealFlag(propertyKey: String, default: Boolean = false): String {
    val raw = localProperties.getProperty(propertyKey)?.trim()?.lowercase() ?: return default.toString()
    val enabled = raw == "true" || raw == "1" || raw == "yes"
    return enabled.toString()
}

android {
    namespace = "com.example.learningpandaai"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.learningpandaai"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val baseUrl = localProperties.getProperty("BASE_URL","http://10.0.2.2:8000/")
            .trim()
            .let { if(it.endsWith("/")) it else "$it/" }

        buildConfigField("String","BASE_URL","\"$baseUrl\"")
        buildConfigField("String","MOBILE_API_KEY","\"${localProperties.getProperty("MOBILE_API_KEY","")}\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")}\"")
        buildConfigField("String", "CERT_PIN_SHA256", "\"${localProperties.getProperty("CERT_PIN_SHA256", "")}\"")
        buildConfigField("boolean", "CERT_PINNING_ENABLED", devRealFlag("CERT_PINNING_ENABLED"))

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "environment"
    productFlavors {
        create("mock") {
            dimension = "environment"
            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"
        }

        create("prod")
        {
            dimension = "environment"
        }

        create("dev")
        {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("boolean", "DEV_REAL_AUTH", devRealFlag("DEV_REAL_AUTH", default = true))
            buildConfigField("boolean", "DEV_REAL_SYLLABUS", devRealFlag("DEV_REAL_SYLLABUS"))
            buildConfigField("boolean", "DEV_REAL_PROGRESS", devRealFlag("DEV_REAL_PROGRESS"))
            buildConfigField("boolean", "DEV_REAL_AGENT", devRealFlag("DEV_REAL_AGENT"))
            buildConfigField("boolean", "DEV_REAL_PROFILE", devRealFlag("DEV_REAL_PROFILE"))
            buildConfigField("boolean", "DEV_REAL_ONBOARDING", devRealFlag("DEV_REAL_ONBOARDING", default = true))
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Added..

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.coil.compose)


}
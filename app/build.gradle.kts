import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.dagger.hilt)//hilt dependency injection
}

android {
    namespace = "meow.softer.mydiary"
    compileSdk = 36

    defaultConfig {
        applicationId = "meow.softer.mydiary"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug" // for installing release and debug builds side by side
            versionNameSuffix = "-DEBUG"
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    //stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Set Room Schema export location
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // kotlin & compose
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    // compose bill of material
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    testImplementation(composeBom)
    androidTestImplementation(composeBom)
    debugImplementation(composeBom)

    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.compose.icons.core)
    implementation(libs.compose.icons.extended)
    implementation(libs.navigation.compose)

    androidTestImplementation(libs.ui.test.junit4)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // splash screen
    implementation(libs.splash.screen)
    // data store preferences
    implementation(libs.datastore.preferences)
    // room database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // photo cropping
    implementation(libs.crop.kit)
    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    // desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    //live data
    implementation(libs.androidx.runtime.livedata)
    // coil
    implementation(libs.coil.compose)
    // biometric
    implementation(libs.androidx.biometric)
}

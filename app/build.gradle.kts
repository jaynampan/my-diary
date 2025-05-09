plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
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
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }


    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
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

    androidTestImplementation(libs.ui.test.junit4)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    //lib
    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout.compose)


    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //libs
    implementation(libs.commons.io)
    implementation(libs.material.calendarview)
    implementation(libs.android.segmented)
    implementation(libs.fresco)
    implementation(libs.holocolorpicker)
    implementation(libs.filepicker)
    implementation(libs.library)
    implementation(libs.advrecyclerview)
    implementation(libs.gson)
    implementation(libs.ucrop)
    implementation(libs.circleimageview)
    implementation(libs.ultimaterecyclerview.library)
    implementation(libs.recyclerview.animators)
    implementation(libs.play.services.places)
    implementation(libs.photodraweeview)
    // desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

}
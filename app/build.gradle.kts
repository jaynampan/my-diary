plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {

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
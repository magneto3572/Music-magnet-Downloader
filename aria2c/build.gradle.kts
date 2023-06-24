@Suppress("Dsl_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android )
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
        testInstrumentationRunner  ="androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled =  false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    namespace =  "com.bishal.aria2c"

}


dependencies {
//    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation(project(":ytdlplibrary"))
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso)
    implementation(libs.apache.common)
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    kotlin("plugin.serialization") version "1.8.21"
    id("com.google.protobuf") version "0.9.3"
    id("com.google.dagger.hilt.android")
    kotlin("kapt")

}

android {
    namespace = "com.example.vpassport"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.vpassport"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX Core and Kotlin extensions
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
    implementation("com.google.android.material:material:1.9.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    // To use the JUnit Extension APIs
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // Kotlin extensions for androidx.test.ext.junit
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.0")
    androidTestImplementation("junit:junit:4.12")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Lifecycle components
    val lifecycle_version = "2.6.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

    // ML Kit
    implementation("com.google.android.gms:play-services-code-scanner:16.0.0")

    // System UI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.2-alpha")

    // Room Database
    val room_version = "2.5.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Proto DataStore
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:3.18.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Kotlinx Serialization and Collections
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // Dagger and Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // JMRTD
    implementation("org.jmrtd:jmrtd:0.7.37")

    // Navigation
    val nav_version = "2.6.0"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Ktor
    val ktor_version = "1.6.4"
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    // Scarlet
    val scarlet_version = "0.1.12"
    implementation("com.tinder.scarlet:scarlet:$scarlet_version")
    implementation("com.tinder.scarlet:lifecycle-android:$scarlet_version")
    implementation("com.tinder.scarlet:websocket-okhttp:$scarlet_version")
    implementation("com.tinder.scarlet:message-adapter-moshi:$scarlet_version")
    implementation("com.tinder.scarlet:stream-adapter-coroutines:$scarlet_version")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.17.3"
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

kapt {
    correctErrorTypes = true
}
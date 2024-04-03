plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
    id("io.gitlab.arturbosch.detekt").version("1.23.3")
}

android {
    namespace = "com.app.kirimpesanapp"
    compileSdk = 34

    flavorDimensions += "version"
    productFlavors {
        create("production") {
            applicationId = "com.app.kirimpesanapp"
            minSdk = 26
            targetSdk = 34
            versionCode = 1
            versionName = "1.0.0"
            dimension = "version"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        create("staging") {
            applicationId = "com.app.kirimpesanapp"
            minSdk = 26
            targetSdk = 34
            versionCode = 1
            versionName = "1.0.0-staging"
            dimension = "version"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "Kirim Pesan")

            buildConfigField("String", "web_client_id", "\"912815326166-959f7igb4dauols6jespn89eikmj6k7m.apps.googleusercontent.com\"")
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "KirimpesanAppDebug")

            buildConfigField("String", "web_client_id", "\"912815326166-959f7igb4dauols6jespn89eikmj6k7m.apps.googleusercontent.com\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-appdistribution-api-ktx:16.0.0-beta11")    

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    //text recognition
    implementation("com.google.mlkit:text-recognition:16.0.0")

    //room
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.room:room-paging:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
}
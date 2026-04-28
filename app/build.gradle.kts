plugins {
    id("com.android.application")
}

android {
    namespace = "com.datamarkets.app"

    compileSdk = 34

    defaultConfig {
        applicationId = "com.datamarkets.app"
        // Mantenemos 26 de GitHub para mayor compatibilidad de dispositivos
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val alphaVantageKey = project.findProperty("ALPHA_VANTAGE_KEY")?.toString() ?: ""
        buildConfigField("String", "ALPHA_VANTAGE_KEY", "\"$alphaVantageKey\"")
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
        // Mantenemos tu versión de Java 11 por ser más moderna
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    //
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // --- Librerías Base (Versiones actualizadas de GitHub) ---
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.fragment:fragment:1.7.0")

    // --- Retrofit + Gson (API Financiera) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Glide (Añadido de tu versión local) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // --- ViewModel + LiveData (Patrón MVVM) ---
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // --- Navigation Component ---
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")

    // --- MPAndroidChart (Gráficos de GitHub) ---
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // --- Tests ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
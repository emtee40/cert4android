plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

android {
    namespace = "at.bitfire.cert4android"

    compileSdk = 33

    defaultConfig {
        minSdk = 21            // Android 5
        targetSdk = 33         // Android 13

        aarMetadata {
            minCompileSdk = 29
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        aidl = true
    }

    lint {
        disable += listOf("MissingTranslation", "ExtraTranslation")
    }

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testFixtures {
        enable = true
    }

    publishing {
        // Configure publish variant
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    // Configure publishing data
    publications {
        register("release", MavenPublication::class.java) {
            groupId = "com.github.bitfireAT"
            artifactId = "cert4android"
            version = project.properties["cert4android.version"] as String

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.conscrypt:conscrypt-android:2.5.2")

    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    testImplementation("junit:junit:4.13.2")
}
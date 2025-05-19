plugins {
    id("com.android.application") version "8.8.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false  // Updated version
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}


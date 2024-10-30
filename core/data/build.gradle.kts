plugins {
    id("griffin.library")
}

android {
    namespace = "com.griffin.core.data"
}

dependencies {
    implementation(libs.kotlin.serialization)
}
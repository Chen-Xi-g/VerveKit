plugins {
    id("griffin.library")
}

android {
    namespace = "com.griffin.core.rv"
}

dependencies {
    // RecyclerView
    implementation(libs.androidx.recyclerview)
    // refresh layout
    api(libs.refresh)
    api(libs.refresh.header)
    api(libs.refresh.footer)
    implementation(project(":core:data"))
}
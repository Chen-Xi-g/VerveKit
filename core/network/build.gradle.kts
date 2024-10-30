plugins {
    id("griffin.library")
    id("griffin.hilt")
}

android {
    namespace = "com.griffin.core.network"
    buildTypes {
        // debug
        getByName("debug") {
            // 构建配置
            buildConfigField(
                "String",
                "SERVER_URL",
                "\"${libs.versions.domainUrlMainDebug.get()}\""
            )
        }
        // release
        getByName("release") {
            // 构建配置
            buildConfigField(
                "String",
                "SERVER_URL",
                "\"${libs.versions.domainUrlMain.get()}\""
            )
        }
    }
    buildFeatures{
        buildConfig = true
    }
}

dependencies {
    implementation(libs.kotlin.serialization)
    implementation(libs.retrofit)
    implementation("com.alibaba.fastjson2:fastjson2:2.0.53")
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(project(":core:data"))
    implementation(project(":core:utils"))
    implementation(project(":core:router"))
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)
}
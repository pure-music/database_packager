pluginManagement {
    repositories {
        gradlePluginPortal()
        google() // 添加 Google Maven 仓库
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}



rootProject.name = "subsonic_database_scan"


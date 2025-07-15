plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("androidx.room") version "2.7.2"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

application {
    mainClass.set("MainKt") // 指定主类，若你的 main 函数在 Main.kt 中，默认主类名是 MainKt
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
//    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    val ktor_version = "3.0.3"
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    api("com.soywiz:korlibs-crypto:6.0.0")
    api("androidx.room:room-runtime:2.7.2")
    api("androidx.sqlite:sqlite-bundled:2.5.2")
    ksp("androidx.room:room-compiler:2.7.2")
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // 将所有依赖打包到 JAR 中
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "dev.kosmx.dropper"
version = "1.0.0"
application {
    mainClass.set("dev.kosmx.dropper.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.json)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    // ignore transitive error, crypto will only happen between trusted endpoints
    implementation(libs.exposed.crypt)
    implementation(libs.exposed.postgresql)
    implementation(libs.exposed.sqlite)
    implementation(libs.exposed.time)

    //testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}
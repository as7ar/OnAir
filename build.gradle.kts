val kotlinVersion : String by project

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("kapt") version "2.3.0"
    id("com.gradleup.shadow") version "9.2.2"
    id("java")
    `java-library`
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"

}

group = "kr.astar"
version = "1.3.0-a1"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "eclipse")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

    dependencies {
        implementation("com.github.twitch4j:twitch4j:1.25.0")
        implementation("io.github.r2turntrue:chzzk4j:0.1.3")
        implementation("com.github.as7ar:cime4j:1.0-a2")

        implementation("org.slf4j:slf4j-api:2.0.17")

        implementation("org.jsoup:jsoup:1.15.3")
        implementation("org.seleniumhq.selenium:selenium-java:4.34.0")
        implementation("io.github.bonigarcia:webdrivermanager:6.2.0")

        implementation("com.google.code.gson:gson:2.13.1")
        implementation("com.google.api-client:google-api-client:1.33.0")
        implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
        implementation("com.google.apis:google-api-services-youtube:v3-rev20230816-2.0.0")
        implementation("com.google.http-client:google-http-client-jackson2:1.39.2")

        implementation("com.squareup.okhttp3:okhttp:4.12.0")
    }
}

project(":platforms:paper") {
    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT") {
            exclude("com.google.code.gson", "gson")
            exclude("org.slf4j", "slf4j-api")
        }
        // compileOnly(files("libs/PlaceholderAPI-2.11.7.jar"))
    }
}

val targetJavaVersion = 25
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

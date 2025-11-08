val kotlinVersion : String by project

plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "8.3.0"
    id("java")
    `java-library`
}

group = "kr.apo2073"
version = "1.2.1-b9"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.skriptlang.org/releases")
    maven { url = uri("https://repo.gradle.org/gradle/repo") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT") {
        exclude("com.google.code.gson", "gson")
        exclude("org.slf4j", "slf4j-api")
    }
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly("net.wesjd:anvilgui:1.10.6-SNAPSHOT")
    compileOnly("org.bstats","bstats-bukkit","3.1.0")
    compileOnly("com.github.SkriptLang:Skript:2.12.1")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("com.github.twitch4j:twitch4j:1.25.0")

    implementation("org.slf4j:slf4j-api:2.0.17")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("io.socket:socket.io-client:2.1.1")
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.seleniumhq.selenium:selenium-java:4.34.0")
    implementation("io.github.bonigarcia:webdrivermanager:6.2.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.5")

    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.google.api-client:google-api-client:1.33.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20230816-2.0.0")
    implementation("com.google.http-client:google-http-client-jackson2:1.39.2")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf(
        "version" to version
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    minimize {
        exclude("kr.apo2073.*")
    }
    archiveFileName.set("onAir-${version}.jar")
    archiveClassifier.set("all")
//    destinationDirectory = file("C:\\Users\\PC\\Desktop\\Test_Server\\21.8\\plugins")
//    destinationDirectory= file("C:\\Users\\이태수\\Desktop\\server\\plugins")
//    destinationDirectory=file("C:\\Users\\PC\\Desktop\\projects\\OnAir\\run\\plugins")
    mergeServiceFiles()
    dependencies {
        include(dependency("net.wesjd:anvilgui:1.10.6-SNAPSHOT"))
    }
}

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "kr.apo2073"
version = "1.0-ALPHA"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.skriptlang.org/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("net.wesjd:anvilgui:1.10.6-SNAPSHOT")
    implementation("org.bstats","bstats-bukkit","3.1.0")
    implementation("com.github.SkriptLang:Skript:2.11.2")
    implementation("me.clip:placeholderapi:2.11.6")

    implementation(files("libs/chzzk4j-0.1.1.jar"))
    implementation(files("libs/ToontaionLiv-1.2.1.jar"))
    implementation(files("libs/AfreecatvLib-master-1.0.3.jar"))
    implementation(files("libs/YouTubeLiv-1.1.1.jar"))
    implementation("com.github.twitch4j:twitch4j:1.25.0")

    implementation("org.slf4j:slf4j-api:2.0.7")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("io.socket:socket.io-client:2.0.1")
    implementation("io.reactivex.rxjava2:rxjava:2.1.16")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.seleniumhq.selenium:selenium-java:4.34.0")
    implementation("io.github.bonigarcia:webdrivermanager:6.2.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.5")

    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    minimize()
    archiveFileName.set("onAir-${version}.jar")
    archiveClassifier.set("all")
//    destinationDirectory = file("C:\\Users\\PC\\Desktop\\Test_Server\\21.1\\plugins")
//    destinationDirectory= file("C:\\Users\\이태수\\Desktop\\server\\plugins")
    destinationDirectory=file("C:\\Users\\PC\\Desktop\\projects\\OnAir\\run\\plugins")
    mergeServiceFiles()
    dependencies {
        include(dependency("net.wesjd:anvilgui:1.10.6-SNAPSHOT"))
        include(dependency(files("libs/AfreecatvLib-master-1.0.3.jar")))
        include(dependency(files("libs/ToontaionLiv-1.2.1.jar")))
        include(dependency(files("libs/chzzk4j-0.1.1.jar")))
    }
}

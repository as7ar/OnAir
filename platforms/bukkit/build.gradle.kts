plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
}

group = rootProject.group
version = rootProject.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT") {
        exclude("com.google.code.gson", "gson")
        exclude("org.slf4j", "slf4j-api")
    }

    implementation(project(":API"))

    compileOnly("net.wesjd:anvilgui:1.10.6-SNAPSHOT")
    compileOnly("org.bstats","bstats-bukkit","3.1.0")
    compileOnly("com.github.SkriptLang:Skript:2.13.1")
    implementation("me.clip:placeholderapi:2.11.7")
}

kotlin {
    jvmToolchain(21)
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
        exclude("kr.astar.*")
    }
    archiveFileName.set("OnAir-${version}.jar")
    archiveClassifier.set("all")
//    destinationDirectory = file("C:\\Users\\PC\\Desktop\\Test_Server\\21.8\\plugins")
//    destinationDirectory= file("C:\\Users\\이태수\\Desktop\\server\\plugins")
//    destinationDirectory=file("C:\\Users\\PC\\Desktop\\projects\\OnAir\\run\\plugins")
    mergeServiceFiles()
    dependencies {
        include(dependency("net.wesjd:anvilgui:1.10.6-SNAPSHOT"))
        include(project(":API"))
    }
}

tasks.build {
    dependsOn("shadowJar")
}

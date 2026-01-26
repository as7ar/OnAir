plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
}

group = "kr.astar.onair"
version = rootProject.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["bukkitVersion"]}-R0.1-SNAPSHOT") {
        exclude("com.google.code.gson", "gson")
        exclude("org.slf4j", "slf4j-api")
    }

    implementation(project(":API"))
//    implementation(project(":platforms:paper"))
//    implementation(project(":platforms:spigot"))

    compileOnly("net.wesjd:anvilgui:1.10.6-SNAPSHOT")
    compileOnly("org.bstats","bstats-bukkit","3.1.0")
    compileOnly("com.github.SkriptLang:Skript:2.13.1")
    compileOnly("me.clip:placeholderapi:2.11.7")
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
        exclude("kr.astar.onair.*")
        exclude(dependency("net.kyori:.*"))
    }
    archiveFileName.set("OnAir-${version}.jar")
    archiveClassifier.set("all")
    destinationDirectory = file("${project.rootDir}/target")

//    destinationDirectory = file("C:\\Users\\PC\\Desktop\\Test_Server\\21.8\\plugins")
//    destinationDirectory= file("C:\\Users\\이태수\\Desktop\\server\\plugins")
//    destinationDirectory=file("C:\\Users\\PC\\Desktop\\projects\\OnAir\\run\\plugins")

    mergeServiceFiles()
    dependencies {
        include(dependency("net.wesjd:anvilgui:1.10.6-SNAPSHOT"))
        include(dependency("net.kyori:.*"))
        include(project(":API"))
        include(project(":platforms:paper"))
        include(project(":platforms:spigot"))
    }
}

tasks.build {
    dependsOn("shadowJar")
}

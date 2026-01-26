plugins {
    kotlin("jvm")
}

group = "kr.astar.onair.paper"
version = rootProject.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["bukkitVersion"]}-R0.1-SNAPSHOT") {
        exclude("com.google.code.gson", "gson")
        exclude("org.slf4j", "slf4j-api")
    }

    implementation(project(":API"))
    compileOnly(project(":plugin"))

    compileOnly("net.wesjd:anvilgui:1.10.6-SNAPSHOT")
    compileOnly("org.bstats","bstats-bukkit","3.1.0")
    compileOnly("com.github.SkriptLang:Skript:2.13.1")
    compileOnly("me.clip:placeholderapi:2.11.7")
}
plugins {
    kotlin("jvm")
}

group = "kr.astar.onair.spigot"
version = rootProject.version

dependencies {
    compileOnly("org.spigotmc:spigot-api:${rootProject.properties["bukkitVersion"]}-R0.1-SNAPSHOT")

    implementation(project(":API"))
    compileOnly(project(":plugin"))

    compileOnly("net.wesjd:anvilgui:1.10.6-SNAPSHOT")
    compileOnly("org.bstats","bstats-bukkit","3.1.0")
    compileOnly("com.github.SkriptLang:Skript:2.13.1")
    compileOnly("me.clip:placeholderapi:2.11.7")
}
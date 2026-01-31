import org.gradle.kotlin.dsl.maven

rootProject.name = "OnAir"

include(
    "API",
    "platforms:paper",
    "platforms:spigot"
//    "platforms:velocity"
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://jitpack.io")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
//        maven("https://repo.helpch.at/releases")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://repo.skriptlang.org/releases")
        maven { url = uri("https://repo.gradle.org/gradle/repo") }
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}
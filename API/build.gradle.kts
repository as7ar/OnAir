plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    id("java")
}

group = "kr.astar.onair.api"
version = rootProject.version

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

kotlin {
    jvmToolchain(21)
}
plugins {
    `maven-publish`
    kotlin("jvm")
}

dependencies {
    testImplementation(libs.kotest)
    testImplementation(libs.kotestAssertions)
    testImplementation(libs.kotestRunnerJunit5)
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
plugins {
    `maven-publish`
    kotlin("jvm")
}
dependencies {
    implementation(kotlin("stdlib-jdk21"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}
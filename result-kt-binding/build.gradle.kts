plugins {
    `maven-publish`
    kotlin("jvm")
}
dependencies {
    api(project(":result-kt-core"))
    implementation(kotlin("stdlib-jdk21"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}
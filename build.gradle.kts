plugins {
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
}

spotless {
    kotlin {
        target("**/*.kt", "**/*.kts")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        ktlint()
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:function-naming"
        }
    }
}

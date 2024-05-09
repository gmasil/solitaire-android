import com.diffplug.spotless.LineEnding

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.spotless)
}

spotless {
    encoding("UTF-8")
    lineEndings = LineEnding.UNIX
    format("misc") {
        target("**/*.properties", "**/*.toml", "**/*.xml", "**/*.yml", "**/*.yaml")
        targetExclude(".idea/**")
        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
    java {
        target("**/*.java")
        eclipse()
    }
    kotlin {
        target("**/*.kt", "**/*.kts")
        ktfmt().dropboxStyle()
    }
}

tasks.findByPath(":app:assembleDebug")?.group = "build"

tasks.findByPath(":app:assembleRelease")?.group = "build"

tasks.named<TaskReportTask>("tasks") { displayGroups = listOf("build", "install", "verification") }

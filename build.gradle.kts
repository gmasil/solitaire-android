import com.diffplug.spotless.LineEnding

// Top-level build file where you can add configuration options common to all sub-projects/modules.
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

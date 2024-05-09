import com.diffplug.spotless.LineEnding

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.diffplug.spotless") version "6.25.0"
}

spotless {
    encoding("UTF-8")
    format("all") {
        target("*")
        trimTrailingWhitespace()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
}

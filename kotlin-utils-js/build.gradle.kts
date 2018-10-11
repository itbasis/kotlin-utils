import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin

apply {
  plugin<KotlinPlatformJsPlugin>()
}

val kotlinLoggingVersion: String by project

dependencies {
  "compile"("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
}

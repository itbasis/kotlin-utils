import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin

apply {
  plugin<KotlinPlatformCommonPlugin>()
}

val kotlinLoggingVersion: String by extra

dependencies {
  "compile"("io.github.microutils:kotlin-logging-common:$kotlinLoggingVersion")
}

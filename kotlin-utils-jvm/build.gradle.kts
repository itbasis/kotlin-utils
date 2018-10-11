import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

apply {
  plugin<KotlinPlatformJvmPlugin>()
}

val kotlinLoggingVersion: String by project

dependencies {
  "implementation"(kotlin("stdlib-jdk8"))

  "compile"("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
}

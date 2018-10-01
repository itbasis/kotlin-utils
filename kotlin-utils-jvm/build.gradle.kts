import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

apply {
  plugin<KotlinPlatformJvmPlugin>()
}

val kloggingVersion: String by project

dependencies {
  "implementation"(kotlin("stdlib-jdk8"))

  "compile"("com.github.lewik.klogging:klogging.jvm:$kloggingVersion")
}

import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin

apply {
  plugin<KotlinPlatformJsPlugin>()
}

val kloggingVersion: String by project

dependencies {
  "compile"("com.github.lewik.klogging:klogging.js:$kloggingVersion")
}

import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin

apply {
  plugin<KotlinPlatformCommonPlugin>()
}

val kloggingVersion: String by project

dependencies {
  "compile"("com.github.lewik.klogging:klogging.common:$kloggingVersion")
}

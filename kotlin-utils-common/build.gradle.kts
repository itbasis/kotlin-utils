import com.jfrog.bintray.gradle.BintrayPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin

apply {
  plugin<KotlinPlatformCommonPlugin>()
  plugin<BintrayPlugin>()
}

val kloggingVersion: String by project

dependencies {
  "compile"("com.github.lewik.klogging:klogging.common:$kloggingVersion")
}

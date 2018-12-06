import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin

apply {
  plugin<KotlinPlatformCommonPlugin>()
}

val kotlinVersion = rootProject.extra["kotlin.version"] as String
val klogVersion = rootProject.extra["klog.version"] as String

dependencies {
  "compile"(kotlin("stdlib-common", kotlinVersion))

  "compile"("com.github.lewik.klog:klog-metadata:$klogVersion")

  "testImplementation"(kotlin("test-common", kotlinVersion))
  "testImplementation"(kotlin("test-annotations-common", kotlinVersion))
}

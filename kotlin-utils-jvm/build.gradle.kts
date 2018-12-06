import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

apply {
  plugin<KotlinPlatformJvmPlugin>()
}

val kotlinVersion = extra["kotlin.version"] as String
val klogVersion = extra["klog.version"] as String
val kotlintestVersion = extra["kotlintest.version"] as String
val slf4jVersion = extra["slf4j.version"] as String
val junitJupiterVersion = extra["junit.jupiter.version"] as String

dependencies {
  "implementation"(kotlin("stdlib-jdk8", kotlinVersion))
  "implementation"(kotlin("reflect", kotlinVersion))

  "implementation"("com.github.lewik.klog:klog-jvm:$klogVersion")

  "testImplementation"("org.slf4j:slf4j-simple:$slf4jVersion")
  "testImplementation"(kotlin("test-junit5", kotlinVersion))
  "testImplementation"("io.kotlintest:kotlintest-extensions-system:$kotlintestVersion")
  "testImplementation"("io.kotlintest:kotlintest-assertions-arrow:$kotlintestVersion")
  "testImplementation"("io.kotlintest:kotlintest-runner-junit5:$kotlintestVersion")
  "testImplementation"("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
  "testImplementation"("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

apply {
	plugin<KotlinPlatformJvmPlugin>()
}

val kloggingVersion: String by project

dependencies {
	"implementation"(kotlin("stdlib-jdk8"))

	"compile"(group = "com.github.lewik.klogging", name = "klogging.jvm", version = kloggingVersion) {
		exclude(group = "com.github.lewik")
	}
}

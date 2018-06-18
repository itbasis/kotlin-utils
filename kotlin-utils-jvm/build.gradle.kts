import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

apply {
	plugin<KotlinPlatformJvmPlugin>()
}

dependencies {
	"implementation"(kotlin("stdlib-jdk8"))
}

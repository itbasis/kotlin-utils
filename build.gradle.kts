import com.github.breadmoirai.GithubReleaseExtension
import com.github.breadmoirai.GithubReleasePlugin
import com.github.breadmoirai.GithubReleaseTask
import com.gradle.scan.plugin.BuildScanExtension
import io.gitlab.arturbosch.detekt.DetektCheckTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DEFAULT_PROFILE_NAME
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformPluginBase
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME

tasks.withType<Wrapper> {
	distributionType = Wrapper.DistributionType.BIN
	gradleVersion = "4.8.1"
}

buildscript {
	val kotlinVersion: String by extra

	repositories {
		jcenter()
		gradlePluginPortal()
	}

	dependencies {
		classpath(kotlin("gradle-plugin", kotlinVersion))
		classpath("gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:latest.release")
		classpath("gradle.plugin.org.jlleitschuh.gradle:ktlint-gradle:latest.release")
		classpath("gradle.plugin.com.github.breadmoirai:github-release:latest.release")
	}
}

plugins {
	`build-scan`
}

apply {
	plugin<IdeaPlugin>()
	plugin<MavenPlugin>()
	plugin<MavenPublishPlugin>()
	plugin<GithubReleasePlugin>()
}

configure<BuildScanExtension> {
	setTermsOfServiceUrl("https://gradle.com/terms-of-service")
	setTermsOfServiceAgree("yes")

	if (!java.lang.System.getenv("CI").isNullOrEmpty()) {
		publishAlways()
		tag("CI")
	}
}

version =
	if (version != "unspecified") version else file("versions.txt").readLines().first().substringAfter(
		"="
	)

subprojects {
	version = rootProject.version

	apply {
		from("$rootDir/gradle/dependencies.gradle.kts")
		plugin<BasePlugin>()
		plugin<DetektPlugin>()
	}

	configure<DetektExtension> {
		this.profile(DEFAULT_PROFILE_NAME, Action {
			config = rootDir.resolve("detekt-config.yml")
		})
	}

	afterEvaluate {
		plugins.withType(JavaBasePlugin::class.java) {
			configure<JavaPluginConvention> {
				sourceCompatibility = JavaVersion.VERSION_1_8
			}
			tasks.withType(DetektCheckTask::class.java) {
				dependsOn(tasks.withType(Test::class.java))
				tasks.getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn(this)
			}
		}

		tasks.withType(KotlinCompile::class.java) {
			kotlinOptions {
				jvmTarget = JavaVersion.VERSION_1_8.toString()
			}
		}

		plugins.withType(KotlinPlatformPluginBase::class.java) {
			configure<KotlinProjectExtension> {
				experimental.coroutines = Coroutines.ENABLE
			}
			rootProject.configure<PublishingExtension> {
				publications {
					create(project.name, MavenPublication::class.java) {
						artifactId = project.name
						from(project.components["java"])
					}
				}
			}
		}
		plugins.withType(KotlinPlatformCommonPlugin::class.java) {
			dependencies {
				"implementation"(kotlin("stdlib-common"))

				arrayOf(kotlin("test-common"), kotlin("test-annotations-common")).forEach {
					"testImplementation"(it)
				}
			}
		}
		plugins.withType(KotlinPlatformJvmPlugin::class.java) {
			dependencies {
				"implementation"(kotlin("stdlib-jdk8"))
				"implementation"(kotlin("reflect"))

				arrayOf(
					"org.slf4j:slf4j-simple",
					kotlin("test-junit"),
					"io.kotlintest:kotlintest-extensions-system",
					"io.kotlintest:kotlintest-assertions-arrow",
					"io.kotlintest:kotlintest-runner-junit4"
				).forEach {
					"testImplementation"(it)
				}
			}
		}
		plugins.withType(KotlinPlatformJsPlugin::class.java) {
			dependencies {
				"implementation"(kotlin("stdlib-js"))

				"testImplementation"(kotlin("test-js"))
			}
		}

		tasks.withType(Test::class.java).all {
			failFast = true
			useJUnit()
			testLogging {
				showStandardStreams = true
			}
		}
	}

	rootProject.tasks[BUILD_TASK_NAME].shouldRunAfter(tasks[BUILD_TASK_NAME])
}

configure<GithubReleaseExtension> {
	if (hasProperty("githubToken")) setToken(findProperty("githubToken") as String)
	setOwner("itbasis")
}

tasks.create("generateVersion").apply {
	group = LifecycleBasePlugin.BUILD_GROUP
	tasks[ASSEMBLE_TASK_NAME].shouldRunAfter(this)
	doLast {
		version = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
		subprojects.forEach { it.version = version }
		file("versions.txt").writeText("version=$version")
	}
}
tasks[BUILD_TASK_NAME].apply {
	doLast {
		subprojects.forEach { subProject ->
			logger.lifecycle("subProject: '${subProject.group}:${subProject.name}:${subProject.version}'")
			copy {
				from("${subProject.buildDir}/libs")
				into("$buildDir/libs")
			}
		}
	}
}
tasks.withType(GithubReleaseTask::class.java) {
	//  setReleaseAssets(buildDir.resolve("libs").listFiles())
}

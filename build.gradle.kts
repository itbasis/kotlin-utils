import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.publish.maven.MavenPom
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
import org.gradle.plugins.ide.idea.model.IdeaModel

buildscript {
  val kotlinVersion = extra["kotlin.version"] as String

  repositories {
    jcenter()
    gradlePluginPortal()
  }

  dependencies {
    classpath(kotlin("gradle-plugin", kotlinVersion))
    classpath("gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:+")
  }
}

apply {
  plugin<MavenPlugin>()
  plugin<MavenPublishPlugin>()
}

version = when (version) {
  "unspecified" -> rootDir.resolve("versions.txt").readLines().first().substringAfter("=")
  else          -> version
}

allprojects {
  group = "ru.itbasis.kotlin.utils"

  apply {
    plugin<IdeaPlugin>()
  }

  configure<IdeaModel> {
    module {
      isDownloadJavadoc = false
      isDownloadSources = false
    }
  }

  repositories {
    mavenLocal()
    jcenter()
    maven(url = "https://jitpack.io")
  }

  configurations.all {
    resolutionStrategy {
      failOnVersionConflict()

      eachDependency {
        when (requested.group) {
          "org.jetbrains.kotlin" -> useVersion(extra["kotlin.version"] as String)
          "org.junit.jupiter"    -> useVersion(extra["junit.jupiter.version"] as String)
          "org.junit.platform"   -> useVersion(extra["junit.platform.version"] as String)
        }
      }
    }
  }
}

subprojects {
  version = rootProject.version

  apply {
    plugin<BasePlugin>()
    plugin<DetektPlugin>()
    plugin<MavenPublishPlugin>()
  }

  afterEvaluate {
    plugins.withType(JavaBasePlugin::class.java) {
      configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
      }
    }

    tasks.withType(KotlinCompile::class.java) {
      kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
      }
    }
    tasks.withType(Test::class.java).all {
      failFast = true
      useJUnitPlatform {
        includeEngines("spek")
      }
      testLogging {
        showStandardStreams = true
      }
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

  rootProject.tasks[BUILD_TASK_NAME].shouldRunAfter(tasks[BUILD_TASK_NAME])
}

tasks.create("generateAllPomFiles") {
  group = PublishingExtension.NAME
  dependsOn(tasks.withType(GenerateMavenPom::class.java))
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

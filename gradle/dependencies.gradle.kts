import org.gradle.plugins.ide.idea.model.IdeaModel

val kotlinVersion: String by extra
val kotlinLoggingVersion: String by extra

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
        "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
        "io.github.microutils" -> useVersion(kotlinLoggingVersion)

        "org.slf4j"            -> useVersion("+")
        "junit"                -> useVersion("4.+")
        "io.kotlintest"        -> useVersion("+")
        "org.junit.platform"   -> useVersion("+")
        "org.opentest4j"       -> useVersion("+")
        "org.junit.jupiter"    -> useVersion("+")
      }
    }
  }
}

apply {
  plugin<IdeaPlugin>()
}

configure<IdeaModel> {
  module {
    isDownloadJavadoc = false
    isDownloadSources = false
  }
}

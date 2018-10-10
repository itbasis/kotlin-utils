import org.gradle.plugins.ide.idea.model.IdeaModel

val kotlinVersion: String by extra
val junit5PlatformVersion: String by extra

repositories {
  mavenLocal()
  jcenter()
  maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
  maven(url = "https://jitpack.io")
}

configurations.all {
  resolutionStrategy {
    failOnVersionConflict()

    eachDependency {
      when (requested.group) {
        "org.jetbrains.kotlin"      -> useVersion(kotlinVersion)
        "org.slf4j"                 -> useVersion("1.+")
        "junit"                     -> useVersion("4.12")
        "io.kotlintest"             -> useVersion("3.1.6")
        "org.junit.platform"        -> useVersion(junit5PlatformVersion)
        """org.opentest4j"""        -> useVersion("1.1.0")
        "org.junit.jupiter"         -> useVersion("5.2.0")
        "com.github.lewik.klogging" -> useVersion("+")
        "com.github.lewik"          -> useTarget("com.github.lewik.klogging:${requested.name}:${requested.version}")

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

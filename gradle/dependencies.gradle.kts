import org.gradle.plugins.ide.idea.model.IdeaModel

val kotlinVersion: String by extra

repositories {
	mavenLocal()
	jcenter()
	mavenCentral()
	maven(url = "https://jitpack.io")
}

configurations.all {
	resolutionStrategy {
		failOnVersionConflict()

		eachDependency {
			when (requested.group) {
				"org.jetbrains.kotlin" -> useVersion(kotlinVersion)
				"org.slf4j" -> useVersion("1.7.25")
				"junit" -> useVersion("4.12")
				"io.kotlintest" -> useVersion("3.1.6")
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

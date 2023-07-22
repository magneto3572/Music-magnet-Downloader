pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://jcenter.bintray.com")
        }
        maven {
            url = uri("https://maven.google.com/")
        }
    }
}
rootProject.name = "Music Magnet"
include (":app")
include (":ytdlplibrary")
include (":aria2c")

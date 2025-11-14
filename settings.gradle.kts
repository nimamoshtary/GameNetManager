pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven {
            url = uri("https://maven-central.storage.apis.com")
        }
        ivy {
            url = uri("https://github.com/ivy-rep/")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "GameNetHelper"
include(":app")
 
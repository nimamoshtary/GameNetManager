pluginManagement {
    repositories {
        //google()
        maven { url = uri("https://maven.myket.ir") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("android.*")
            }
        }

        mavenCentral()
        gradlePluginPortal()
        ivy {
            url = uri("https://github.com/ivy-rep/")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.myket.ir") }
        google()
        mavenCentral()
    }
}

rootProject.name = "GameNetHelper"
include(":app")
 
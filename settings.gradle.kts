pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://cardinalcommerceprod.jfrog.io/artifactory/android") {
            credentials {
                username = extra["palpalUsername"].toString()
                password = extra["paypalPassword"].toString()
            }
        }
        maven(url = "https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://cardinalcommerceprod.jfrog.io/artifactory/android") {
            credentials {
                username = extra["palpalUsername"].toString()
                password = extra["paypalPassword"].toString()
            }
        }
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "I Cash"
include(":app")
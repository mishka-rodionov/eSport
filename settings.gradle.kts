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
    }
//    versionCatalogs {
//        create("libs") {
//            from(files("../gradle/libs.versions.toml"))
//        }
//    }
}

rootProject.name = "Sports Enthusiast"
include(":app")
include(":domain")
include(":data")
include(":feature:profile")
include(":feature:news")

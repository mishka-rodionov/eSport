pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
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
include(":data:navigation")
include(":feature:profile")
include(":feature:news")
include(":feature:center")
include(":core:designsystem")
include(":data:local")
include(":data:remote")
include(":data:remote")
include(":data")
include(":core:resources")
include(":utils")

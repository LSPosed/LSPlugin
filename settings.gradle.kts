rootProject.name = "LSPlugin"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
    }
}
include(":publish", ":jgit", ":resopt", ":apksign", ":apktransform", ":cmaker")

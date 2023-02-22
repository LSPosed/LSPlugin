rootProject.name = "LSPlugin"

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
    versionCatalogs {
        create("libs") {
            library("jgit", "org.eclipse.jgit:org.eclipse.jgit:6.4.0.202211300538-r")
            library("agp", "com.android.tools.build:gradle-api:7.4.1")
        }
    }
}
include(":publish")
include(":jgit", ":resopt", ":apksign", ":apktransform", ":cmaker")

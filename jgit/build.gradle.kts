plugins {
    kotlin("jvm")
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

version = "1.0"

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.jgit)
}

publish {
    githubRepo = "LSPosed/LSPlugin"
    publishPlugin("$group.jgit", "jgit", "org.lsposed.lsplugin.JGitPlugin") {
        name.set("JGit")
        description.set("Git plugin for Gradle")
        url.set("https://github.com/LSPosed/LSPlugin")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://github.com/LSPosed/LSPlugin/blob/master/LICENSE.txt")
            }
        }
        developers {
            developer {
                name.set("LSPosed")
                url.set("https://lsposed.org")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/LSPosed/LSPlugin.git")
            url.set("https://github.com/LSPosed/LSPlugin")
        }
    }
}

plugins {
    kotlin("jvm")
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

version = "1.2"

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.agp)
}

publish {
    githubRepo = "LSPosed/LSPlugin"
    publishPlugin("apktransform", "org.lsposed.lsplugin.ApktransformPlugin") {
        name = "ApkTransform"
        description = "Transform apk"
        url = "https://github.com/LSPosed/LSPlugin"
        licenses {
            license {
                name = "Apache License 2.0"
                url = "https://github.com/LSPosed/LSPlugin/blob/master/LICENSE.txt"
            }
        }
        developers {
            developer {
                name = "LSPosed"
                url = "https://lsposed.org"
            }
        }
        scm {
            connection = "scm:git:https://github.com/LSPosed/LSPlugin.git"
            url = "https://github.com/LSPosed/LSPlugin"
        }
    }
}

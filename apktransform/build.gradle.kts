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

group = "org.lsposed.lsplugin.apktransform"
version = "1.0"

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.agp)
}

publish {
    githubRepo = "LSPosed/LSPlugin"
    publishPlugin("apktransform", "org.lsposed.lsplugin.ApktransformPlugin") {
        description.set("Transform apk")
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

plugins {
    kotlin("jvm")
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    signing
//    alias(libs.plugins.maven.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

version = "1.3"

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.agp)
    implementation(libs.maven.publish)
}

publish {
    githubRepo = "LSPosed/LSPlugin"
    publishPlugin("publish", "org.lsposed.lsplugin.PublishPlugin") {
        name = "Publish"
        description = "Android resources optimizer plugin"
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

//mavenPublishing {
//    coordinates("com.example.mylibrary", "library-name", "1.0.3-SNAPSHOT")
//
//    pom {
//        name = "My Library"
//        description = "A description of what my library does."
//        inceptionYear = "2020"
//        url = "https://github.com/username/mylibrary/"
//        licenses {
//            license {
//                name = "The Apache License, Version 2.0"
//                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
//                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
//            }
//        }
//        developers {
//            developer {
//                id = "username"
//                name = "User Name"
//                url = "https://github.com/username/"
//            }
//        }
//        scm {
//            url = "https://github.com/username/mylibrary/"
//            connection = "scm:git:git://github.com/username/mylibrary.git"
//            developerConnection = "scm:git:ssh://git@github.com/username/mylibrary.git"
//        }
//    }
//}
//


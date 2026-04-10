plugins {
    `kotlin-dsl`
    signing
}

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("LSPlugin") {
            id = "LSPlugin"
            implementationClass = "org.lsposed.lsplugin.PublishPlugin"
        }
    }
}

sourceSets {
    main {
        kotlin {
            srcDir("../publish/src/main/kotlin")
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.36.0")
}

signing {
    setRequired {
        !gradle.taskGraph.allTasks.any { it is PublishToMavenLocal }
    }
}

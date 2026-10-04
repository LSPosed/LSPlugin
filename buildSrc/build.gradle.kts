plugins {
    `kotlin-dsl`
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
    // Must stay equal to `maven-publish` in gradle/libs.versions.toml.
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.37.0")
}

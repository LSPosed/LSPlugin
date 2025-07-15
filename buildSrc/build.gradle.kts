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
    implementation(kotlin("gradle-plugin", "2.0.21"))
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
}

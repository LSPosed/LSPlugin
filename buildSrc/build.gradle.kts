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
    implementation(kotlin("gradle-plugin", "1.9.23"))
}

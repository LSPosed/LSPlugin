package org.lsposed.lsplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.credentials
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin

inline fun Project.configRepository(crossinline setup: MavenArtifactRepository.() -> Unit) {
    plugins.withType(MavenPublishPlugin::class.java) {
        extensions.configure(PublishingExtension::class.java) {
            repositories {
                maven {
                    setup()
                }
            }
        }
    }
}

open class PublishExtensionImpl(private val project: Project) : PublishExtension {
    override var githubRepo: String? = null
        set(value) {
            field = value
            project.configRepository {
                name = "GithubPackages"
                url = project.uri("https://maven.pkg.github.com/$githubRepo")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }


    override fun publications(action: PublicationContainer.() -> Unit) {
        project.run {
            plugins.withType(MavenPublishPlugin::class.java) {
                extensions.configure(PublishingExtension::class.java) {
                    publications {
                        action()
                    }
                }
            }
        }
    }

    override fun publishPlugin(id: String, name: String, implementationClass: String, action: MavenPom.() -> Unit) {
        project.run {
            plugins.withType(JavaGradlePluginPlugin::class.java) {
                extensions.configure(GradlePluginDevelopmentExtension::class.java) {
                    plugins {
                        register(name) {
                            this@register.id = id
                            this@register.implementationClass = implementationClass
                        }
                    }
                }
            }
        }
        project.afterEvaluate {
            plugins.withType(JavaGradlePluginPlugin::class.java) {
                extensions.configure(PublishingExtension::class.java) {
                    publications {
                        named<MavenPublication>("pluginMaven") {
                            pom {
                                action()
                            }
                        }
                        named<MavenPublication>("${name}PluginMarkerMaven") {
                            pom {
                                action()
                            }
                        }
                    }
                }
            }
        }
    }
}

class PublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.subprojects {
            extensions.create(PublishExtensionImpl::class.java, "publish", PublishExtensionImpl::class.java, this)
            plugins.withType(SigningPlugin::class.java) {
                extensions.configure(SigningExtension::class.java) {
                    val signingKey = findProperty("signingKey") as String?
                    val signingPassword = findProperty("signingPassword") as String?
                    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
                        useInMemoryPgpKeys(signingKey, signingPassword)
                        plugins.withType(MavenPublishPlugin::class.java) {
                            extensions.configure(PublishingExtension::class.java) {
                                sign(publications)
                            }
                        }
                    }
                }
            }
            plugins.withType(MavenPublishPlugin::class.java) {
                extensions.configure(PublishingExtension::class.java) {
                    configRepository {
                        name = "CentralPortal"
                        url = uri("https://central.sonatype.com/api/v1/publisher/upload")
                        credentials(PasswordCredentials::class)
                    }
                }
            }
            plugins.withType(JavaPlugin::class.java) {
                extensions.configure(JavaPluginExtension::class.java) {
                    withSourcesJar()
                    withJavadocJar()
                }
            }
        }
    }
}

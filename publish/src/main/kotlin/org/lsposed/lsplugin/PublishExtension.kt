package org.lsposed.lsplugin

import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication

sealed interface PublishExtension {
    var githubRepo: String?
    fun publications(action: PublicationContainer.() -> Unit)
    fun publishPlugin(id: String, name: String, implementationClass: String, action: MavenPom.() -> Unit)
}

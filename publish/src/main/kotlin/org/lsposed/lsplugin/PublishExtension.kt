package org.lsposed.lsplugin

import org.gradle.api.Action
import org.gradle.api.publish.maven.MavenPom

sealed interface PublishExtension {
    var githubRepo: String?
    fun publications(artifactId: String, action: Action<in MavenPom>)
    fun publishPlugin( artifactId: String, implementationClass: String, action: Action<in MavenPom>)
}

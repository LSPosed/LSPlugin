package org.lsposed.lsplugin

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import java.io.File

private class JRepoImpl(
    override val raw: Repository,
    private val gitDir: File,
    private val providers: ProviderFactory,
) : JGitExtension.JRepo {
    override val git: Git
        get() = Git(raw)

    override fun version(ref: String): Provider<GitVersion> =
        providers.of(GitVersionSource::class.java) {
            parameters.gitDirectory.set(gitDir.absolutePath)
            parameters.ref.set(ref)
        }
}

private open class JGitExtensionImpl(private val project: Project) : JGitExtension {
    override fun repo(fromRootProject: Boolean): JGitExtension.JRepo? {
        val builder = FileRepositoryBuilder().apply {
            project.file(".git").run {
                findGitDir(if (exists()) this else if (fromRootProject) project.rootProject.file(".git") else null)
            }
        }
        return runCatching { builder.build() }.getOrNull()?.let { raw ->
            JRepoImpl(raw, builder.gitDir, project.providers)
        }
    }
}

class JGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(JGitExtension::class.java, "jgit", JGitExtensionImpl::class.java, project)
    }
}

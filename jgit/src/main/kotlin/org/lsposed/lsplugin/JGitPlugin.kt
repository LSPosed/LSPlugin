package org.lsposed.lsplugin

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

private class JRepoImpl(override val raw: Repository) : JGitExtension.JRepo {
    override val git: Git
        get() = Git(raw)

    override fun commitCount(ref: String): Int? = runCatching {
        git.log().add(raw.resolve(ref)).call().count()
    }.getOrNull()

    override val latestTag: String? = runCatching {
        git.describe().setTags(true).setAbbrev(0).call()
    }.getOrNull()
}

private open class JGitExtensionImpl(private val project: Project) : JGitExtension {
    override fun repo(fromRootProject: Boolean): JGitExtension.JRepo? {
        val builder = FileRepositoryBuilder().apply {
            project.file(".git").run {
                findGitDir(if (exists()) this else if (fromRootProject) project.rootProject.file(".git") else null)
            }
        }
        return runCatching { JRepoImpl(builder.build()) }.getOrNull()
    }
}

class JGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(JGitExtension::class.java, "jgit", JGitExtensionImpl::class.java, project)
    }
}

package org.lsposed.lsplugin

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input
import java.io.File
import java.io.Serializable

/**
 * Reads the repository for one [ValueSourceParameters] pair. Gradle caches the value per project,
 * so a build with several modules asking for the same values reads the repository once per module
 * rather than once per call site; making it build-wide needs a shared build service, which breaks
 * across the classloader boundary a `build-logic` included build introduces.
 */
internal abstract class GitVersionSource : ValueSource<GitVersion, GitVersionSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        @get:Input
        val gitDirectory: Property<String>

        @get:Input
        val ref: Property<String>
    }

    override fun obtain(): GitVersion = runCatching {
        FileRepositoryBuilder().setGitDir(File(parameters.gitDirectory.get())).build().use { raw ->
            val git = Git(raw)
            GitVersion(
                commitCount = runCatching {
                    raw.resolve(parameters.ref.get())?.let { git.log().add(it).call().count() }
                }.getOrNull(),
                latestTag = runCatching {
                    git.describe().setTags(true).setAbbrev(0).call()
                }.getOrNull(),
            )
        }
    }.getOrElse { GitVersion(null, null) }
}

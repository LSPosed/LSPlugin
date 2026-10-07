package org.lsposed.lsplugin

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.gradle.api.provider.Provider

sealed interface JGitExtension {
    sealed interface JRepo {
        val git: Git

        val raw: Repository

        /**
         * Commit count and nearest tag of this repository, obtained once per build no matter how many
         * projects ask for it, and staying lazy until something consumes it.
         *
         * A field is `null` when it cannot be read: no such [ref], no tag at all, or a repository
         * that jgit refuses to open.
         */
        fun version(ref: String = "HEAD"): Provider<GitVersion>
    }

    fun repo(fromRootProject: Boolean = true): JRepo?
}

/** What [JGitExtension.JRepo.version] resolves to; `null` fields mean "could not be read". */
data class GitVersion(val commitCount: Int?, val latestTag: String?)

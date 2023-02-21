package org.lsposed.lsplugin

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository

sealed interface JGitExtension {
    sealed interface JRepo {
        val git: Git

        val raw: Repository

        fun commitCount(ref: String): Int?
        val latestTag: String?
    }

    fun repo(fromRootProject: Boolean = true): JRepo?
}

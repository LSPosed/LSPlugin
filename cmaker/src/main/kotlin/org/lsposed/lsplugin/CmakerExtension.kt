package org.lsposed.lsplugin

import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.ExternalNativeCmakeOptions

interface CmakerExtension {
    fun default(action: ExternalNativeCmakeOptions.() -> Unit)
    fun buildTypes(action: ExternalNativeCmakeOptions.(BuildType) -> Unit)
}

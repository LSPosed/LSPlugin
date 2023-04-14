package org.lsposed.lsplugin

import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CmakeFlags

interface CmakerExtension {
    fun default(action: CmakeFlags.() -> Unit)
    fun buildTypes(action: CmakeFlags.(BuildType) -> Unit)
}

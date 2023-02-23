package org.lsposed.lsplugin

import com.android.build.api.variant.BuiltArtifact
import com.android.build.api.variant.Variant
import java.io.File

sealed interface ApktransformExtension {
    fun transform(action: (Variant) -> ((BuiltArtifact) -> File?)?)

    fun copy(action: (Variant) -> File?)
}

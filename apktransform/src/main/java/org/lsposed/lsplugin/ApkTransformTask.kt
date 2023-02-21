package org.lsposed.lsplugin

import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.variant.BuiltArtifact
import com.android.build.api.variant.Variant
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class ApkTransformTask @Inject constructor(
    private val variant: Variant,
    private val action: Variant.(BuiltArtifact) -> File?
) :
    DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val apkFolder: DirectoryProperty

    @get:OutputDirectory
    abstract val outFolder: DirectoryProperty

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<ApkTransformTask>>

    @TaskAction
    fun taskAction() = transformationRequest.get().submit(this) { artifact ->
        variant.action(artifact) ?: File(artifact.outputFile)
    }
}

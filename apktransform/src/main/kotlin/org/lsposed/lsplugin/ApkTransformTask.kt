package org.lsposed.lsplugin

import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.variant.BuiltArtifact
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "The transformation is a user-supplied lambda, and its behaviour is not declared as an input")
abstract class ApkTransformTask @Inject constructor(
    private val action: (BuiltArtifact) -> File?
) : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val apkFolder: DirectoryProperty

    @get:OutputDirectory
    abstract val outFolder: DirectoryProperty

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<ApkTransformTask>>

    @TaskAction
    fun taskAction() = transformationRequest.get().submit(this) { artifact ->
        action(artifact) ?: File(artifact.outputFile)
    }
}

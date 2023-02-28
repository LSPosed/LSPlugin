package org.lsposed.lsplugin

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

interface Injected {
    @get:Inject
    val fs: FileSystemOperations
}

private open class ApktransformExtensionImpl(private val project: Project) : ApktransformExtension {
    override fun transform(action: (Variant) -> ((BuiltArtifact) -> File?)?) {
        project.plugins.withType(AndroidBasePlugin::class.java) {
            project.extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
                onVariants { variant ->
                    val transform = action(variant) ?: return@onVariants
                    variant.registerTransform(
                        project.tasks.register(
                            "transform${variant.name.replaceFirstChar { it.uppercase() }}Apk",
                            ApkTransformTask::class.java,
                            transform
                        )
                    )
                }
            }
        }
    }

    override fun copy(action: (Variant) -> File?) {
        project.plugins.withType(AndroidBasePlugin::class.java) {
            project.extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
                onVariants { variant ->
                    val outFile = action(variant) ?: return@onVariants
                    val variantName = variant.name
                    val inject = project.objects.newInstance(Injected::class.java)
                    val copyAction: (BuiltArtifact) -> File = { artifact ->
                        inject.fs.copy {
                            from(artifact.outputFile)
                            into(outFile.parentFile)
                            rename { outFile.name }
                        }
                        outFile
                    }
                    variant.registerTransform(
                        project.tasks.register(
                            "copy${variantName.replaceFirstChar { it.uppercase() }}Apk",
                            ApkTransformTask::class.java,
                            copyAction
                        )
                    )
                }
            }
        }
    }

    private fun Variant.registerTransform(task: TaskProvider<ApkTransformTask>) {
        val transformationRequest = artifacts.use(task)
            .wiredWithDirectories(ApkTransformTask::apkFolder, ApkTransformTask::outFolder)
            .toTransformMany(SingleArtifact.APK)
        task.configure {
            this.transformationRequest.set(transformationRequest)
        }
    }

}

class ApktransformPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            ApktransformExtension::class.java,
            "apktransform",
            ApktransformExtensionImpl::class.java,
            project
        )
    }
}

package org.lsposed.lsplugin

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File


private open class ApktransformExtensionImpl(private val project: Project) : ApktransformExtension {
    override fun transform(action: Variant.(BuiltArtifact) -> File?) {
        project.plugins.withType(AndroidBasePlugin::class.java) {
            project.extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
                onVariants { variant ->
                    variant.registerTransform(
                        project.tasks.register(
                            "transform${variant.name.replaceFirstChar { it.uppercase() }}Apk",
                            ApkTransformTask::class.java,
                            variant,
                            action
                        )
                    )
                }
            }
        }
    }

    override fun copy(action: Variant.() -> File?) {
        project.plugins.withType(AndroidBasePlugin::class.java) {
            project.extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
                onVariants { variant ->
                    val outFile = action(variant)
                    if (outFile != null) {
                        val copyAction: Variant.(BuiltArtifact) -> File = { artifact ->
                            outFile.parentFile.mkdirs()
                            File(artifact.outputFile).copyTo(outFile, true)
                        }
                        variant.registerTransform(
                            project.tasks.register(
                                "copy${variant.name.replaceFirstChar { it.uppercase() }}Apk",
                                ApkTransformTask::class.java,
                                variant,
                                copyAction
                            )
                        )
                    }
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

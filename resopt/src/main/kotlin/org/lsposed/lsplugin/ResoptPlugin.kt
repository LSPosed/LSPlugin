package org.lsposed.lsplugin

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.file.Paths

class ResoptPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AndroidBasePlugin::class.java) {
            project.extensions.configure(AndroidComponentsExtension::class.java) {
                onVariants { variant ->
                    if (variant.buildType != "release") return@onVariants
                    val ext = project.extensions.getByType(CommonExtension::class.java)
                    val aapt2 = Paths.get(
                        sdkComponents.sdkDirectory.get().toString(), "build-tools", ext.buildToolsVersion, "aapt2"
                    )
                    val name = variant.name.replaceFirstChar { c -> c.uppercase() }
                    val zip = Paths.get(
                        project.buildDir.path,
                        "intermediates",
                        "optimized_processed_res",
                        name,
                        "resources-release-optimize.ap_"
                    )
                    val optimized = File("${zip}.opt")
                    project.afterEvaluate {
                        tasks.getByPath("optimize${name}Resources").doLast {
                            val cmd = exec {
                                commandLine(aapt2, "optimize", "--collapse-resource-names", "-o", optimized, zip)
                                isIgnoreExitValue = true
                            }
                            if (cmd.exitValue == 0) {
                                delete(zip)
                                optimized.renameTo(zip.toFile())
                            } else {
                                println("Failed to optimize resources for variant ${variant.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}

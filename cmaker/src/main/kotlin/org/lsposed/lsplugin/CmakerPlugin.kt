package org.lsposed.lsplugin

import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.CmakeFlags
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import java.io.File
import java.nio.file.Paths

fun Project.findInPath(executable: String, property: String): String? {
    val pathEnv = System.getenv("PATH")
    return pathEnv.split(File.pathSeparator).map { folder ->
        Paths.get("${folder}${File.separator}${executable}${if (OperatingSystem.current().isWindows) ".exe" else ""}")
            .toFile()
    }.firstOrNull { path ->
        path.exists()
    }?.absolutePath ?: properties.getOrDefault(property, null) as? String?
}

private open class CmakerExtensionImpl(private val project: Project) : CmakerExtension {
    val ccachePatch by lazy {
        project.findInPath("ccache", "ccache.path")?.also {
            println("Use ccache: $it")
        }
    }

    override fun default(action: CmakeFlags.() -> Unit) {
        val flags = arrayOf(
            "-Wall",
            "-Qunused-arguments",
            "-fno-rtti",
            "-fvisibility=hidden",
            "-fvisibility-inlines-hidden",
            "-fno-exceptions",
            "-fno-stack-protector",
            "-fomit-frame-pointer",
            "-Wno-builtin-macro-redefined",
            "-Wno-unused-value",
            "-D__FILE__=__FILE_NAME__",
        )
        project.default {
            cppFlags("-std=c++2b", *flags)
            cFlags("-std=c2x", *flags)
            ccachePatch?.let {
                arguments += "-DANDROID_CCACHE=$it"
            }
            action()
        }
    }

    fun Project.default(action: CmakeFlags.() -> Unit) {
        plugins.withType(AndroidBasePlugin::class.java) {
            extensions.configure(CommonExtension::class.java) {
                defaultConfig {
                    externalNativeBuild {
                        cmake {
                            action()
                        }
                    }
                }
            }
        }
        subprojects {
            this.default(action)
        }
    }

    override fun buildTypes(action: CmakeFlags.(BuildType) -> Unit) {
        val flags = arrayOf(
            "-flto",
            "-ffunction-sections",
            "-fdata-sections",
            "-Wl,--gc-sections",
            "-fno-unwind-tables",
            "-fno-asynchronous-unwind-tables",
            "-Wl,--exclude-libs,ALL",
        )
        val configFlags = arrayOf(
            "-Oz", "-DNDEBUG"
        ).joinToString(" ")
        project.buildTypes {
            if (it.name == "debug") {
                arguments.addAll(
                    arrayOf(
                        "-DCMAKE_CXX_FLAGS_DEBUG=-Og",
                        "-DCMAKE_C_FLAGS_DEBUG=-Og",
                    )
                )
            } else if (it.name == "release") {
                cppFlags.addAll(flags)
                cFlags.addAll(flags)
                arguments(
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DCMAKE_CXX_FLAGS_RELEASE=$configFlags",
                    "-DCMAKE_C_FLAGS_RELEASE=$configFlags",
                )
            }
            action(it)
        }
    }

    fun Project.buildTypes(action: CmakeFlags.(BuildType) -> Unit) {
        plugins.withType(AndroidBasePlugin::class.java) {
            extensions.configure(CommonExtension::class.java) {
                buildTypes {
                    all {
                        externalNativeBuild {
                            cmake {
                                action(this@all)
                            }
                        }
                    }
                }
            }
        }
        subprojects {
            this.buildTypes(action)
        }
    }
}

class CmakerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(CmakerExtension::class.java, "cmaker", CmakerExtensionImpl::class.java, project)
        project.subprojects {
            extensions.create(CmakerExtension::class.java, "cmaker", CmakerExtensionImpl::class.java, this)
        }
    }
}

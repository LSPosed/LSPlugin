package org.lsposed.lsplugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

private open class ApksignExtensionImpl(private val project: Project) : ApksignExtension {
    private var storeFile: File? = null
    private var storePassword: String? = null
    private var keyAlias: String? = null
    private var keyPassword: String? = null
    private var config = false
    override var storeFileProperty: String? = null
        set(value) {
            field = value
            storeFile = (project.properties.getOrDefault(value, null) as? String?)?.let { project.rootProject.file(it) }
            maybeConfig()
        }
    override var storePasswordProperty: String? = null
        set(value) {
            field = value
            storePassword = project.properties.getOrDefault(value, null) as? String?
            maybeConfig()
        }
    override var keyAliasProperty: String? = null
        set(value) {
            field = value
            keyAlias = project.properties.getOrDefault(value, null) as? String?
            maybeConfig()
        }
    override var keyPasswordProperty: String? = null
        set(value) {
            field = value
            keyPassword = project.properties.getOrDefault(value, null) as? String?
            maybeConfig()
        }

    private fun maybeConfig() {
        if (storeFileProperty == null || storePasswordProperty == null || keyAliasProperty == null || keyPasswordProperty == null || config) return
        config = true
        project.plugins.withType(AndroidBasePlugin::class.java) {
            project.extensions.configure(ApplicationExtension::class.java) {
                if (storeFile?.exists() == true) {
                    signingConfigs {
                        val sign = create("apksign") {
                            storeFile = this@ApksignExtensionImpl.storeFile
                            storePassword = this@ApksignExtensionImpl.storePassword
                            keyAlias = this@ApksignExtensionImpl.keyAlias
                            keyPassword = this@ApksignExtensionImpl.keyPassword
                        }
                        buildTypes {
                            all {
                                signingConfig = sign
                            }
                        }
                    }
                } else {
                    println("Apksign fallbacks to use debug singature as ${storeFile?.absolutePath} does not exist")
                    buildTypes {
                        all {
                            signingConfig = signingConfigs.getByName("debug")
                        }
                    }
                }
            }
        }
    }
}

class ApksignPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(ApksignExtension::class.java, "apksign", ApksignExtensionImpl::class.java, project)
    }
}

package org.lsposed.lsplugin

sealed interface ApksignExtension {
    var storeFileProperty: String?
    var storePasswordProperty: String?
    var keyAliasProperty: String?
    var keyPasswordProperty: String?
}

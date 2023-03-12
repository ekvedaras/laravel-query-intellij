package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import dev.ekvedaras.laravelquery.domain.ReferencesNamespace

sealed interface MigratesNamespace : ReferencesNamespace {
    val namespaceParameter: NamespaceParameter?
}

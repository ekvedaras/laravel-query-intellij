package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import dev.ekvedaras.laravelquery.domain.ReferencesNamespace

sealed interface MigratesNamespace : SchemaBuilderMethodCall, ReferencesNamespace {
    val namespaceParameter: NamespaceParameter?
}

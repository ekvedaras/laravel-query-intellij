package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import dev.ekvedaras.laravelquery.domain.ReferencesIndex

sealed interface MigratesIndexes : ReferencesIndex {
    val indexes: List<MigratedIndex>
}

package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import dev.ekvedaras.laravelquery.domain.ReferencesTableKey

sealed interface MigratesTableKeys : ReferencesTableKey {
    val tableKeys: List<MigratedTableKey>
}

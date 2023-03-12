package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import dev.ekvedaras.laravelquery.domain.ReferencesTable
import dev.ekvedaras.laravelquery.domain.StandaloneTableParameter

sealed interface MigratesTable : ReferencesTable {
    val tableParameter: StandaloneTableParameter?
}

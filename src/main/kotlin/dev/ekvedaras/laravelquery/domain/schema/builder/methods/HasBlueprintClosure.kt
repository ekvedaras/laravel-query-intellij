package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import dev.ekvedaras.laravelquery.domain.ReferencesTable
import dev.ekvedaras.laravelquery.domain.StandaloneTableParameter

sealed interface HasBlueprintClosure : ReferencesTable {
    val tableParameter: StandaloneTableParameter?
}

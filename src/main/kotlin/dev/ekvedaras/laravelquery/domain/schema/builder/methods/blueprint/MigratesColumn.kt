package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import dev.ekvedaras.laravelquery.domain.ReferencesColumn
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter

sealed interface MigratesColumn : ReferencesColumn {
    val columnParameter: StandaloneColumnParameter?;
}

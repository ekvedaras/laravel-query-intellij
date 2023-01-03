package dev.ekvedaras.laravelquery.domain.query.builder.methods

import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

sealed interface TableSelectionCall : QueryMethodCall {
    val tableParameter: TableParameter?
    val alias: Alias?
}

package dev.ekvedaras.laravelquery.domain.query.builder.methods

import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

interface TableSelectionCall : MethodCall {
    val tableParameter: TableParameter?
    val alias: Alias?
}

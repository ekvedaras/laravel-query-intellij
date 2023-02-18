package dev.ekvedaras.laravelquery.domain.query.builder.methods

sealed interface SubQuerySelectionCall : QueryMethodCall {
    val subQueryAlias: SubQueryAlias?
}

package dev.ekvedaras.laravelquery.domain.query.builder.methods

import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ClosureParameter

sealed interface AcceptsClosures : QueryMethodCall {
    val closures: Set<ClosureParameter>
}

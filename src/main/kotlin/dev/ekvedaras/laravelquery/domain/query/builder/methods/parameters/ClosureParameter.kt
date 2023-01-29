package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.jetbrains.php.lang.psi.elements.Function

sealed interface ClosureParameter {
    val function: Function
    val shouldScan: Boolean get() = false
    val queryParameter: QueryParameter? get() = null
}

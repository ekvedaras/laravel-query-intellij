package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.jetbrains.php.lang.psi.elements.Function
import dev.ekvedaras.laravelquery.support.transform

class WhenFalseClosureParameter(override val function: Function) : ClosureParameter {
    override val shouldScan = true
    override val queryParameter = function.getParameter(0).transform { QueryParameter(it) }
}

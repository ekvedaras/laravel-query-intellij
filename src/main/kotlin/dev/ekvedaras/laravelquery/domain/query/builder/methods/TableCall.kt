package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.query.QueryStatement

class TableCall(reference: MethodReference, queryStatement: QueryStatement) : FromCall(reference, queryStatement)

package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesColumn
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.support.returnWhen
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class StringCall(override val reference: MethodReference, override val table: MigrationTable) : BlueprintMethodCall, MigratesColumn {
    override val columnParameter = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, StandaloneColumnParameter> {
        StandaloneColumnParameter(it.asStringParameter())
    }

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? = returnWhen(
        parameter.equals(columnParameter),
        table.asExistingTable().transform { columnParameter?.findColumnReference(it) }
    )

    override fun completeFor(parameter: StringParameter): List<LookupElement> = returnWhen(
        parameter.equals(columnParameter),
        table.asExistingTable().transform { columnParameter?.getCompletionOptions(it) }
    ) ?: listOf()
}

package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbTable
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpExpression
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StandaloneTableParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.Migration
import dev.ekvedaras.laravelquery.support.firstChildOfType
import dev.ekvedaras.laravelquery.support.returnWhen
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class CreateCall(override val reference: MethodReference, override val migration: Migration) : SchemaBuilderMethodCall, HasBlueprintClosure {
    override val tableParameter = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, StandaloneTableParameter> {
        StandaloneTableParameter(it.asStringParameter())
    }

    override val closure: BlueprintClosure?
        get() = reference
            .getParameter(1)
            .transformInstanceOf<PhpExpression, BlueprintClosure?> { expression ->
                expression.firstChildOfType<Function>().transform { function ->
                    migration.tables
                        .find { it.name == tableParameter?.tableName }
                        .transform { BlueprintClosure(function, it) }
                }
            }

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? = returnWhen(parameter.equals(tableParameter), tableParameter?.table?.asDbTable())
    override fun completeFor(parameter: StringParameter): List<LookupElement> = returnWhen(parameter.equals(tableParameter)) {
        tableParameter?.getCompletionOptions()
    } ?: listOf()
}
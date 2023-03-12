package dev.ekvedaras.laravelquery.domain

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.util.text.findTextRange
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.QueryMethodCall
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasedParam
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationKeyParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ScopeParameter
import dev.ekvedaras.laravelquery.domain.tests.TestMethodCall
import dev.ekvedaras.laravelquery.support.cleanup
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.SchemaBuilderMethodCall
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint.BlueprintMethodCall

data class StringParameter(val element: StringLiteralExpression) {
    val project = element.project
    val text = element.text
        .cleanup() // remove quotes and trim
        .substringBefore("->") // ignore json selectors
        .substringBefore(":") // ignore relation columns
    val isEmpty = text.isEmpty()
    val parts = text.split('.')
    val hasOnePart = parts.size == 1
    val hasTwoParts = parts.size == 2
    val hasThreeParts = parts.size == 3
    val hasUncompletedPart = parts.size > 1 && text.endsWith('.')
    val lastPartRange: TextRange = element.text.findTextRange(parts.last().substringBefore(' ')) ?: TextRange(0, 0)
    val oneBeforeLastPartRange: TextRange = (if (parts.size > 1) element.text.findTextRange(parts[parts.size - 2]) else null)
        ?: TextRange(0, 0)
    val twoBeforeLastPartRange: TextRange = (if (parts.size > 2) element.text.findTextRange(parts[parts.size - 3]) else null)
        ?: TextRange(0, 0)

    private val isMethodReferenceParameter = element.parent.parent is MethodReference
    private val isEntryOfArrayWhichIsMethodReferenceParameter = element.parent.parent is ArrayCreationExpression && element.parent.parent.parent.parent is MethodReference
    private val isArrayHashKeyOfArrayWhichIsMethodReferenceParameter = element.parent.parent is ArrayHashElement && (element.parent.parent as ArrayHashElement).key == element && element.parent.parent.parent.parent.parent is MethodReference
    private val isInnerArrayEntry = element.parent.parent is ArrayCreationExpression && element.parent.parent.parent.parent is ArrayCreationExpression && element.parent.parent.parent.parent.parent.parent is MethodReference
    private val isInnerArrayHashKey = element.parent.parent is ArrayHashElement && element.parent.parent.parent is ArrayCreationExpression && element.parent.parent.parent.parent.parent is ArrayCreationExpression && element.parent.parent.parent.parent.parent.parent.parent is MethodReference

    val parentMethodParameter = when (element.parent.parent) {
        is ArrayHashElement -> {
            element.parentOfType<ParameterList>()?.parameters?.find { it == element.parent.parent.parent }
        }

        is ArrayCreationExpression -> {
            element.parentOfType<ParameterList>()?.parameters?.find { it == element.parent.parent }
        }

        else -> element
    }

    fun toAliasedParam(): AliasedParam = AliasedParam(this)

    fun shouldBeInspected(): Boolean {
        return isMethodReferenceParameter // ->get('parameter');
            || isEntryOfArrayWhichIsMethodReferenceParameter // ->get(['parameter'])
            || isArrayHashKeyOfArrayWhichIsMethodReferenceParameter // ->get(['parameter' => 'foo'])
            || isInnerArrayEntry // ->where([ ['parameter', '=', 'foo'] ])
            || isInnerArrayHashKey // ->insert([ ['parameter' => '1' ] ])
    }

    private val methodReference: MethodReference? get() = element.parentOfType()
    private val statement: Statement? get() = methodReference?.parentOfType()
    private val queryStatement: QueryStatement? get() = statement.transform { QueryStatement.from(it) }

    val queryMethodCall: QueryMethodCall? get() = methodReference.transform { queryStatement?.callChain?.methodCallFor(it) }
    val testMethodCall: TestMethodCall? get() = methodReference.transform { TestMethodCall.from(it) }
    val schemaBuilderMethodCall: SchemaBuilderMethodCall? get() = methodReference.transform { SchemaBuilderMethodCall.from(it) }
    val blueprintMethodCall: BlueprintMethodCall? get() = methodReference.transform { BlueprintMethodCall.from(it) }

    companion object {
        fun StringLiteralExpression.asStringParameter() = StringParameter(this)
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is TableWithAliasParameter -> other.stringParameter == this
        is TableParameter -> other.stringParameter == this
        is StandaloneTableParameter -> other.stringParameter == this
        is ColumnParameter -> other.stringParameter == this
        is StandaloneColumnParameter -> other.stringParameter == this
        is AliasParameter -> other.stringParameter == this
        is RelationParameter -> other.stringParameter == this
        is RelationKeyParameter -> other.stringParameter == this
        is ScopeParameter -> other.stringParameter == this
        is StringParameter -> other.element.originalElement == element.originalElement
        is PsiElement -> other == element.originalElement
        else -> false
    }

    override fun hashCode(): Int = element.hashCode()
}

package dev.ekvedaras.laravelquery.integration.query.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.query.Query
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.utils.LookupUtils.Companion.buildLookup
import kotlin.streams.toList

class NamespaceCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val string = parameters.position.parent as StringLiteralExpression

        if (string.parent.parent !is MethodReference && !(string.parent.parent is ArrayCreationExpression && string.parent.parent.parent is MethodReference)) {
            return
        }

        val methodReference = string.parentOfType<MethodReference>() ?: return
        val statement = QueryStatement(methodReference.parentOfType<Statement>() ?: return)

        val methodCall = statement.methodCallFor(methodReference) ?: return

        result.addAllElements(
            methodCall.completeFor(string.parentOfType<Parameter>() ?: return)
        )
    }
}

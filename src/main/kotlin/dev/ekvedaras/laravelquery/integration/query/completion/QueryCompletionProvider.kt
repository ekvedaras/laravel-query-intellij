package dev.ekvedaras.laravelquery.integration.query.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter

class QueryCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val string = parameters.position.parent as StringLiteralExpression

        if (string.parent.parent !is MethodReference && !(string.parent.parent is ArrayCreationExpression && string.parent.parent.parent.parent is MethodReference)) {
            return
        }

        val methodReference = string.parentOfType<MethodReference>() ?: return
        val statement = QueryStatement(methodReference.parentOfType() ?: return)

        val methodCall = statement.methodCallFor(methodReference) ?: return

        result.addAllElements(
            methodCall.completeFor(StringParameter(string))
        )
    }
}

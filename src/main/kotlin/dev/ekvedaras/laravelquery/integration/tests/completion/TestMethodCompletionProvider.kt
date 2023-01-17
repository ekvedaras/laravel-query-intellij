package dev.ekvedaras.laravelquery.integration.tests.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class TestMethodCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val string = parameters.position.parent.transformInstanceOf<StringLiteralExpression, StringParameter> {
            StringParameter(it)
        } ?: return

        if (! string.shouldBeInspected()) return

        val methodCall = string.testMethodCall ?: return

        result.addAllElements(
            methodCall.completeFor(string)
        )
    }
}
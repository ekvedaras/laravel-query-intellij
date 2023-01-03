package dev.ekvedaras.laravelquery.integration.model.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.model.methods.ModelMethodCall

class ModelCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val string = parameters.position.parent as StringLiteralExpression

        if (
            string.parent.parent !is MethodReference
            && !(string.parent.parent is ArrayCreationExpression && string.parent.parent.parent.parent is MethodReference)
            && !(string.parent.parent is ArrayHashElement && string.parent.parent.parent.parent.parent is MethodReference)
        ) {
            return
        }

        val methodReference = string.parentOfType<MethodReference>() ?: return
        val methodCall = ModelMethodCall.from(methodReference) ?: return

        result.addAllElements(
            methodCall.completeFor(StringParameter(string))
        )
    }
}
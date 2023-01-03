package dev.ekvedaras.laravelquery.integration.model.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.model.methods.ModelMethodCall
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class ModelCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val string = parameters.position.parent.transformInstanceOf<StringLiteralExpression, StringParameter> {
            StringParameter(it)
        } ?: return

        if (
            ! string.isMethodReferenceParameter
            && ! string.isEntryOfArrayWhichIsMethodReferenceParameter
            && ! string.isArrayHashKeyOfArrayWhichIsMethodReferenceParameter
        ) {
            return
        }

        val methodReference = string.element.parentOfType<MethodReference>() ?: return
        val methodCall = ModelMethodCall.from(methodReference) ?: return

        result.addAllElements(
            methodCall.completeFor(string)
        )
    }
}

package dev.ekvedaras.laravelquery.domain.model.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import kotlin.streams.toList

class CreateCall(override val reference: MethodReference) : ModelMethodCall {
    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        return this.model?.table?.columns()?.map { it.asLookupElement() }?.toList() ?: listOf()
    }
}

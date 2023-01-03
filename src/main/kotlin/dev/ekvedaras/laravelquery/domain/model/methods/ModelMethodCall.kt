package dev.ekvedaras.laravelquery.domain.model.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.support.classReference
import dev.ekvedaras.laravelquery.support.transform

sealed interface ModelMethodCall {
    val reference: MethodReference
    val classReference: ClassReference?
        get() = this.reference.classReference()
    val model: Model?
        get() = this.classReference.transform { Model.from(it) }

    fun completeFor(parameter: StringParameter): List<LookupElement>

    companion object {
        fun from(reference: MethodReference): ModelMethodCall? {
            return when(reference.name) {
                "create" -> CreateCall(reference)
                else -> null
            }
        }
    }
}

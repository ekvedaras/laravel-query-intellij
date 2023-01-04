package dev.ekvedaras.laravelquery.v4.utils

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isColumnDefinitionMethod

class SchemaTableResolver(private val reference: DbReferenceExpression) {
    fun resolve(methods: MutableList<MethodReference>, method: MethodReference) {
        if (!method.isBlueprintMethod(reference.project) && !method.isColumnDefinitionMethod(reference.project)) {
            return
        }

        methods.add(
            method.parentOfType<Function>()?.parentOfType() ?: return
        )
    }
}

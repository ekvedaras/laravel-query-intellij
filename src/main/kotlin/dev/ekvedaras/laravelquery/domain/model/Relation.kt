package dev.ekvedaras.laravelquery.domain.model

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.support.callChainOfType
import dev.ekvedaras.laravelquery.support.firstChildOfType
import dev.ekvedaras.laravelquery.support.transformInstanceOf

private val relationMethods = setOf(
    "hasOne", "belongsTo", "hasMany",
    "hasOneThrough", "hasManyThrough", "belongsToMany",
    "morphTo", "morphEagerTo", "morphInstanceTo", "morphOne", "morphMany", "morphToMany", "morphedByMany",
)

data class Relation(val method: Method) {
    private val groupStatement = method.firstChildOfType<GroupStatement>()
        ?: throw Exception("Invalid method. Can not find GroupStatement: { ... }");
    private val returnStatement = groupStatement.firstChildOfType<PhpReturn>()
        ?: throw Exception("Relation method group statement has no PhpReturn statement: return ... ");
    private val definitionMethod = returnStatement.firstChildOfType<MethodReference>()
        ?.callChainOfType<MethodReference>()
        ?.firstOrNull()
        ?: throw Exception("Relation method return statement must return a MethodReference: return \$this->methodCall(...)");

    init {
        val variable = definitionMethod.firstPsiChild as? Variable
            ?: throw Exception("Definition method must be called from a variable: \$this->...")
        if (variable.name != "this") throw Exception("Definition method must be return using \$this: return \$this->...")
        if (!relationMethods.contains(definitionMethod.name)) throw Exception("Definition method is not using a supported relation definition method like hasOne, belongsTo, etc. Used: ${definitionMethod.name}")
    }

    private val firstParameter = definitionMethod.getParameter(0) ?: throw Exception("No related model reference found")

    val model = when (firstParameter) {
        is ClassConstantReference -> firstParameter.firstPsiChild.transformInstanceOf<ClassReference, Model?> { Model.from(it) }
        is StringLiteralExpression -> Model.from(firstParameter) ?: throw Exception()
        else -> null
    } ?: throw Exception("Cannot find related model for method ${method.name}")

    fun asLookupElement() =
        LookupElementBuilder
            .create(method, method.name)
            .withLookupString(method.name)
            .withTailText(model.name, true)
            .withTypeText(model.table?.nameWithoutPrefix, true)
            .withIcon(model.clazz.icon)
}

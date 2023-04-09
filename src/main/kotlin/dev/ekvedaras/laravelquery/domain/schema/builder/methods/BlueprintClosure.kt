package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint.BlueprintMethodCall
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint.MigratesColumn
import dev.ekvedaras.laravelquery.support.firstChildOfType
import dev.ekvedaras.laravelquery.support.transformInstanceOf
import dev.ekvedaras.laravelquery.support.tryTransforming

data class BlueprintClosure(val closure: Function, val table: MigrationTable) {
    private val tableVariable: BlueprintVariable? = closure.getParameter(0).transformInstanceOf<Parameter, BlueprintVariable?> {
        it.tryTransforming { variable -> BlueprintVariable(variable) }
    }

    val columns = tableVariable
        ?.usageStatements()
        ?.mapNotNull { it.firstChildOfType<MethodReference>() }
        ?.mapNotNull { BlueprintMethodCall.from(it, table) }
        ?.filterIsInstance<MigratesColumn>()
        ?.mapNotNull { it.columnParameter }
}

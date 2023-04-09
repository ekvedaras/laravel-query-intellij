package dev.ekvedaras.laravelquery.domain.schema

import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.MigratesNamespace
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.MigratesTable
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.SchemaBuilderMethodCall
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.firstChildOfType
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.tap
import dev.ekvedaras.laravelquery.support.transformInstanceOf

data class Migration(private val clazz: PhpClass) {
    init {
        if (! clazz.isChildOfAny(LaravelClasses.Migration)) throw Exception("Class ${clazz.fqn} in file ${clazz.containingFile.name} does not extend ${LaravelClasses.Migration}")
    }

    private val upMethod = clazz.methods.firstOrNull { it.name == "up" }
    private val downMethod = clazz.methods.firstOrNull { it.name == "down" }

    var namespaces = setOf<MigrationNamespace>()
    var tables = setOf<MigrationTable>()

    init {
        val scanStatements: (statement: Statement) -> Unit = { statement: Statement ->
            statement.firstPsiChild.transformInstanceOf<MethodReference, Unit> { reference ->
                when(val migrationStatement = SchemaBuilderMethodCall.from(reference, this)) {
                    is MigratesTable -> tables += MigrationTable(migrationStatement)
                    is MigratesNamespace -> namespaces += MigrationNamespace(migrationStatement)
                    else -> {}
                }
            }
        }

        upMethod?.firstChildOfType<GroupStatement>()?.childrenOfType<Statement>()?.forEach(scanStatements)
        downMethod?.firstChildOfType<GroupStatement>()?.childrenOfType<Statement>()?.forEach(scanStatements)
    }
}

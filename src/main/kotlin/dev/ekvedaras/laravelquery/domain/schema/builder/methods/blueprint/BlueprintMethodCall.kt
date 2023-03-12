package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.HasBlueprintClosure
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.SchemaBuilderMethodCall
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isMemberOfAny
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

sealed interface BlueprintMethodCall {
    val reference: MethodReference
    val table: MigrationTable

    companion object {
        fun from(reference: MethodReference): BlueprintMethodCall? =
            reference
                .parentOfType<Function>()
                ?.parentOfType<MethodReference>()
                .transform { SchemaBuilderMethodCall.from(it) }
                .transformInstanceOf<HasBlueprintClosure, BlueprintMethodCall?> {
                    it.tableParameter?.tableName.transform { tableName ->
                        from(reference, MigrationTable(tableName, reference.project))
                    }
                }

        fun from(reference: MethodReference, table: MigrationTable): BlueprintMethodCall? {
            if (!reference.isMemberOfAny(LaravelClasses.Blueprint)) return null

            return when (reference.name) {
                "string" -> StringCall(reference, table)
                else -> null
            }
        }
    }

    fun completeFor(parameter: StringParameter): List<LookupElement>
}
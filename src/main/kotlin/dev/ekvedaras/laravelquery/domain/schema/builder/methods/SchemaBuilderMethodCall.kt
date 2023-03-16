package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.schema.Migration
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isMemberOfAny
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.tryTransforming

sealed interface SchemaBuilderMethodCall {
    val reference: MethodReference
    val migration: Migration

    companion object {
        fun from(reference: MethodReference) = reference.parentOfType<PhpClass>().tryTransforming {
            Migration(it)
        }.transform { from(reference, it) }

        fun from(reference: MethodReference, migration: Migration): SchemaBuilderMethodCall? {
            if (! reference.isMemberOfAny(
                    LaravelClasses.SchemaBuilder,
                    LaravelClasses.SchemaFacade,
                    LaravelClasses.SchemaFacadeAlias,
            )) return null

            return when(reference.name) {
                "createDatabase" -> CreateDatabaseCall(reference, migration)
                "dropDatabase", "dropDatabaseIfExists" -> DropDatabaseCall(reference, migration)
                "create", "table" -> CreateCall(reference, migration)
                "drop", "dropIfExists", "hasTable", "getColumnListing" -> DropCall(reference, migration)
                else -> null
            }
        }
    }

    fun completeFor(parameter: StringParameter): List<LookupElement>
}

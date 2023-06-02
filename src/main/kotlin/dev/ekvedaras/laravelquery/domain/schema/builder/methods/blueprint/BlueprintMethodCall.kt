package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.schema.Migration
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
    val migration: Migration get() = table.migration

    companion object {
        fun from(reference: MethodReference): BlueprintMethodCall? =
            reference
                .parentOfType<Function>()
                ?.parentOfType<MethodReference>()
                .transform { SchemaBuilderMethodCall.from(it) }
                .transformInstanceOf<HasBlueprintClosure, BlueprintMethodCall?> {
                    from(reference, MigrationTable(it))
                }

        fun from(reference: MethodReference, table: MigrationTable): BlueprintMethodCall? {
            if (!reference.isMemberOfAny(LaravelClasses.Blueprint)) return null

            return when (reference.name) {
                "id", "foreignId", "uuid", "foreignUuid",
                "increments", "integerIncrements", "tinyIncrements", "smallIncrements", "mediumIncrements", "bigIncrements",
                "integer", "tinyInteger", "smallInteger", "mediumInteger", "bigInteger",
                "unsignedInteger", "unsignedTinyInteger", "unsignedSmallInteger", "unsignedMediumInteger", "unsignedBigInteger",
                "float", "double", "decimal", "unsignedFloat", "unsignedDouble", "unsignedDecimal",
                "char", "string", "text", "mediumText", "longText",
                "boolean", "enum", "set", "json", "jsonb",
                "date", "dateTime", "dateTimeTz",
                "time", "timeTz", "timestamp", "timestampTz",
                "softDeletes", "softDeletesTz", "dropSoftDeletes", "dropSoftDeletesTz",
                "year",
                "binary", "ipAddress", "macAddress",
                "geometry", "point", "lineString", "polygon", "geometryCollection",
                "multiPoint", "multiLineString", "multiPolygon", "multiPolygonZ",
                "computed", "after", "rename", "removeColumn" -> StringCall(reference, table)
                "dropColumn" -> DropColumnCall(reference, table)
                "renameColumn" -> RenameColumnCall(reference, table)
                "foreignIdFor" -> ForeignIdForCall(reference, table)
                "addColumn" -> AddColumnCall(reference, table)
                "index" -> IndexCall(reference, table)
                else -> null
            }
        }
    }

    fun completeFor(parameter: StringParameter): List<LookupElement>
}

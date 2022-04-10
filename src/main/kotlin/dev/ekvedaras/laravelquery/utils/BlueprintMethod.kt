package dev.ekvedaras.laravelquery.utils

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.nextSiblingWithText
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

internal val BlueprintColumnMethods = listOf(
    "increments",
    "integerIncrements",
    "tinyIncrements",
    "mediumIncrements",
    "bigIncrements",
    "char",
    "string",
    "text",
    "mediumText",
    "longText",
    "integer",
    "tinyInteger",
    "smallInteger",
    "mediumInteger",
    "bigInteger",
    "unsignedInteger",
    "unsignedTinyInteger",
    "unsignedSmallInteger",
    "unsignedMediumInteger",
    "unsignedBigInteger",
    "foreignId",
//                            "foreignIdFor", // Needs custom handling
    "float",
    "double",
    "decimal",
    "unsignedFloat",
    "unsignedDouble",
    "unsignedDecimal",
    "boolean",
    "enum",
    "set",
    "json",
    "jsonb",
    "date",
    "dateTime",
    "dateTimeTz",
    "time",
    "timeTz",
    "timestamp",
    "timestampTz",
    "softDeletes",
    "softDeletesTz",
    "year",
    "binary",
    "uuid",
    "foreignUuid",
    "ipAddress",
    "macAddress",
    "geometry",
    "point",
    "lineString",
    "polygon",
    "geometryCollection",
    "multiPoint",
    "multiLineString",
    "multiPolygon",
    "multiPolygonZ",
    "computed",
)

class BlueprintMethod private constructor() {
    companion object {
        fun MethodReference.isId() = this.name == "id"
        fun MethodReference.isTimestamps() = this.name == "timestamps" || this.name == "timestampsTz"
        fun MethodReference.isSoftDeletes() = this.name == "softDeletes" || this.name == "softDeletesTz"
        fun MethodReference.isColumnDefinition() = BlueprintColumnMethods.contains(this.name)
        fun MethodReference.getColumnDefinitionReference() = this.firstPsiChild?.nextPsiSibling?.firstPsiChild
        fun MethodReference.getColumnName() = this.getColumnDefinitionReference()?.text?.unquoteAndCleanup()
        fun MethodReference.isInsideUpMigration() = this.parentOfType<Method>()?.name == "up"
        fun MethodReference.createsTable() = this.parentOfType<Function>()?.parentOfType<MethodReference>()?.name == "create"
        fun MethodReference.isNullable() = this.nextSibling is LeafPsiElement &&
            (this.nextSibling as LeafPsiElement).textMatches("->") &&
            this.nextSiblingWithText("nullable") != null &&
            (
                ((this.nextSiblingWithText("nullable")!!.nextSibling.nextSibling as ParameterList).getParameter(0) !is ConstantReference) || (
                    ((this.nextSiblingWithText("nullable")!!.nextSibling.nextSibling as ParameterList).getParameter(0) is ConstantReference)
                        && ((this.nextSiblingWithText("nullable")!!.nextSibling.nextSibling as ParameterList).getParameter(0) as ConstantReference).canonicalText == "true"
                    )
                )

    }
}

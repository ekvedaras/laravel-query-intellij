package dev.ekvedaras.laravelquery.utils

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.nextSiblingInTreeWithText
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup
import icons.DatabaseIcons

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
        fun MethodReference.getColumnName(): String? {
            if (this.getColumnDefinitionReference() is ArrayCreationExpression) {
                var element = (this.getColumnDefinitionReference() as ArrayCreationExpression).firstPsiChild
                var name = element?.text?.unquoteAndCleanup() ?: ""

                while (element?.nextPsiSibling != null) {
                    element = element.nextPsiSibling
                    name += if (element != null) {
                        "_" + element.text.unquoteAndCleanup()
                    } else {
                        ""
                    }
                }

                return name
            }

            return this.getColumnDefinitionReference()?.text?.unquoteAndCleanup()
        }

        fun MethodReference.isInsideUpMigration() = this.parentOfType<Method>()?.name == "up"
        fun MethodReference.createsTable() = this.parentOfType<Function>()?.parentOfType<MethodReference>()?.name == "create"
        fun MethodReference.isNullable() = this.nextSibling is LeafPsiElement &&
            (this.nextSibling as LeafPsiElement).textMatches("->") &&
            this.nextSiblingInTreeWithText("nullable") != null &&
            (
                ((this.nextSiblingInTreeWithText("nullable")!!.nextSibling.nextSibling as ParameterList).getParameter(0) !is ConstantReference) || (
                    ((this.nextSiblingInTreeWithText("nullable")!!.nextSibling.nextSibling as ParameterList).getParameter(0) is ConstantReference)
                        && ((this.nextSiblingInTreeWithText("nullable")!!.nextSibling.nextSibling as ParameterList).getParameter(0) as ConstantReference).canonicalText == "true"
                    )
                )

        fun MethodReference.hasUniqueIndex() = this.nextSibling is LeafPsiElement &&
            (this.nextSibling as LeafPsiElement).textMatches("->") &&
            this.nextSiblingInTreeWithText("unique") != null

        fun MethodReference.hasIndex() = this.nextSibling is LeafPsiElement &&
            (this.nextSibling as LeafPsiElement).textMatches("->") &&
            this.nextSiblingInTreeWithText("index") != null

        fun MethodReference.isPrimary() = this.nextSibling is LeafPsiElement &&
            (this.nextSibling as LeafPsiElement).textMatches("->") &&
            this.nextSiblingInTreeWithText("primary") != null

        fun MethodReference.getIndexName(table: String): String {
            if (this.getColumnDefinitionReference()?.nextPsiSibling is StringLiteralExpression) {
                return this.getColumnDefinitionReference()!!.nextPsiSibling!!.text.unquoteAndCleanup()
            }

            return "${table}_${this.getColumnName()}_${this.name}"
        }

        fun MethodReference.dbIcon() =
            if (this.isPrimary()) {
                DatabaseIcons.ColGoldKey
            } else if (this.hasIndex()) {
                if (this.isNullable()) {
                    DatabaseIcons.ColIndex
                } else {
                    DatabaseIcons.ColDotIndex
                }
            } else if (this.hasUniqueIndex()) {
                if (this.isNullable()) {
                    DatabaseIcons.ColBlueKeyIndex
                } else {
                    DatabaseIcons.ColBlueKeyDotIndex
                }
            } else {
                DatabaseIcons.ColDot
            }
    }
}

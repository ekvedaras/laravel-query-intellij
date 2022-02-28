package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.impl.ArrayHashElementImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl
import dev.ekvedaras.laravelquery.utils.ClassUtils.Companion.asTableName
import dev.ekvedaras.laravelquery.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.tableName
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.isArrayKey
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.isArrayValue
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.isPhpArray
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

object LaravelClasses {
    const val QueryBuilder = "\\Illuminate\\Database\\Query\\Builder"
    const val EloquentBuilder = "\\Illuminate\\Database\\Eloquent\\Builder"
    const val SchemaBuilder = "\\Illuminate\\Database\\Schema\\Builder"
    const val Blueprint = "\\Illuminate\\Database\\Schema\\Blueprint"
    const val JoinClause = "\\Illuminate\\Database\\Query\\JoinClause"
    const val Relation = "\\Illuminate\\Database\\Eloquent\\Relations\\Relation"
    const val Model = "\\Illuminate\\Database\\Eloquent\\Model"
    const val DbFacade = "\\Illuminate\\Support\\Facades\\DB"
    const val DbFacadeAlias = "\\DB"
    const val SchemaFacade = "\\Illuminate\\Support\\Facades\\Schema"
    const val SchemaFacadeAlias = "\\Schema"
    const val ColumnDefinition = "\\Illuminate\\Database\\Schema\\ColumnDefinition"
}

@Suppress("TooManyFunctions")
class LaravelUtils private constructor() {
    companion object {
        // <editor-fold desc="\Illuminate\Database query builder classes" defaultstate="collapsed">
        @JvmStatic
        val InterestingClasses = listOf(
            LaravelClasses.QueryBuilder,
            LaravelClasses.EloquentBuilder,
            LaravelClasses.JoinClause,
            LaravelClasses.Relation,
            LaravelClasses.Model,
            LaravelClasses.DbFacade,
            LaravelClasses.DbFacadeAlias,
            LaravelClasses.SchemaBuilder,
            LaravelClasses.SchemaFacade,
            LaravelClasses.SchemaFacadeAlias,
            LaravelClasses.Blueprint,
            LaravelClasses.ColumnDefinition,
        )
        // </editor-fold>

        // <editor-fold desc="\Illuminate\Database schema builder classes" defaultstate="collapsed">
        @JvmStatic
        val SchemaBuilderClasses = listOf(
            LaravelClasses.SchemaBuilder,
            LaravelClasses.SchemaFacade,
            LaravelClasses.SchemaFacadeAlias,
        )
        // </editor-fold>

        // <editor-fold desc="Query builder methods where table name should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableMethods = listOf(
            "from",
            "join", "joinWhere",
            "leftJoin", "leftJoinWhere",
            "rightJoin", "rightJoinWhere",
            "crossJoin",
            "table", "hasTable", "getColumnListing",
            "hasColumn", "hasColumns", "getColumnType",
            "table", "create", "drop", "dropIfExists",
            "dropColumns", "rename", "createDatabase", "dropDatabaseIfExists",
        )
        // </editor-fold>

        // <editor-fold desc="Schema builder methods where schema name should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderSchemaMethods = listOf(
            "createDatabase", "dropDatabaseIfExists"
        )
        // </editor-fold>

        // <editor-fold desc="Table methods param indexes where table alias is defined" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableAliasParams = hashMapOf(
            "from" to 1,
            "fromSub" to 1,
            "selectSub" to 1,
            "table" to 1,
        )
        // </editor-fold>

        // <editor-fold desc="Methods and params where columns should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableColumnsParams = mapOf(
            "select" to listOf(-1),
            "addSelect" to listOf(0),
            "join" to listOf(1, 2, 3),
            "joinWhere" to listOf(1),
            "joinSub" to listOf(2, 3, 4),
            "leftJoin" to listOf(1, 2, 3),
            "leftJoinWhere" to listOf(1),
            "leftJoinSub" to listOf(2, 3, 4),
            "rightJoin" to listOf(1, 2, 3),
            "rightJoinWhere" to listOf(1),
            "rightJoinSub" to listOf(2, 3, 4),
            "crossJoin" to listOf(1, 2, 3),
            "where" to listOf(0),
            "orWhere" to listOf(0),
            "whereColumn" to listOf(0, 1, 2),
            "orWhereColumn" to listOf(0, 1, 2),
            "whereIn" to listOf(0),
            "orWhereIn" to listOf(0),
            "whereNotIn" to listOf(0),
            "orWhereNotIn" to listOf(0),
            "whereIntegerInRaw" to listOf(0),
            "orWhereIntegerInRaw" to listOf(0),
            "orWhereIntegerNotInRaw" to listOf(0),
            "whereNull" to listOf(0),
            "orWhereNull" to listOf(0),
            "whereNotNull" to listOf(0),
            "whereBetween" to listOf(0),
            "whereBetweenColumns" to listOf(0, 1),
            "orWhereBetween" to listOf(0),
            "orWhereBetweenColumns" to listOf(0, 1),
            "whereNotBetween" to listOf(0),
            "whereNotBetweenColumns" to listOf(0, 1),
            "orWhereNotBetween" to listOf(0),
            "orWhereNotBetweenColumns" to listOf(0, 1),
            "orWhereNotNull" to listOf(0),
            "whereDate" to listOf(0),
            "orWhereDate" to listOf(0),
            "whereTime" to listOf(0),
            "orWhereTime" to listOf(0),
            "whereDay" to listOf(0),
            "orWhereDay" to listOf(0),
            "whereMonth" to listOf(0),
            "orWhereMonth" to listOf(0),
            "whereYear" to listOf(0),
            "orWhereYear" to listOf(0),
            "whereRowValues" to listOf(0),
            "orWhereRowValues" to listOf(0),
            "whereJsonContains" to listOf(0),
            "orWhereJsonContains" to listOf(0),
            "whereJsonDoesntContain" to listOf(0),
            "orWhereJsonDoesntContain" to listOf(0),
            "whereJsonLength" to listOf(0),
            "orWhereJsonLength" to listOf(0),
            "groupBy" to listOf(-1),
            "having" to listOf(0),
            "orHaving" to listOf(0),
            "havingBetween" to listOf(0),
            "orderBy" to listOf(0),
            "orderByDesc" to listOf(0),
            "latest" to listOf(0),
            "oldest" to listOf(0),
            "forPageBeforeId" to listOf(2),
            "forPageAfterId" to listOf(2),
            "reorder" to listOf(0),
            "find" to listOf(1),
            "value" to listOf(0),
            "get" to listOf(-1),
            "paginate" to listOf(1),
            "simplePaginate" to listOf(1),
            "getCountForPagination" to listOf(0),
            "pluck" to listOf(0, 1),
            "implode" to listOf(0),
            "count" to listOf(0),
            "min" to listOf(0),
            "max" to listOf(0),
            "sum" to listOf(0),
            "avg" to listOf(0),
            "average" to listOf(0),
            "aggregate" to listOf(1),
            "numericAggregate" to listOf(1),
            "insertUsing" to listOf(1),
            "insertUsing" to listOf(1),
            "increment" to listOf(0),
            "decrement" to listOf(0),
            "updateOrInsert" to listOf(0, 1),
            "update" to listOf(0),
            "on" to listOf(0, 1, 2),
            "hasColumn" to listOf(1),
            "hasColumns" to listOf(1),
            "getColumnType" to listOf(1),
            "dropColumn" to listOf(0),
            "dropColumns" to listOf(1),
            "dropConstrainedForeignId" to listOf(0),
            "renameColumn" to listOf(0),
            "dropSoftDeletes" to listOf(0),
            "dropSoftDeletesTz" to listOf(0),
            "unique" to listOf(0),
            "index" to listOf(0),
            "spatialIndex" to listOf(0),
            "foreign" to listOf(0),
            "indexCommand" to listOf(1),
            "createIndexName" to listOf(1),
            "after" to listOf(0),
            "removeColumn" to listOf(0),
            "primary" to listOf(0),
            "unique" to listOf(0),
            "index" to listOf(0),
            "dropIndex" to listOf(0),
            "dropUnique" to listOf(0),
            "dropPrimary" to listOf(0),
            "dropForeign" to listOf(0),
            "dropSpatialIndex" to listOf(0),
            "foreign" to listOf(0),
            "id" to listOf(0),
            "increments" to listOf(0),
            "integerIncrements" to listOf(0),
            "tinyIncrements" to listOf(0),
            "mediumIncrements" to listOf(0),
            "bigIncrements" to listOf(0),
            "char" to listOf(0),
            "string" to listOf(0),
            "text" to listOf(0),
            "mediumText" to listOf(0),
            "longText" to listOf(0),
            "integer" to listOf(0),
            "tinyInteger" to listOf(0),
            "smallInteger" to listOf(0),
            "mediumInteger" to listOf(0),
            "bigInteger" to listOf(0),
            "unsignedInteger" to listOf(0),
            "unsignedTinyInteger" to listOf(0),
            "unsignedSmallInteger" to listOf(0),
            "unsignedMediumInteger" to listOf(0),
            "unsignedBigInteger" to listOf(0),
            "foreignId" to listOf(0),
            "foreignIdFor" to listOf(1),
            "float" to listOf(0),
            "double" to listOf(0),
            "decimal" to listOf(0),
            "unsignedFloat" to listOf(0),
            "unsignedDouble" to listOf(0),
            "unsignedDecimal" to listOf(0),
            "boolean" to listOf(0),
            "enum" to listOf(0),
            "set" to listOf(0),
            "json" to listOf(0),
            "jsonb" to listOf(0),
            "date" to listOf(0),
            "dateTime" to listOf(0),
            "dateTimeTz" to listOf(0),
            "time" to listOf(0),
            "timeTz" to listOf(0),
            "timestamp" to listOf(0),
            "timestampTz" to listOf(0),
            "softDeletes" to listOf(0),
            "softDeletesTz" to listOf(0),
            "year" to listOf(0),
            "binary" to listOf(0),
            "uuid" to listOf(0),
            "foreignUuid" to listOf(0),
            "ipAddress" to listOf(0),
            "macAddress" to listOf(0),
            "geometry" to listOf(0),
            "point" to listOf(0),
            "lineString" to listOf(0),
            "polygon" to listOf(0),
            "geometryCollection" to listOf(0),
            "multiPoint" to listOf(0),
            "multiLineString" to listOf(0),
            "multiPolygon" to listOf(0),
            "multiPolygonZ" to listOf(0),
            "computed" to listOf(0),
            "create" to listOf(0),
            "update" to listOf(0),
            "fill" to listOf(0),
            "updateOrCreate" to listOf(0, 1),
            "updateOrInsert" to listOf(0, 1),
        )
        // </editor-fold>

        // <editor-fold desc="Methods and params where indexes should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableIndexesParams = mapOf(
            "index" to listOf(1),
            "dropIndex" to listOf(0),
        )
        // </editor-fold>

        // <editor-fold desc="Methods and params where unique indexes should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableUniqueIndexesParams = mapOf(
            "unique" to listOf(1),
            "dropUnique" to listOf(0),
        )
        // </editor-fold>

        // <editor-fold desc="Methods and params where keys should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableKeysParams = mapOf(
            "primary" to listOf(1),
            "dropPrimary" to listOf(0),
        )
        // </editor-fold>

        // <editor-fold desc="Methods and params where foreign keys should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableForeignKeysParams = mapOf(
            "foreign" to listOf(0),
            "dropForeign" to listOf(0),
        )
        // </editor-fold>

        // <editor-fold desc="Methods where params may accept columns as array values" defaultstate="collapsed">
        @JvmStatic
        private val BuilderMethodsWithTableColumnsInArrayValues = listOf(
            "get", "select",
            "whereBetweenColumns", "orWhereBetweenColumns",
            "whereNotBetweenColumns", "orWhereNotBetweenColumns",
            "hasColumns", "dropColumns", "dropColumns",
            "primary", "unique", "index", "spatialIndex", "foreign",
            "dropPrimary", "dropUnique", "dropIndex", "dropSpatialIndex", "dropForeign",
            "indexCommand", "createIndexName",
        )
        // </editor-fold>

        // <editor-fold desc="Methods where only columns should be completed" defaultstate="collapsed">
        @JvmStatic
        private val MethodsWhereOnlyColumnsShouldBeCompleted = listOf(
            "create", "update", "fill",
        )
        // </editor-fold>

        // <editor-fold desc="Methods where params only accept columns as array values" defaultstate="collapsed">
        @JvmStatic
        private val BuilderMethodsWithTableColumnsOnlyInArrayValues = listOf(
            "dropPrimary", "dropUnique", "dropIndex", "dropSpatialIndex", "dropForeign",
        )
        // </editor-fold>

        // <editor-fold desc="Methods that work with indexes" defaultstate="collapsed">
        @JvmStatic
        private val BlueprintMethodsForIndexes = listOf(
            "index", "spacialIndex",
            "dropIndex", "dropSpatialIndex",
        )
        // </editor-fold>

        // <editor-fold desc="Methods that work with indexes" defaultstate="collapsed">
        @JvmStatic
        private val BlueprintMethodsForUniqueIndexes = listOf(
            "unique", "dropUnique",
        )
        // </editor-fold>

        // <editor-fold desc="Methods that work with keys" defaultstate="collapsed">
        @JvmStatic
        private val BlueprintMethodsForKeys = listOf(
            "primary",
            "dropPrimary",
        )
        // </editor-fold>

        // <editor-fold desc="Methods that work with foreign keys" defaultstate="collapsed">
        @JvmStatic
        private val BlueprintMethodsForForeignKeys = listOf(
            "foreign",
            "dropForeign",
        )
        // </editor-fold>

        // <editor-fold desc="Possible operators" defaultstate="collapsed">
        @JvmStatic
        private val Operators = listOf<CharSequence>(
            "=", "<", ">", "<=", ">=", "<>", "!=", "<=>",
            "like", "like binary", "not like", "like",
            "&", "|", "^", "<<", ">>",
            "rlike", "not rlike", "regexp", "not regexp",
            "~", "~*", "!~", "!~*", "similar to",
            "not similar to", "not ilike", "~~*", "!~~*",
        )

        @JvmStatic
        private val OperatorPositions = listOf(1, 2)
        // </editor-fold>

        fun MethodReference.isInteresting(project: Project): Boolean =
            MethodUtils.resolveMethodClasses(this, project).any { clazz ->
                InterestingClasses.any {
                    clazz.isChildOf(it)
                }
            }

        fun MethodReference.isEloquentModel(project: Project): Boolean =
            MethodUtils.resolveMethodClasses(this, project).any { clazz ->
                clazz.isChildOf(LaravelClasses.Model)
            }

        fun MethodReference.shouldCompleteSchemas(project: Project): Boolean =
            this.shouldCompleteOnlySchemas() || !this.isSchemaBuilderMethod(project)

        fun MethodReference.isSchemaBuilderMethod(project: Project): Boolean =
            MethodUtils.resolveMethodClasses(this, project).any { clazz ->
                SchemaBuilderClasses.any {
                    clazz.isChildOf(it)
                }
            }

        fun MethodReference.shouldCompleteOnlySchemas(): Boolean =
            BuilderSchemaMethods.contains(this.name)

        fun MethodReference.shouldCompleteOnlyColumns(): Boolean =
            MethodsWhereOnlyColumnsShouldBeCompleted.contains(this.name)

        fun MethodReference.isBlueprintMethod(project: Project): Boolean =
            MethodUtils.resolveMethodClasses(this, project).any { clazz ->
                clazz.isChildOf(LaravelClasses.Blueprint)
            }

        fun MethodReference.isColumnDefinitionMethod(project: Project): Boolean =
            MethodUtils.resolveMethodClasses(this, project).any { clazz ->
                clazz.isChildOf(LaravelClasses.ColumnDefinition)
            }

        fun PhpClass.tableName(resolveFromName: Boolean = true): String? {
            val tableField = this.fields.find { it.name == "table" }

            if (ClassUtils.fieldHasDefaultValue(tableField)) {
                val defaultName = tableField?.defaultValue?.text?.unquoteAndCleanup()
                if (defaultName != null) {
                    return defaultName
                }

                if (!resolveFromName) {
                    return defaultName
                }
            }

            if (this.parent is PhpClass) {
                val defaultName = (this.parent as PhpClass).tableName(false)
                if (defaultName != null) {
                    return defaultName
                }

                if (!resolveFromName) {
                    return defaultName
                }
            }

            return this.asTableName()
        }

        fun PsiElement.isInsideRelationClosure(): Boolean =
            this is ArrayHashElementImpl && this.parentOfType<MethodReferenceImpl>()?.name == "with"

        fun PhpClassImpl.isJoinOrRelation(): Boolean =
            this.isChildOf(LaravelClasses.JoinClause) || this.isChildOf(LaravelClasses.Relation)

        fun MethodReference.isBuilderMethodByName(): Boolean =
            BuilderTableMethods.contains(this.name)

        fun MethodReference.isBuilderMethodForColumns(): Boolean =
            BuilderTableColumnsParams.containsKey(this.name)

        fun MethodReference.isBuilderMethodForIndexes(): Boolean =
            BuilderTableIndexesParams.containsKey(this.name)

        fun MethodReference.isBuilderMethodForUniqueIndexes(): Boolean =
            BuilderTableUniqueIndexesParams.containsKey(this.name)

        fun MethodReference.isBuilderMethodForKeys(): Boolean =
            BuilderTableKeysParams.containsKey(this.name)

        fun MethodReference.isBuilderMethodForForeignKeys(): Boolean =
            BuilderTableForeignKeysParams.containsKey(this.name)

        fun MethodReference.isColumnParam(position: PsiElement, allowArray: Boolean): Boolean =
            this.isColumnParam(position.findParamIndex(allowArray))

        fun MethodReference.isIndexParam(position: PsiElement): Boolean =
            this.isIndexParam(position.findParamIndex())

        fun MethodReference.isUniqueIndexParam(position: PsiElement): Boolean =
            this.isUniqueIndexParam(position.findParamIndex())

        fun MethodReference.isKeyParam(position: PsiElement): Boolean =
            this.isKeyParam(position.findParamIndex())

        fun MethodReference.isForeignKeyParam(position: PsiElement): Boolean =
            this.isForeignKeyParam(position.findParamIndex())

        fun MethodReference.hasColumnsInAllParams(): Boolean =
            this.isColumnParam(-1)

        fun MethodReference.hasIndexesInAllParams(): Boolean =
            this.isIndexParam(-1)

        fun MethodReference.hasUniqueIndexesInAllParams(): Boolean =
            this.isUniqueIndexParam(-1)

        fun MethodReference.hasKeysInAllParams(): Boolean =
            this.isKeyParam(-1)

        fun MethodReference.hasForeignKeysInAllParams(): Boolean =
            this.isForeignKeyParam(-1)

        fun MethodReference.isColumnParam(index: Int): Boolean =
            BuilderTableColumnsParams[this.name]?.contains(index) ?: false

        fun MethodReference.isIndexParam(index: Int): Boolean =
            BuilderTableIndexesParams[this.name]?.contains(index) ?: false

        fun MethodReference.isUniqueIndexParam(index: Int): Boolean =
            BuilderTableUniqueIndexesParams[this.name]?.contains(index) ?: false

        fun MethodReference.isKeyParam(index: Int): Boolean =
            BuilderTableKeysParams[this.name]?.contains(index) ?: false

        fun MethodReference.isForeignKeyParam(index: Int): Boolean =
            BuilderTableForeignKeysParams[this.name]?.contains(index) ?: false

        fun MethodReference.canHaveAliasParam(): Boolean =
            BuilderTableAliasParams.containsKey(this.name)

        fun CompletionParameters.isColumnIn(method: MethodReference, allowArray: Boolean): Boolean =
            this.position.isColumnIn(method, allowArray)

        fun PsiElement.isColumnIn(method: MethodReference, allowArray: Boolean): Boolean =
            method.isColumnParam(this, allowArray) || method.hasColumnsInAllParams()

        fun CompletionParameters.isIndexIn(method: MethodReference): Boolean =
            this.position.isIndexIn(method)

        fun PsiElement.isIndexIn(method: MethodReference): Boolean =
            method.isIndexParam(this) || method.hasIndexesInAllParams()

        fun CompletionParameters.isUniqueIndexIn(method: MethodReference): Boolean =
            this.position.isUniqueIndexIn(method)

        fun PsiElement.isUniqueIndexIn(method: MethodReference): Boolean =
            method.isUniqueIndexParam(this) || method.hasUniqueIndexesInAllParams()

        fun CompletionParameters.isKeyIn(method: MethodReference): Boolean =
            this.position.isKeyIn(method)

        fun PsiElement.isKeyIn(method: MethodReference): Boolean =
            method.isKeyParam(this) || method.hasKeysInAllParams()

        fun CompletionParameters.isForeignKeyIn(method: MethodReference): Boolean =
            this.position.isForeignKeyIn(method)

        fun PsiElement.isForeignKeyIn(method: MethodReference): Boolean =
            method.isForeignKeyParam(this) || method.hasForeignKeysInAllParams()

        fun MethodReference.canHaveColumnsInArrayValues(): Boolean =
            BuilderMethodsWithTableColumnsInArrayValues.contains(this.name)

        fun MethodReference.canOnlyHaveColumnsInArrayValues(): Boolean =
            BuilderMethodsWithTableColumnsOnlyInArrayValues.contains(this.name)

        fun MethodReference.isForIndexes(): Boolean =
            BlueprintMethodsForIndexes.contains(this.name)

        fun MethodReference.isForUniqueIndexes(): Boolean =
            BlueprintMethodsForUniqueIndexes.contains(this.name)

        fun MethodReference.isForKeys(): Boolean =
            BlueprintMethodsForKeys.contains(this.name)

        fun MethodReference.isForForeignKeys(): Boolean =
            BlueprintMethodsForForeignKeys.contains(this.name)

        fun CompletionParameters.isInsideRegularFunction(): Boolean =
            this.position.isInsideRegularFunction()

        fun PsiElement.isInsideRegularFunction(): Boolean =
            (this.parent?.parent is FunctionReference && this.parent?.parent !is MethodReference) ||
                (this.parent?.parent?.parent is FunctionReference && this.parent?.parent?.parent !is MethodReference)

        fun PsiElement.isOperatorParam(allowArray: Boolean = false): Boolean =
            OperatorPositions.contains(this.findParamIndex(allowArray)) && Operators.any {
                this.textMatches("'$it'") || this.textMatches("\"$it\"")
            }

        fun CompletionParameters.isInsidePhpArrayOrValue(): Boolean =
            this.position.isInsidePhpArrayOrValue()

        fun CompletionParameters.isArrayKey(): Boolean =
            this.position.parent?.parent?.isArrayKey() ?: false

        fun CompletionParameters.isArrayValue(): Boolean =
            this.position.parent?.parent?.isArrayValue() ?: false

        fun PsiElement.isInsidePhpArrayOrValue(): Boolean =
            (this.parent?.parent?.isPhpArray() ?: false) ||
                (this.parent?.parent?.isArrayValue() ?: false) ||
                this.parent?.parent is ArrayHashElementImpl?

        fun PsiElement.isAssocArrayValue(): Boolean =
            (
                this.parent?.parent?.prevSibling is TreeElement &&
                    (this.parent?.parent?.prevSibling as TreeElement).textMatches("=>")
                ) ||
                (
                    this.parent?.parent?.prevSibling?.prevSibling is TreeElement &&
                        (this.parent?.parent?.prevSibling?.prevSibling as TreeElement).textMatches("=>")
                    )

        fun PsiElement.selectsAllColumns(): Boolean =
            this.textContains('*')

        fun CompletionParameters.isTableParam(): Boolean =
            this.position.isTableParam()

        fun PsiElement.isTableParam(): Boolean =
            this.findParamIndex() == 0 // So far all functions accept table as the first argument
    }
}

package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.impl.ArrayHashElementImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import dev.ekvedaras.laravelquery.utils.ClassUtils.Companion.asTableName
import dev.ekvedaras.laravelquery.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.isArrayValue
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.isPhpArray
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

object LaravelClasses {
    const val QueryBuilder = "\\Illuminate\\Database\\Query\\Builder"
    const val EloquentBuilder = "\\Illuminate\\Database\\Eloquent\\Builder"
    const val JoinClause = "\\Illuminate\\Database\\Query\\JoinClause"
    const val Relation = "\\Illuminate\\Database\\Eloquent\\Relations\\Relation"
    const val Model = "\\Illuminate\\Database\\Eloquent\\Model"
}

@Suppress("TooManyFunctions")
class LaravelUtils private constructor() {
    companion object {
        // <editor-fold desc="\Illuminate\Database query builder classes" defaultstate="collapsed">
        @JvmStatic
        val DatabaseBuilderClasses = listOf(
            LaravelClasses.QueryBuilder,
            LaravelClasses.EloquentBuilder,
            LaravelClasses.JoinClause,
            LaravelClasses.Relation,
            LaravelClasses.Model,
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
        )
        // </editor-fold>

        // <editor-fold desc="Table methods param indexes where table alias is defined" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableAliasParams = hashMapOf(
            "from" to 1,
            "fromSub" to 1,
            "selectSub" to 1,
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
        )
        // </editor-fold>

        // <editor-fold desc="Methods where params may accept columns as array values" defaultstate="collapsed">
        @JvmStatic
        private val BuilderMethodsWithTableColumnsInArrayValues = listOf(
            "get", "select",
            "whereBetweenColumns", "orWhereBetweenColumns",
            "whereNotBetweenColumns", "orWhereNotBetweenColumns",
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

        fun MethodReference.isBuilderClassMethod(project: Project): Boolean =
            MethodUtils.resolveMethodClasses(this, project).any { clazz ->
                DatabaseBuilderClasses.any {
                    clazz.isChildOf(it)
                }
            }

        fun PhpClass.tableName(): String {
            val tableField = this.fields.find { it.name == "table" }

            if (ClassUtils.fieldHasDefaultValue(tableField)) {
                return tableField?.defaultValue?.text?.unquoteAndCleanup() ?: this.asTableName()
            }

            return this.asTableName()
        }

        fun PsiElement.isInsideRelationClosure(): Boolean =
            this is ArrayHashElementImpl && this.parentOfType<MethodReferenceImpl>()?.name == "with"

        fun PhpClass.isJoinOrRelation(): Boolean =
            this.fqn == LaravelClasses.JoinClause || this.fqn == LaravelClasses.Relation

        fun MethodReference.isBuilderMethodByName(): Boolean =
            BuilderTableMethods.contains(this.name)

        fun MethodReference.isBuilderMethodForColumns(): Boolean =
            BuilderTableColumnsParams.containsKey(this.name)

        fun MethodReference.isColumnParam(parameters: CompletionParameters): Boolean =
            this.isColumnParam(parameters.position)

        fun MethodReference.isColumnParam(position: PsiElement): Boolean =
            this.isColumnParam(position.findParamIndex())

        fun MethodReference.hasColumnsInAllParams(): Boolean =
            this.isColumnParam(-1)

        fun MethodReference.isColumnParam(index: Int): Boolean =
            BuilderTableColumnsParams[this.name]?.contains(index) ?: false

        fun MethodReference.canHaveAliasParam(): Boolean =
            BuilderTableAliasParams.containsKey(this.name)

        fun CompletionParameters.isColumnIn(method: MethodReference): Boolean =
            this.position.isColumnIn(method)

        fun PsiElement.isColumnIn(method: MethodReference): Boolean =
            method.isColumnParam(this) || method.hasColumnsInAllParams()

        fun MethodReference.canHaveColumnsInArrayValues(): Boolean =
            BuilderMethodsWithTableColumnsInArrayValues.contains(this.name)

        fun CompletionParameters.isInsideRegularFunction(): Boolean =
            this.position.isInsideRegularFunction()

        fun PsiElement.isInsideRegularFunction(): Boolean =
            (this.parent?.parent is FunctionReference && this.parent?.parent !is MethodReference) ||
                (this.parent?.parent?.parent is FunctionReference && this.parent?.parent?.parent !is MethodReference)

        fun PsiElement.isOperatorParam(): Boolean =
            OperatorPositions.contains(this.findParamIndex()) && Operators.any {
                this.textMatches("'$it'") || this.textMatches("\"$it\"")
            }

        fun CompletionParameters.isInsidePhpArrayOrValue(): Boolean =
            this.position.isInsidePhpArrayOrValue()

        fun PsiElement.isInsidePhpArrayOrValue(): Boolean =
            (this.parent?.parent?.isPhpArray() ?: false) ||
                (this.parent?.parent?.isArrayValue() ?: false)

        fun PsiElement.selectsAllColumns(): Boolean =
            this.textContains('*')

        fun CompletionParameters.isTableParam(): Boolean =
            this.position.isTableParam()

        fun PsiElement.isTableParam(): Boolean =
            this.findParamIndex() == 0 // So far all functions accept table as the first argument
    }
}

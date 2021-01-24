package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils.Companion.isChildOf

class LaravelUtils {
    companion object {
        //<editor-fold desc="\Illuminate\Database query builder classes" defaultstate="collapsed">
        @JvmStatic
        val DatabaseBuilderClasses = listOf(
            "\\Illuminate\\Database\\Query\\Builder",
            "\\Illuminate\\Database\\Eloquent\\Builder",
            "\\Illuminate\\Database\\Query\\JoinClause",
            "\\Illuminate\\Database\\Eloquent\\Relations\\Relation",
        )
        //</editor-fold>

        //<editor-fold desc="Query builder methods where table name should be completed" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableMethods = listOf(
            "from",
            "join", "joinWhere",
            "leftJoin", "leftJoinWhere",
            "rightJoin", "rightJoinWhere",
            "crossJoin",
        )
        //</editor-fold>

        //<editor-fold desc="Query builder table methods param indexes where table alias is defined" defaultstate="collapsed">
        @JvmStatic
        val BuilderTableAliasParams = hashMapOf(
            "from" to 1,
            "fromSub" to 1,
            "selectSub" to 1,
        )
        //</editor-fold>

        //<editor-fold desc="Query builder methods and params where columns should be completed" defaultstate="collapsed">
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
        //</editor-fold>

        //<editor-fold desc="Query builder methods where params may accept columns as array values" defaultstate="collapsed">
        @JvmStatic
        val BuilderMethodsWithTableColumnsInArrayValues = listOf(
            "get", "select",
            "whereBetweenColumns", "orWhereBetweenColumns",
            "whereNotBetweenColumns", "orWhereNotBetweenColumns",
        )
        //</editor-fold>

        fun isQueryBuilderMethod(method: MethodReference): Boolean {
            return MethodUtils.resolveMethodClasses(method).any { clazz ->
                DatabaseBuilderClasses.any {
                    clazz.isChildOf(it)
                }
            }
        }
    }

}
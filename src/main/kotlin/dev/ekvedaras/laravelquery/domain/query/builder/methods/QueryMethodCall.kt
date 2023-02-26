package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.classReference
import dev.ekvedaras.laravelquery.support.isMemberOfAny
import dev.ekvedaras.laravelquery.support.resolveClassesFromType
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.tryTransforming

/**
 * Unlike QueryStatementElement,
 * this interface represents only method calls (MethodReference) in query statement.
 */
sealed interface QueryMethodCall : QueryStatementElement {
    override val reference: MethodReference
    override val classReference: ClassReference?
        get() = reference.classReference()

    fun completeFor(parameter: StringParameter): List<LookupElement>

    companion object {
        fun from(reference: MethodReference, queryStatement: QueryStatement): QueryMethodCall? {
            if (!reference.isMemberOfAny(
                    LaravelClasses.QueryBuilder,
                    LaravelClasses.EloquentBuilder,
                    LaravelClasses.DbFacade,
                    LaravelClasses.DbFacadeAlias,
                    LaravelClasses.Model,
                    LaravelClasses.JoinClause,
                    LaravelClasses.Relation
                )) return null

            return when (reference.name) {
                "query", "newQuery", "newModelQuery", "newQueryWithoutRelationships",
                "newQueryWithoutScopes", "newQueryForRestoration", "newEloquentBuilder", "newBaseQueryBuilder" -> QueryCall(reference, queryStatement)

                "create", "forceCreate" -> CreateCall(reference, queryStatement)
                "with" -> WithCall(reference, queryStatement)
                "when" -> WhenCall(reference, queryStatement)
                "from", "table" -> FromCall(reference, queryStatement)
                "join", "joinWhere", "leftJoin", "leftJoinWhere", "rightJoin", "rightJoinWhere", "crossJoin" -> JoinCall(reference, queryStatement)
                "joinSub", "leftJoinSub", "rightJoinSub" -> JoinSubCall(reference, queryStatement)
                "on", "orOn" -> OnCall(reference, queryStatement)
                "where", "orWhere" -> WhereCall(reference, queryStatement)
                "whereColumn", "orWhereColumn" -> WhereColumnCall(reference, queryStatement)
                "whereBetweenColumns", "orWhereBetweenColumns", "whereNotBetweenColumns", "orWhereNotBetweenColumns" -> WhereBetweenColumnsCall(reference, queryStatement)
                "whereIn", "orWhereIn", "orWhereNotIn",
                "whereBetween", "orWhereBetween", "whereNotBetween", "orWhereNotBetween",
                "whereIntegerInRaw", "orWhereIntegerInRaw", "whereIntegerNotInRaw", "orWhereIntegerNotInRaw",
                "whereDate", "orWhereDate", "whereTime", "orWhereTime",
                "whereDay", "orWhereDay", "whereMonth", "orWhereMonth", "whereYear", "orWhereYear",
                "whereJsonContains", "orWhereJsonContains", "whereJsonDoesntContain", "orWhereJsonDoesntContain",
                "whereJsonLength", "orWhereJsonLength", "having", "orHaving", "havingBetween",
                "orderBy", "orderByDesc", "latest", "oldest", "reorder",
                "value", "implode", "min", "max", "sum", "avg", "average",
                "increment", "decrement" -> WhereOneColumnAsFirstParameterIsSomeValueCall(reference, queryStatement)
                "forPageBeforeId", "forPageAfterId" -> ForPageAroundIdCall(reference, queryStatement)
                "get" -> GetCall(reference, queryStatement)
                "select", "groupBy", "addSelect", "whereNull", "orWhereNull", "whereNotNull", "orWhereNotNull" -> SelectCall(reference, queryStatement)
                "whereRowValues", "orWhereRowValues" -> WhereRowValuesCall(reference, queryStatement)
                "find" -> FindCall(reference, queryStatement)
                "paginate", "simplePaginate" -> PaginateCall(reference, queryStatement)
                else -> {
                    if (reference.isMemberOfAny(LaravelClasses.Model)) {
                        return reference.classReference
                            ?.resolveClassesFromType()
                            ?.firstNotNullOfOrNull { clazz ->
                                clazz.tryTransforming { Model(it) }
                                    ?.relation(reference.name ?: "")
                                    .transform { ModelRelationMethodCall(it, reference, queryStatement) }
                            }
                    }

                    null
                }
            }
        }
    }
}

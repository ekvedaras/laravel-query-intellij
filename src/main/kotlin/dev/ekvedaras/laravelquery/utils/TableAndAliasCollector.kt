package dev.ekvedaras.laravelquery.utils

import com.intellij.psi.PsiReference
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.AssignmentExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.ClassReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.ParenthesizedExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.tableName
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.containsAlias
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.statementFirstPsiChild
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup
import java.util.Collections

class TableAndAliasCollector(private val reference: DbReferenceExpression) {
    private val aliasCollector = AliasCollector(reference)
    private val relationResolver = ModelRelationResolver(reference, this)
    private val schemaTableResolver = SchemaTableResolver(reference)

    fun collect() {
        val method = MethodUtils.resolveMethodReference(reference.expression) ?: return
        val methods = Collections.synchronizedList(mutableListOf<MethodReference>())

        collectMethodsAcrossVariableReferences(methods, method)
        collectMethodsInCurrentTree(methods, method)

        relationResolver.resolveModelAndRelationTables(methods, method)
        schemaTableResolver.resolve(methods, method)

        methods
            .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
            .forEach { scanMethodReference(it) }
    }

    private fun collectMethodsAcrossVariableReferences(methods: MutableList<MethodReference>, method: MethodReference) {
        val variable = method.parentOfType<Statement>()?.firstPsiChild?.firstPsiChild
        if (variable !is VariableImpl) return


        val declaration = variable.resolve()
        if (declaration?.reference != null) {
            collectMethodsInVariableReference(declaration.reference as PsiReference, methods)
        }

        variable.references.forEach {
            collectMethodsInVariableReference(it, methods)
        }
    }

    private fun collectMethodsInVariableReference(
        variableReference: PsiReference,
        methods: MutableList<MethodReference>
    ) {
        val element = variableReference.statementFirstPsiChild() ?: return

        // $var = query()->table();
        if (element is AssignmentExpressionImpl && element.lastChild is MethodReference) {
            MethodUtils.findMethodsInTree(element.lastChild).forEach { methods.addUnique(Lifetime.Eternal, it) }
            return
        }

        // $var->where()
        if (element is MethodReference) {
            MethodUtils.findMethodsInTree(
                // $var->where(['relation' => function (Relation $relation) { $relation->where() }])
                // $var->join('table', function (JoinClause $join) { $join->on() })
                if (element.isJoinOrRelation(reference.project)) {
                    element.parent?.parentOfType<Statement>()?.parentOfType<Statement>() ?: return
                } else {
                    element.parent
                }
            ).forEach { methods.addUnique(Lifetime.Eternal, it) }
        }
    }

    private fun collectMethodsInCurrentTree(methods: MutableList<MethodReference>, method: MethodReference) {
        // $var->where(['relation' => function (Relation $relation) { $relation->where() }])
        // $var->join('table', function (JoinClause $join) { $join->on() })
        if (method.isJoinOrRelation(reference.project)) {
            MethodUtils.findMethodsInTree(
                method.parentOfType<Statement>()
                    ?.parentOfType<Statement>()
                    ?.parentOfType<Statement>()
                    ?.firstPsiChild
            ).forEach { methods.addUnique(Lifetime.Eternal, it) }
        } else {
            MethodUtils.findMethodsInTree(method.firstChildOfParentStatement()).forEach {
                methods.addUnique(Lifetime.Eternal, it)
            }
        }

        // Mode::when(true, function (Builder $query) { $query->where(''); });
        if (method.isInsideModelQueryClosure(reference.project)) {
            MethodUtils.findMethodsInTree(method.getParentOfClosure()).forEach {
                methods.addUnique(Lifetime.Eternal, it)
            }
        }
    }

    fun resolveTableName(model: PhpClass) {
        val name = model.tableName()
        reference.tablesAndAliases[name] = name to null
    }

    fun resolveModelReference(methods: MutableList<MethodReference>): PhpTypedElement? {
        if (!methods.none { it.name == "from" }) return null

        // TODO can this be improved with methods like firstPsiChild, nextPsiSibling ?
        return methods.find { isModelReference(it) }?.firstChild as? PhpTypedElement
            ?: methods.find { isNewModelInstance(it) }
                ?.firstChild
                ?.firstChild
                ?.nextSibling
                ?.firstChild
                ?.nextSibling
                ?.nextSibling as? PhpTypedElement
            ?: methods.find { // Inside scope method inside model
                it.isInteresting(it.project) &&
                    it.parentOfType<PhpClassImpl>()?.isChildOf(LaravelClasses.Model) ?: false
            }?.parentOfType<PhpClassImpl>() as? PhpTypedElement
    }

    private fun isNewModelInstance(methodReference: MethodReference): Boolean {
        val classReference = methodReference
            .firstChild
            ?.firstChild
            ?.nextSibling
            ?.firstChild
            ?.nextSibling
            ?.nextSibling as? ClassReferenceImpl

        val isModel = classReference?.getClass(reference.project)?.isChildOf(LaravelClasses.Model) == true

        return methodReference.firstChild is ParenthesizedExpressionImpl && isModel
    }

    private fun isModelReference(methodReference: MethodReference): Boolean {
        return when (methodReference.firstPsiChild) {
            is ClassReferenceImpl -> (methodReference.firstChild as ClassReferenceImpl)
                .getClass(reference.project)
                ?.isChildOf(LaravelClasses.Model) ?: false
            is VariableImpl -> (methodReference.firstChild as VariableImpl)
                .getClass(reference.project)
                ?.isChildOf(LaravelClasses.Model) ?: false
            else -> false
        }
    }

    private fun scanMethodReference(method: MethodReference) {
        if (method.getParameter(0) !is StringLiteralExpressionImpl) {
            return
        }

        var (referencedTable: String, referencedSchema: String?) = extractTableAndSchema(method)

        if (referencedTable.containsAlias()) {
            aliasCollector.extractAliasFromString(method, referencedTable, referencedSchema)
            return
        }

        if (referencedSchema == null) {
            reference.project.dbDataSourcesInParallel().forEach { dataSource ->
                dataSource.tables().firstOrNull { it.name == referencedTable }?.let {
                    referencedSchema = it.dasParent?.name
                }
            }
        }

        aliasCollector.collectAliasFromMethodReference(method, referencedTable, referencedSchema)
    }

    private fun extractTableAndSchema(method: MethodReference): Pair<String, String?> {
        val definition = (method.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

        var referencedTable: String = definition
        var referencedSchema: String? = null

        if (!definition.contains(".")) return Pair(referencedTable, referencedSchema)

        for (part in definition.split(".").reversed()) {
            if (referencedTable == definition) {
                referencedTable = part.unquoteAndCleanup()
            } else {
                referencedSchema = part.unquoteAndCleanup()
            }
        }

        return Pair(referencedTable, referencedSchema)
    }
}

package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle
import dev.ekvedaras.intellijilluminatequerybuilderintegration.reference.ColumnPsiReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class UnknownColumnInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) {
                    return
                }

                val method = MethodUtils.resolveMethodReference(expression) ?: return

                if (shouldNotCompleteCurrentParameter(method, expression)) {
                    return
                }

                if (shouldNotCompleteArrayValue(method, expression)) {
                    return
                }

                if (!LaravelUtils.isQueryBuilderMethod(method)) {
                    return
                }

                val tablesAndAliases = collectTablesAndAliases(method)
                var found = false
                DbUtil.getDataSources(method.project).forEach loop@{ dataSource ->
                    DasUtil.getTables(dataSource.dataSource).forEach { table ->
                        val tableOrAlias = expression.text.substringBefore(".").trim('"').trim('\'')

                        if (!table.isSystem && table.name == tablesAndAliases[tableOrAlias]) {
                            found = DasUtil.getColumns(table)
                                .filter {
                                    it.name == expression.text.substringAfter(".").substringBefore(" as ").trim('"')
                                        .trim('\'')
                                }
                                .isNotEmpty

                            if (found) {
                                return@loop
                            }
                        }
                    }
                }

                if (!found) {
                    holder.registerProblem(
                        expression,
                        MyBundle.message("unknownColumnDescription"),
                        ProblemHighlightType.WARNING
                    )
                }
            }

            private fun shouldNotCompleteCurrentParameter(
                method: MethodReference,
                expression: StringLiteralExpression
            ) =
                LaravelUtils.BuilderTableColumnsParams[method.name]?.contains(
                    MethodUtils.findParameterIndex(expression)
                ) != true

            private fun shouldNotCompleteArrayValue(method: MethodReference, expression: StringLiteralExpression) =
                !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name)
                        && expression.parent.parent.elementType?.index?.toInt() == 1889

            private fun collectTablesAndAliases(method: MethodReference): MutableMap<String, String> {
                val aliases = mutableMapOf<String, String>();

                MethodUtils.findMethodsInTree(method.parentOfType<Statement>()!!.firstChild)
                    .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
                    .forEach loop@{
                        val tableName = (it.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

                        if (tableName.contains(" as ")) {
                            aliases[tableName.substringAfter("as").trim()] = tableName.substringBefore("as").trim()
                            return@loop
                        }

                        if (!LaravelUtils.BuilderTableAliasParams.containsKey(it.name)) {
                            aliases[tableName] = tableName
                            return@loop
                        }

                        val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[it.name] ?: return@loop
                        val alias: String? = (it.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

                        aliases[alias ?: tableName] = tableName
                    }

                return aliases
            }
        }
    }
}
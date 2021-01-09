package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class UnknownTableOrViewInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) {
                    return
                }

                val method = MethodUtils.resolveMethodReference(expression) ?: return

                if (shouldNotCompleteCurrentParam(method, expression)) {
                    return
                }

                if (!LaravelUtils.isQueryBuilderMethod(method)) {
                    return
                }

                var found = false
                DbUtil.getDataSources(method.project).forEach loop@ { dataSource ->
                    found = DasUtil.getTables(dataSource.dataSource)
                        .filter { !it.isSystem && it.name == expression.text.substringBefore(" as ").trim('"').trim('\'') }
                        .isNotEmpty

                    if (found) {
                        return@loop
                    }
                }

                if (!found) {
                    holder.registerProblem(
                        expression,
                        MyBundle.message("unknownTableOrViewDescription"),
                        ProblemHighlightType.WARNING
                    )
                }
            }

            private fun shouldNotCompleteCurrentParam(method: MethodReference, expression: StringLiteralExpression) =
                !LaravelUtils.BuilderTableMethods.contains(method.name)
                        || MethodUtils.findParameterIndex(expression) != 0
        }
    }
}
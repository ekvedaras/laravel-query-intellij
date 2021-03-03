package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class UnknownTableOrViewInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                val method = MethodUtils.resolveMethodReference(expression ?: return) ?: return
                val project = method.project

                if (shouldNotCompleteCurrentParam(method, expression)) {
                    return
                }

                if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
                    return
                }

                val target = DbReferenceExpression(expression, DbReferenceExpression.Companion.Type.Table)

                if (target.table.isEmpty()) {
                    holder.registerProblem(
                        expression,
                        MyBundle.message("unknownTableOrViewDescription"),
                        ProblemHighlightType.WARNING,
                        target.ranges.last()
                    )
                }

                if (target.parts.size > 1 && target.schema.isEmpty()) {
                    holder.registerProblem(
                        expression,
                        MyBundle.message("unknownSchemaDescription"),
                        ProblemHighlightType.WARNING,
                        target.ranges.first()
                    )
                }
            }

            private fun shouldNotCompleteCurrentParam(method: MethodReference, expression: StringLiteralExpression) =
                !LaravelUtils.BuilderTableMethods.contains(method.name) ||
                    MethodUtils.findParameterIndex(expression) != 0 ||
                    (expression.parent?.parent?.parent is FunctionReference && expression.parent?.parent?.parent !is MethodReference) ||
                    (expression.parent?.parent is FunctionReference && expression.parent?.parent !is MethodReference)
        }
    }
}

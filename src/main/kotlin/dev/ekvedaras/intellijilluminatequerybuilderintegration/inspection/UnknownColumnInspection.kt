package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.elementType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
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

                val target = DbReferenceExpression(expression, DbReferenceExpression.Companion.Type.Column)

                if (target.parts.size == 1) {
                    if (target.column.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownColumnDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.first()
                        )
                    }
                } else if (target.parts.size == 2) {
                    if (target.schema.isEmpty() && target.table.isEmpty() && target.column.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownTableOrViewDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.first()
                        )

                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownColumnDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.last()
                        )
                    } else if (target.schema.isEmpty() && target.table.isEmpty() && target.column.isNotEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownTableOrViewDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.first()
                        )
                    } else if (target.schema.isEmpty() && target.table.isNotEmpty() && target.column.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownColumnDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.last()
                        )
                    } else if (target.schema.isNotEmpty() && target.table.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownTableOrViewDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.last()
                        )
                    } else if (target.schema.isEmpty() && target.column.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownSchemaDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.first()
                        )
                    }
                } else if (target.parts.size == 3) {
                    if (target.schema.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownSchemaDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.first()
                        )
                    }

                    if (target.table.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownTableOrViewDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges[1]
                        )
                    }

                    if (target.column.isEmpty()) {
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownColumnDescription"),
                            ProblemHighlightType.WARNING,
                            target.ranges.last()
                        )
                    }
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
        }
    }
}
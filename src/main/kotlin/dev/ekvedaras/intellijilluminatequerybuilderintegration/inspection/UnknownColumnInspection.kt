package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
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
                if ((target.schema == null || target.schema?.name != target.parts[0]) && expression.text.split(".").size > 2) {
                    val length = expression.text.substringAfter(".").length
                    holder.registerProblem(
                        expression,
                        MyBundle.message("unknownSchemaDescription"),
                        ProblemHighlightType.WARNING,
                        TextRange.allOf(expression.text).shiftRight(1).grown(-length - 2)
                    )
                }

                if (target.table == null || target.table?.name != target.parts[expression.text.split(".").size - 2]) {
                    if (expression.text.split(".").size > 2) {
                        val length = expression.text.substringAfterLast(".").length
                        val schemaLength = expression.text.substringBefore(".").length
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownTableOrViewDescription"),
                            ProblemHighlightType.WARNING,
                            TextRange.allOf(expression.text)
                                .shiftRight(schemaLength + 1)
                                .grown(-schemaLength - length - 2)
                        )
                    } else {
                        val length = expression.text.substringAfter(".").length
                        holder.registerProblem(
                            expression,
                            MyBundle.message("unknownSchemaDescription"),
                            ProblemHighlightType.WARNING,
                            TextRange.allOf(expression.text).shiftRight(1).grown(-length - 2)
                        )
                    }
                }

                if (target.column == null) {
                    val length = expression.text.substringBeforeLast(".").length + 1
                    holder.registerProblem(
                        expression,
                        MyBundle.message("unknownColumnDescription"),
                        ProblemHighlightType.WARNING,
                        TextRange.allOf(expression.text).shiftRight(length).grown(-length - 1)
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
        }
    }
}
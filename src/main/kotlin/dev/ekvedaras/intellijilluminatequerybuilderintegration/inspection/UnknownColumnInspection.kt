package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.elementType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.MyBundle"

class UnknownColumnInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                val method = MethodUtils.resolveMethodReference(expression ?: return) ?: return
                val project = method.project

                if (shouldNotCompleteCurrentParameter(method, expression)) {
                    return
                }

                if (shouldNotCompleteArrayValue(method, expression)) {
                    return
                }

                if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
                    return
                }

                val target = DbReferenceExpression(expression, DbReferenceExpression.Companion.Type.Column)

                when (target.parts.size) {
                    1 -> inspectWithOnePart(target, expression)
                    2 -> inspectWithTwoParts(target, expression)
                    else -> inspectWithThreeParts(target, expression)
                }
            }

            private fun registerProblem(
                expression: StringLiteralExpression,
                @PropertyKey(resourceBundle = BUNDLE) key: String,
                range: TextRange
            ) = holder.registerProblem(
                expression,
                MyBundle.message(key),
                ProblemHighlightType.WARNING,
                range
            )

            private fun inspectWithOnePart(
                target: DbReferenceExpression,
                expression: StringLiteralExpression
            ) {
                if (target.column.isEmpty()) {
                    registerProblem(expression, "unknownColumnDescription", target.ranges.first())
                }
            }

            private fun inspectWithTwoParts(
                target: DbReferenceExpression,
                expression: StringLiteralExpression
            ) {
                when {
                    target.schema.isEmpty() && target.table.isEmpty() && target.column.isEmpty() -> {
                        registerProblem(expression, "unknownTableOrViewDescription", target.ranges.first())
                        registerProblem(expression, "unknownColumnDescription", target.ranges.last())
                    }
                    target.schema.isEmpty() && target.table.isEmpty() && target.column.isNotEmpty() -> {
                        registerProblem(expression, "unknownTableOrViewDescription", target.ranges.first())
                    }
                    target.schema.isEmpty() && target.table.isNotEmpty() && target.column.isEmpty() -> {
                        registerProblem(expression, "unknownColumnDescription", target.ranges.last())
                    }
                    target.schema.isNotEmpty() && target.table.isEmpty() -> {
                        registerProblem(expression, "unknownTableOrViewDescription", target.ranges.last())
                    }
                    target.schema.isEmpty() && target.column.isEmpty() -> {
                        registerProblem(expression, "unknownSchemaDescription", target.ranges.first())
                    }
                }
            }

            private fun inspectWithThreeParts(
                target: DbReferenceExpression,
                expression: StringLiteralExpression
            ) {
                if (target.schema.isEmpty()) {
                    registerProblem(expression, "unknownSchemaDescription", target.ranges.first())
                }

                if (target.table.isEmpty()) {
                    registerProblem(expression, "unknownTableOrViewDescription", target.ranges[1])
                }

                if (target.column.isEmpty()) {
                    registerProblem(expression, "unknownColumnDescription", target.ranges.last())
                }
            }

            private fun shouldNotCompleteCurrentParameter(
                method: MethodReference,
                expression: StringLiteralExpression
            ) =
                expression.textContains('$') || // don't inspect variables
                        expression.textContains('*') || // * means all column, no need to inspect
                        MethodUtils.findParameters(expression)?.parameters?.size == 3 && MethodUtils.findParameterIndex(
                    expression
                ) == 1 || // It's an operator argument: <=, =, >=, etc.
                        !LaravelUtils.BuilderTableColumnsParams.containsKey(method.name) ||
                        (
                                !LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(
                                    MethodUtils.findParameterIndex(
                                        expression
                                    )
                                ) && // argument index must be in preconfigured list for the method
                                        !LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(-1)
                                ) || // -1 means any argument should auto complete
                        (expression.parent?.parent?.parent is FunctionReference && expression.parent?.parent?.parent !is MethodReference) || // ->where(DB::raw('column')), etc.
                        (expression.parent?.parent is FunctionReference && expression.parent?.parent !is MethodReference) // ->whereIn('column', explode(' ' , 'string')), etc. todo: make this check and above work together and be more flexible

            private fun shouldNotCompleteArrayValue(method: MethodReference, expression: StringLiteralExpression) =
                !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name) &&
                        (
                                expression.parent.parent.elementType?.index?.toInt() == 1889 || // 1889 - array expression
                                        expression.parent.parent.elementType?.index?.toInt() == 805
                                ) // 805 - array value
        }
    }
}

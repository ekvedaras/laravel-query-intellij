package dev.ekvedaras.laravelquery.inspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.laravelquery.MyBundle
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.canHaveColumnsInArrayValues
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForColumns
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isColumnIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsidePhpArrayOrValue
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isOperatorParam
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isSchemaBuilderMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.selectsAllColumns
import dev.ekvedaras.laravelquery.utils.MethodUtils
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.containsVariable
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

                if (shouldNotInspect(project, method, expression)) {
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

            private fun shouldNotInspect(
                project: Project,
                method: MethodReference,
                expression: StringLiteralExpression
            ): Boolean {
                val allowArray = method.name?.startsWith("where") ?: false

                return !ApplicationManager.getApplication().isReadAccessAllowed ||
                    expression.containsVariable() ||
                    expression.selectsAllColumns() ||
                    expression.isOperatorParam(allowArray) ||
                    !method.isBuilderMethodForColumns() ||
                    !expression.isColumnIn(method, allowArray) ||
                    expression.isInsideRegularFunction() ||
                    (expression.isInsidePhpArrayOrValue() && !method.canHaveColumnsInArrayValues()) ||
                    !method.isInteresting(project) ||
                    method.isSchemaBuilderMethod(project) ||
                    method.isBlueprintMethod(project)
            }
        }
    }
}

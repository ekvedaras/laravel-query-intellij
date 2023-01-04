package dev.ekvedaras.laravelquery.v4.inspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.ArrayIndexImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.laravelquery.MyBundle
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForTableByName
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isDatabaseAssertion
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isSchemaBuilderMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isTableParam
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isTestCase
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.shouldCompleteOnlyColumns
import dev.ekvedaras.laravelquery.utils.MethodUtils

class UnknownTableOrViewInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                val method = MethodUtils.resolveMethodReference(expression ?: return) ?: return
                val project = method.project

                if (shouldNotInspect(project, method, expression)) {
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

            private fun shouldNotInspect(
                project: Project,
                method: MethodReference,
                expression: StringLiteralExpression
            ) =
                !ApplicationManager.getApplication().isReadAccessAllowed ||
                    expression.parent is ArrayIndexImpl ||
                    !method.isBuilderMethodForTableByName() ||
                    method.shouldCompleteOnlyColumns() ||
                    !expression.isTableParam() ||
                    expression.isInsideRegularFunction() ||
                    (method.isTestCase(project) && !method.isDatabaseAssertion(project)) ||
                    !method.isInteresting(project) ||
                    method.isSchemaBuilderMethod(project) ||
                    method.isBlueprintMethod(project)
        }
    }
}

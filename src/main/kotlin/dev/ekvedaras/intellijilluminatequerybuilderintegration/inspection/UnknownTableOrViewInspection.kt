package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import dev.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderClassMethod
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderMethodByName
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isTableParam
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

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
                !method.isBuilderMethodByName() ||
                    !expression.isTableParam() ||
                    expression.isInsideRegularFunction() ||
                    !method.isBuilderClassMethod(project)
        }
    }
}

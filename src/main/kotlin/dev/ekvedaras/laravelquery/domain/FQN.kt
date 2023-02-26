package dev.ekvedaras.laravelquery.domain

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.support.cleanup
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.whenSmart

data class FQN(val fqn: String, val project: Project) {
    val clazz: PhpClass?
        get() = project.whenSmart {
            PhpIndex.getInstance(project)
                .getClassesByFQN(fqn)
                .firstOrNull()
        }

    companion object {
        fun from(element: StringLiteralExpression) = FQN(element.text.cleanup(), element.project)
        fun from(element: ClassReference): FQN? = element.fqn.transform { FQN(it, element.project) }
    }
}

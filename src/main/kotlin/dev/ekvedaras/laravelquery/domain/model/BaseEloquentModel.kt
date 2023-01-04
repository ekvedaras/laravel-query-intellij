package dev.ekvedaras.laravelquery.domain.model

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.ekvedaras.laravelquery.support.transform

private const val EloquentModelFqn = "\\Illuminate\\Database\\Eloquent\\Model"

class BaseEloquentModel private constructor(val clazz: PhpClass) {
    companion object {
        fun find(project: Project): BaseEloquentModel? =
            if (DumbService.isDumb(project)) null
            else PhpIndex.getInstance(project)
                .getClassesByFQN(EloquentModelFqn)
                .firstOrNull()
                .transform { BaseEloquentModel(it) }
    }
}

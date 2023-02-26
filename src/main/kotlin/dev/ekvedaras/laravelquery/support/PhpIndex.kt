package dev.ekvedaras.laravelquery.support

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpTypedElement

fun PhpTypedElement.resolveClassesFromType(): List<PhpClass> = whenSmart {
    PhpIndex.getInstance(project)
        .completeType(project, type, mutableSetOf())
        .types
        .flatMap { PhpIndex.getInstance(project).getClassesByFQN(it) }
} ?: listOf()

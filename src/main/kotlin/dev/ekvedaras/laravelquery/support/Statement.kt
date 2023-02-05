package dev.ekvedaras.laravelquery.support

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement

fun Statement.referenceVariable() = callChainOfType<MethodReference>().firstOrNull()?.referenceVariable()

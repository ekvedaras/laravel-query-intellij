package dev.ekvedaras.laravelquery.support

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference

fun Function.isInCallOfMethodNamed(name: String) = parentOfType<MethodReference>()?.name == name

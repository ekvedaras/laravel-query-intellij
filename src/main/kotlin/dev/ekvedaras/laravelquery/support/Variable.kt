package dev.ekvedaras.laravelquery.support

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Variable

fun Variable.isInsideCallOfMethod(name: String) = parentOfType<Function>()?.isInCallOfMethodNamed(name) == true
fun Variable.isFirstParameter() = parentOfType<Function>()?.getParameter(0)?.name == name

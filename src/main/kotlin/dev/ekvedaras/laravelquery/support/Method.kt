package dev.ekvedaras.laravelquery.support

import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParenthesizedExpression

fun MethodReference.classReference(): ClassReference? =
    if (this.firstPsiChild is ClassReference) this.firstPsiChild as ClassReference
    else if (this.firstPsiChild is ParenthesizedExpression && this.firstPsiChild?.firstPsiChild is NewExpression && this.firstPsiChild?.firstPsiChild?.firstPsiChild is ClassReference) this.firstPsiChild!!.firstPsiChild!!.firstPsiChild as ClassReference
    else null

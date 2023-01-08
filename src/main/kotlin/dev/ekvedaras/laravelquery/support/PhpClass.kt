package dev.ekvedaras.laravelquery.support

import com.jetbrains.php.lang.psi.elements.PhpClass

fun PhpClass.isChildOf(superFqn: String): Boolean = this.superFQN == superFqn || this.superClass?.isChildOf(superFqn) == true

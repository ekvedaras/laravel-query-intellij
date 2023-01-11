package dev.ekvedaras.laravelquery.support

import com.jetbrains.php.lang.psi.elements.PhpClass

fun PhpClass.isChildOfAny(vararg superFqn: String, orIsAny: Boolean = false): Boolean = superFqn.contains(this.superFQN) || (orIsAny && superFqn.contains(fqn)) || this.superClass?.isChildOfAny(*superFqn, orIsAny = orIsAny) == true

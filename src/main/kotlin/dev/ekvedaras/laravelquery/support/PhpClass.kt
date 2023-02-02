package dev.ekvedaras.laravelquery.support

import com.jetbrains.php.lang.psi.elements.PhpClass

fun PhpClass.isChildOfAny(vararg superFqn: String, orIsAny: Boolean = false): Boolean = superFqn.contains(superFQN) || (orIsAny && superFqn.contains(fqn)) || superClass?.isChildOfAny(*superFqn, orIsAny = orIsAny) == true

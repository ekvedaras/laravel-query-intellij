package dev.ekvedaras.laravelquery.support

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement

/**
 * In case of `$query->from('users')->where('id', 1)->get();` we are fetching
 * `$query->from('users')` which counterintuitively will be the deepest nested method reference.
 */
fun Statement.referenceVariable() = descendantsOfType<MethodReference>().lastOrNull()?.referenceVariable()

package dev.ekvedaras.laravelquery.utils

import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpExpression
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

class SchemaMethod private constructor() {
    companion object {
        fun MethodReference.isCreateOrTable() = this.name == "create" || this.name == "table"
        fun MethodReference.tableName() = (this.firstPsiChild?.nextPsiSibling as? ParameterList)?.getParameter(0)?.text?.unquoteAndCleanup()
        fun MethodReference.blueprintTableParam() = (((this.firstPsiChild?.nextPsiSibling as? ParameterList)?.getParameter(1) as PhpExpression).firstPsiChild as Function).getParameter(0) as? Parameter
    }
}

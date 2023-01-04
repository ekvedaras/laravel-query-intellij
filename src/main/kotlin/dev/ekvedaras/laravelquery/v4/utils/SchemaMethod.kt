package dev.ekvedaras.laravelquery.v4.utils

import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpExpression
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.rd.util.first
import dev.ekvedaras.laravelquery.v4.utils.PsiUtils.Companion.unquoteAndCleanup
import dev.ekvedaras.laravelquery.v4.utils.SchemaMethod.Companion.isCreateOrTable
import dev.ekvedaras.laravelquery.v4.utils.SchemaMethod.Companion.tableName

class SchemaMethod private constructor() {
    companion object {
        fun MethodReference.isCreateOrTable() = this.name == "create" || this.name == "table"
        fun MethodReference.tableName() = (this.firstPsiChild?.nextPsiSibling as? ParameterList)?.getParameter(0)?.text?.unquoteAndCleanup()
        fun MethodReference.blueprintTableParam() = (((this.firstPsiChild?.nextPsiSibling as? ParameterList)?.getParameter(1) as PhpExpression).firstPsiChild as Function).getParameter(0) as? Parameter
        fun Method.statementsForTable(table : String) = this
            .children.first { it is GroupStatement }
            .children.filterIsInstance<Statement>()
            .mapNotNull { it.firstPsiChild as? MethodReference }
            .filter { it.isCreateOrTable() }
            .filter { (it.tableName() ?: "") == table }
    }
}

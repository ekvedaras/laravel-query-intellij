package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class ColumnReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY

        if (shouldNotCompleteCurrentParameter(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (shouldNotCompleteArrayValue(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (!LaravelUtils.isQueryBuilderMethod(method)) {
            return PsiReference.EMPTY_ARRAY
        }

        var references = arrayOf<PsiReference>()
        val tablesAndAliases = collectTablesAndAliases(method)

        // TODO optimize so we don't need to loop all tables and if possible all columns and filter. Try to use find()
        DbUtil.getDataSources(element.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource).forEach { table ->
                val tableOrAlias = element.text.substringBefore(".").trim('"').trim('\'')

                if (!table.isSystem && table.name == tablesAndAliases[tableOrAlias]) {
                    DasUtil.getColumns(table)
                        .filter { it.name == element.text.substringAfter(".").substringBefore(" as ").trim('"').trim('\'') }
                        .forEach { references += ColumnPsiReference(element, it) }
                }
            }
        }

        return references
    }

    private fun shouldNotCompleteCurrentParameter(method: MethodReference, element: PsiElement) =
        LaravelUtils.BuilderTableColumnsParams[method.name]?.contains(
            MethodUtils.findParameterIndex(element)
        ) != true

    private fun shouldNotCompleteArrayValue(method: MethodReference, element: PsiElement) =
        !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name)
                && element.parent.parent.elementType?.index?.toInt() == 1889

    // TODO: This is duplicated code with ColumnCompletionProvider. Extract somewhere or reference.
    private fun collectTablesAndAliases(method: MethodReference): MutableMap<String, String> {
        val aliases = mutableMapOf<String, String>();

        MethodUtils.findMethodsInTree(method.parentOfType<Statement>()!!.firstChild)
            .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
            .forEach loop@{
                val tableName = (it.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

                if (tableName.contains(" as ")) {
                    aliases[tableName.substringAfter("as").trim()] = tableName.substringBefore("as").trim()
                    return@loop
                }

                if (!LaravelUtils.BuilderTableAliasParams.containsKey(it.name)) {
                    aliases[tableName] = tableName
                    return@loop
                }

                val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[it.name] ?: return@loop
                val alias: String? = (it.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

                aliases[alias ?: tableName] = tableName
            }

        return aliases
    }
}
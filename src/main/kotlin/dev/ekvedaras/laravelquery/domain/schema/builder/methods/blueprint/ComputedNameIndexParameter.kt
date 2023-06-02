package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.psi.DbIndex
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.support.transform

class ComputedNameIndexParameter(table : MigrationTable, vararg columns: StandaloneColumnParameter) : MigratedIndex {
    override val name = "${table.name}_${columns.joinToString("_") { it.stringParameter.text }}_index"
}

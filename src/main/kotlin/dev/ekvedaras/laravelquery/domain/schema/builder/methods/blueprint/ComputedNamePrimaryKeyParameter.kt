package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable

class ComputedNamePrimaryKeyParameter(table: MigrationTable, vararg columns: StandaloneColumnParameter) : MigratedTableKey {
    override val name = "${table.name}_${columns.joinToString("_") { it.stringParameter.text }}_primary"
    override val isPrimary = true
}

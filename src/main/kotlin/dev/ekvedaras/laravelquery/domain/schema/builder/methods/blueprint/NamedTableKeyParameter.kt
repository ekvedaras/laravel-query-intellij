package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import dev.ekvedaras.laravelquery.domain.StringParameter

data class NamedTableKeyParameter(val stringParameter: StringParameter, override val isPrimary: Boolean) : MigratedTableKey {
    override val name = stringParameter.text
}

package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import dev.ekvedaras.laravelquery.domain.StringParameter

class AliasParameter(val stringParameter: StringParameter) {
    val name: String = stringParameter.text
}

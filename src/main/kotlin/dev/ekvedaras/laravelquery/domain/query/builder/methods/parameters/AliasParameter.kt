package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import dev.ekvedaras.laravelquery.domain.StringParameter

class AliasParameter(val element: StringParameter) {
    val name: String = element.text
}

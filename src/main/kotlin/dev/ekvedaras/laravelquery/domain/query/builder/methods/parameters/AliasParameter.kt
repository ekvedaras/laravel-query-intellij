package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

class AliasParameter(val element: StringParameter) {
    val name: String = element.text
}

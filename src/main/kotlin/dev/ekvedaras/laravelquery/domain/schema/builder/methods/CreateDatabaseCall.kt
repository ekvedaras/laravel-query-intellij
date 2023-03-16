package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbNamespace
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.Migration
import dev.ekvedaras.laravelquery.support.returnWhen
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class CreateDatabaseCall(override val reference: MethodReference, override val migration: Migration) : SchemaBuilderMethodCall, MigratesNamespace {
    override val namespaceParameter = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, NamespaceParameter> {
        NamespaceParameter(it.asStringParameter())
    }

    override fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace? = returnWhen(parameter.equals(namespaceParameter), namespaceParameter?.namespace?.asDbNamespace())
    override fun completeFor(parameter: StringParameter): List<LookupElement> = returnWhen(parameter.equals(namespaceParameter)) {
        namespaceParameter?.getCompletionOptions()
    } ?: listOf()
}

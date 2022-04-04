package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForTableByName
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isDatabaseAssertion
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isEloquentModel
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isSchemaBuilderMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isTableParam
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isTestCase
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.shouldCompleteOnlyColumns
import dev.ekvedaras.laravelquery.utils.MethodUtils
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.references
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup
import dev.ekvedaras.laravelquery.utils.isJoinOrRelation
import java.util.Collections

class NewMigrationCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return
        val project = method.project

        if (shouldNotComplete(project, method, parameters)) {
            return
        }

        val items = Collections.synchronizedList(mutableListOf<LookupElement>())

        if (ApplicationManager.getApplication().isReadAccessAllowed) {
            ApplicationManager.getApplication().runReadAction {
                (method.firstPsiChild as VariableImpl).references().forEach { reference ->
                    val referenceMethod = (reference.element as VariableImpl).parent as MethodReferenceImpl
                    if (referenceMethod.name == "id") {
                        items.add(LookupElementBuilder.create("id").withLookupString("id").withPsiElement(referenceMethod))
                    } else if (referenceMethod.name == "timestamps") {
                        items.add(LookupElementBuilder.create("created_at").withLookupString("created_at").withPsiElement(referenceMethod))
                        items.add(LookupElementBuilder.create("updated_at").withLookupString("updated_at").withPsiElement(referenceMethod))
                    } else if (referenceMethod.name == "softDeletes") {
                        items.add(LookupElementBuilder.create("deleted_at").withLookupString("deleted_at").withPsiElement(referenceMethod))
                    } else if ( listOf(
                            "increments",
                            "integerIncrements",
                            "tinyIncrements",
                            "mediumIncrements",
                            "bigIncrements",
                            "char",
                            "string",
                            "text",
                            "mediumText",
                            "longText",
                            "integer",
                            "tinyInteger",
                            "smallInteger",
                            "mediumInteger",
                            "bigInteger",
                            "unsignedInteger",
                            "unsignedTinyInteger",
                            "unsignedSmallInteger",
                            "unsignedMediumInteger",
                            "unsignedBigInteger",
                            "foreignId",
//                            "foreignIdFor", // Needs custom handling
                            "float",
                            "double",
                            "decimal",
                            "unsignedFloat",
                            "unsignedDouble",
                            "unsignedDecimal",
                            "boolean",
                            "enum",
                            "set",
                            "json",
                            "jsonb",
                            "date",
                            "dateTime",
                            "dateTimeTz",
                            "time",
                            "timeTz",
                            "timestamp",
                            "timestampTz",
                            "softDeletes",
                            "softDeletesTz",
                            "year",
                            "binary",
                            "uuid",
                            "foreignUuid",
                            "ipAddress",
                            "macAddress",
                            "geometry",
                            "point",
                            "lineString",
                            "polygon",
                            "geometryCollection",
                            "multiPoint",
                            "multiLineString",
                            "multiPolygon",
                            "multiPolygonZ",
                            "computed",
                    ).contains(referenceMethod.name)) {
                        items.add(LookupElementBuilder.create(referenceMethod.firstPsiChild?.nextPsiSibling?.firstPsiChild?.text?.unquoteAndCleanup() ?: "?").withPsiElement(referenceMethod.firstPsiChild?.nextPsiSibling?.firstPsiChild))
                    }
                }
                // Find $table param
                // Collect all references of $table param
                // Parse each reference to build list of columns and indexes added / renamed in this migration
                // Maybe: exclude columns that are already on the table
                // Populate items collection with lookup items
            }
        }

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )

        result.stopHere()
    }

    private fun shouldNotComplete(project: Project, method: MethodReference, parameters: CompletionParameters) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            !method.isBlueprintMethod(project) ||
            parameters.isInsideRegularFunction() ||
            method.firstPsiChild !is VariableImpl
}

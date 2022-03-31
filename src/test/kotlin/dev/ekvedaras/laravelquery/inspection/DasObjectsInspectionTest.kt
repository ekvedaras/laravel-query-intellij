package dev.ekvedaras.laravelquery.inspection

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings

internal class DasObjectsInspectionTest : BaseTestCase() {
    fun testWarnsAboutUnknownSchema() {
        assertInspection("inspection/unknownSchema.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownTable() {
        assertInspection("inspection/unknownTable.php", UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownTable() {
        assertInspection("inspection/knownTable.php", UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownTableWhenUsingPrefixes() {
        LaravelQuerySettings.getInstance(project).tablePrefix = "failed_"
        assertInspection("inspection/knownWithPrefixTable.php", UnknownTableOrViewInspection())
        LaravelQuerySettings.getInstance(project).tablePrefix = ""
    }

    fun testWarnsAboutUnknownSchemaTable() {
        assertInspection("inspection/unknownSchemaTable.php", UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownSchemaTable() {
        assertInspection("inspection/knownSchemaTable.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutTableFromOtherSchema() {
        assertInspection("inspection/tableFromOtherSchema.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownColumn() {
        assertInspection("inspection/unknownColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownColumn() {
        assertInspection("inspection/knownColumn.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownTableColumn() {
        assertInspection("inspection/unknownTableColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownTableColumn() {
        assertInspection("inspection/knownTableColumn.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownSchemaTableColumn() {
        assertInspection("inspection/unknownSchemaTableColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownSchemaTableColumn() {
        assertInspection("inspection/knownSchemaTableColumn.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownAliasColumn() {
        assertInspection("inspection/unknownAliasColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownAliasColumn() {
        assertInspection("inspection/knownAliasColumn.php", UnknownColumnInspection())
        assertInspection("inspection/knownAliasColumn.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutColumnFromOtherTable() {
        assertInspection("inspection/columnFromOtherTable.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownTableAndColumn() {
        assertInspection("inspection/unknownTableAndColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutOperatorInJoinCallWithOperator() {
        assertInspection("inspection/joinWithOperator.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutUnknownColumnForWhereInMethodValuesList() {
        assertInspection("inspection/noInspectionsForValuesInWhereIn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutUnknownColumnForNestedArrayKeys() {
        assertInspection("inspection/noInspectionsForNestedArrayKeys.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownJsonColumn() {
        assertInspection("inspection/knownJsonColumn.php", UnknownColumnInspection())
    }
}

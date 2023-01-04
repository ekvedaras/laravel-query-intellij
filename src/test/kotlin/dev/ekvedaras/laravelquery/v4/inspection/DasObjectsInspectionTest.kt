package dev.ekvedaras.laravelquery.v4.inspection

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.v4.inspection.UnknownColumnInspection
import dev.ekvedaras.laravelquery.v4.inspection.UnknownTableOrViewInspection

internal class DasObjectsInspectionTest : BaseTestCase() {
    fun testWarnsAboutUnknownSchema() {
        assertInspection("v4/inspection/unknownSchema.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownTable() {
        assertInspection("v4/inspection/unknownTable.php", UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownTable() {
        assertInspection("v4/inspection/knownTable.php", UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownTableWhenUsingPrefixes() {
        useTablePrefix("failed_")
        assertInspection("v4/inspection/knownWithPrefixTable.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownTableWhenUsingPrefixes() {
        useTablePrefix("failed")
        assertInspection("v4/inspection/unknownWithPrefixTable.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownSchemaTable() {
        assertInspection("v4/inspection/unknownSchemaTable.php", UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownSchemaTable() {
        assertInspection("v4/inspection/knownSchemaTable.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutTableFromOtherSchema() {
        assertInspection("v4/inspection/tableFromOtherSchema.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownColumn() {
        assertInspection("v4/inspection/unknownColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownColumn() {
        assertInspection("v4/inspection/knownColumn.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownTableColumn() {
        assertInspection("v4/inspection/unknownTableColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownTableColumn() {
        assertInspection("v4/inspection/knownTableColumn.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownSchemaTableColumn() {
        assertInspection("v4/inspection/unknownSchemaTableColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownSchemaTableColumn() {
        assertInspection("v4/inspection/knownSchemaTableColumn.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownAliasColumn() {
        assertInspection("v4/inspection/unknownAliasColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownAliasColumn() {
        assertInspection("v4/inspection/knownAliasColumn.php", UnknownColumnInspection())
        assertInspection("v4/inspection/knownAliasColumn.php", UnknownTableOrViewInspection())
    }

    fun testWarnsAboutColumnFromOtherTable() {
        assertInspection("v4/inspection/columnFromOtherTable.php", UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownTableAndColumn() {
        assertInspection("v4/inspection/unknownTableAndColumn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutOperatorInJoinCallWithOperator() {
        assertInspection("v4/inspection/joinWithOperator.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutUnknownColumnForWhereInMethodValuesList() {
        assertInspection("v4/inspection/noInspectionsForValuesInWhereIn.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutUnknownColumnForNestedArrayKeys() {
        assertInspection("v4/inspection/noInspectionsForNestedArrayKeys.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownJsonColumn() {
        assertInspection("v4/inspection/knownJsonColumn.php", UnknownColumnInspection())
    }

    fun testItDoesNotWarnAboutUnknownColumnInObjectsAsCreateFunctionValues() {
        assertInspection("v4/inspection/newObjectWithinCreateValue.php", UnknownColumnInspection())
    }
}

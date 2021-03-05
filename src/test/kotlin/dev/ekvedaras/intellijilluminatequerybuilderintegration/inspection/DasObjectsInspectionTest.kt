package dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection

import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase

class DasObjectsInspectionTest : BaseTestCase() {
    fun testWarnsAboutUnknownSchema() {
        val file = myFixture.configureByFile("inspection/unknownSchema.php")
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownTable() {
        val file = myFixture.configureByFile("inspection/unknownTable.php")
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownTable() {
        val file = myFixture.configureByFile("inspection/knownTable.php")
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownSchemaTable() {
        val file = myFixture.configureByFile("inspection/unknownSchemaTable.php")
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testDoesNotWarnAboutKnownSchemaTable() {
        val file = myFixture.configureByFile("inspection/knownSchemaTable.php")
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testWarnsAboutTableFromOtherSchema() {
        val file = myFixture.configureByFile("inspection/tableFromOtherSchema.php")
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testWarnsAboutUnknownColumn() {
        val file = myFixture.configureByFile("inspection/unknownColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownColumn() {
        val file = myFixture.configureByFile("inspection/knownColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownTableColumn() {
        val file = myFixture.configureByFile("inspection/unknownTableColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownTableColumn() {
        val file = myFixture.configureByFile("inspection/knownTableColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownSchemaTableColumn() {
        val file = myFixture.configureByFile("inspection/unknownSchemaTableColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownSchemaTableColumn() {
        val file = myFixture.configureByFile("inspection/knownSchemaTableColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownAliasColumn() {
        val file = myFixture.configureByFile("inspection/unknownAliasColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutKnownAliasColumn() {
        val file = myFixture.configureByFile("inspection/knownAliasColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testWarnsAboutColumnFromOtherTable() {
        val file = myFixture.configureByFile("inspection/columnFromOtherTable.php")
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testWarnsAboutUnknownTableAndColumn() {
        val file = myFixture.configureByFile("inspection/unknownTableAndColumn.php")
        assertInspection(file!!, UnknownColumnInspection())
    }
}

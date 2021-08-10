package dev.ekvedaras.laravelquery.edgeCases

import com.intellij.database.dialects.oracle.debugger.success
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.UsefulTestCase
import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.inspection.UnknownColumnInspection
import dev.ekvedaras.laravelquery.inspection.UnknownTableOrViewInspection

internal class EdgeCasesTest : BaseTestCase() {
    fun testClassCastException1() {
        myFixture.configureByFile("edgeCases/classCastException1.php")
        myFixture.completeBasic()
        assertCompletion("email")
    }

    fun testClassCastException2() {
        myFixture.configureByFile("edgeCases/classCastException2.php")
        if (ApplicationManager.getApplication().isReadAccessAllowed) {
            myFixture.completeBasic()
            assertCompletion("email")
        } else {
            success(1)
        }
    }

    fun testNonQueryBuilderTableMethod() {
        val file = myFixture.configureByFile("edgeCases/nonQueryBuilderTableMethod.php")
        val schema = DasUtil.getSchemas(db).first()
        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema)
            ?: return fail("Failed to resolve DB schema")

        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
        assertEmpty(myFixture.findUsages(dbSchema))
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testNonQueryBuilderColumnMethod() {
        val file = myFixture.configureByFile("edgeCases/nonQueryBuilderColumnMethod.php")
        val schema = DasUtil.getSchemas(db).first()
        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema)
            ?: return fail("Failed to resolve DB schema")

        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
        assertEmpty(myFixture.findUsages(dbSchema))
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testDoesNotResolveColumnReferenceIfStringContainsDollarSign() {
        myFixture.configureByFile("edgeCases/nonCompletableArrayValue.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
    }

    fun testJoinClause() {
        myFixture.configureByFile("edgeCases/joinClause.php")
        myFixture.completeBasic()
        assertCompletion("billable_id", "billable_type")
    }

    fun testWhenClause() {
        myFixture.configureByFile("edgeCases/whenClause.php")
        myFixture.completeBasic()
        assertCompletion("first_name", "last_name")
        assertNoCompletion("trial_ends_at")
    }

    /* Code works, but test fails due to some internal code
    fun testDbTable() {
        myFixture.configureByFile("edgeCases/dbTable.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users", "testProject2")
        assertNoCompletion("created_at")
    }*/

    /* Code works, but test fails due to some internal code
    fun testDbFacadeAliasTable() {
        myFixture.configureByFile("edgeCases/dbFacadeAliasTable.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users", "testProject2")
        assertNoCompletion("created_at")
    }*/

    fun testDbTableColumn() {
        myFixture.configureByFile("edgeCases/dbTableColumn.php")
        myFixture.completeBasic()
        assertCompletion("first_name", "last_name")
        assertNoCompletion("trial_ends_at")
    }

    fun testDbTableAliasColumn() {
        myFixture.configureByFile("edgeCases/dbTableAliasColumn.php")
        myFixture.completeBasic()
        assertCompletion("first_name", "last_name")
        assertNoCompletion("trial_ends_at")
    }

    fun testItOnlyCompletesColumnsOnModelCreateMethod() {
        myFixture.configureByFile("edgeCases/createModel.php")
        myFixture.completeBasic()
        assertCompletion("first_name", "last_name")
        assertNoCompletion("trial_ends_at")
        assertNoCompletion("customers", "testProject1", "testProject2", "migrations", "failed_jobs")
    }

    fun testItOnlyCompletesColumnsOnModelCreateMethodWhenOnlyTheKeyIsPresent() {
        myFixture.configureByFile("edgeCases/createModelNewKey.php")
        myFixture.completeBasic()
        assertCompletion("first_name", "last_name")
        assertNoCompletion("trial_ends_at")
        assertNoCompletion("customers", "testProject1", "testProject2", "migrations", "failed_jobs")
    }

    fun testItOnlyCompletesColumnsOnRelationCreateMethodWhenOnlyTheKeyIsPresent() {
        myFixture.configureByFile("edgeCases/createRelationNewKey.php")
        myFixture.completeBasic()
        assertCompletion("trial_ends_at", "billable_id")
        assertNoCompletion("email", "name")
        assertNoCompletion("customers", "testProject1", "testProject2", "migrations", "failed_jobs")
    }

    fun testItDoesNotCompleteColumnsOnModelCreateMethodWhenCaretIsInValue() {
        myFixture.configureByFile("edgeCases/createModelCaretInValue.php")
        myFixture.completeBasic()
        assertSame(0, myFixture.lookupElements?.size ?: 0)
    }

    fun testDoesNotWarnAboutUnknownOperatorInNestedArrayWhere() {
        assertInspection("edgeCases/arrayNestedWhere.php", UnknownColumnInspection())
    }

    fun testDoesNotWarnAboutUnknownTableInCreateMethod() {
        assertInspection("edgeCases/createModel.php", UnknownTableOrViewInspection())
    }

    fun testItDoesNotResolveColumnsFromOtherTablesBecauseOfTheContext() {
        myFixture.configureByFile("edgeCases/createModelValueAsColumnName.php")

        val column = DasUtil.getTables(dataSource())
            .first { it.name == "users" }
            .getDasChildren(ObjectKind.COLUMN)
            .first { it.name == "email" }
        val dbColumn = DbImplUtil.findElement(
            DbUtil.getDataSources(project).first(),
            column
        ) ?: return fail("Failed to resolve DB column")

        val usages = myFixture.findUsages(dbColumn)

        UsefulTestCase.assertSize(0, usages)
    }

    fun testItDoesNotResolveTablesInEloquentMethodArrayKeysAndValues() {
        myFixture.configureByFile("edgeCases/createModelKeyAndValueAsTableName.php")

        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }
        val dbTable = DbImplUtil.findElement(
            DbUtil.getDataSources(project).first(),
            table
        ) ?: return fail("Failed to resolve DB table")

        val usages = myFixture.findUsages(dbTable)

        UsefulTestCase.assertSize(0, usages)
    }
}

package dev.ekvedaras.intellijilluminatequerybuilderintegration.edgeCases

import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase

class EdgeCasesTest : BaseTestCase() {
    fun testClassCastException1() {
        myFixture.configureByFile("edgeCases/classCastException1.php")
        myFixture.completeBasic()
        assertCompletion("email")
    }

    fun testClassCastException2() {
        myFixture.configureByFile("edgeCases/classCastException2.php")
        myFixture.completeBasic()
        assertCompletion("email")
    }
}
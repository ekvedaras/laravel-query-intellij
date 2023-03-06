package dev.ekvedaras.laravelquery.domain.schema

import com.jetbrains.php.lang.psi.elements.Statement

data class MigrationStatement(val statement: Statement) {
}

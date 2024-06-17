package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getPair

class ControlStructureChecker : VoidVisitorAdapter<Void>() {
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    override fun visit(ifStmt: IfStmt, arg: Void?) {
        super.visit(ifStmt, arg)
        checkMisplacedSemicolon(ifStmt, "if statement")
        ifStmt.thenStmt.accept(this, arg)
        ifStmt.elseStmt.ifPresent { stmt: Statement ->
            stmt.accept(
                this,
                arg
            )
        }
    }

    override fun visit(forStmt: ForStmt, arg: Void?) {
        super.visit(forStmt, arg)
        checkMisplacedSemicolon(forStmt, "for statement")
        forStmt.body.accept(this, arg)
    }

    override fun visit(whileStmt: WhileStmt, arg: Void?) {
        super.visit(whileStmt, arg)
        checkMisplacedSemicolon(whileStmt, "while statement")
        whileStmt.body.accept(this, arg)
    }

    private fun checkMisplacedSemicolon(stmt: Statement, stmtType: String) {
        val regex = """\b(if|while|for|switch|do)\s*\(?.*?\)?\s*;""".toRegex()
        if (stmt.toString().matches(regex)) {
            val begin = stmt.begin.getPair()
            val end = stmt.end.getPair()
            issues += RuleType.ControlStructureType(stmtType, begin)
        }
    }
}
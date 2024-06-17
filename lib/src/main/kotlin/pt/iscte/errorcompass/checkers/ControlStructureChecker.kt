package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getPair

/**
 * ControlStructureChecker is a visitor class that extends VoidVisitorAdapter to check for misplaced semicolons
 * in control structures (if, for, while statements). It stores the identified issues in a list.
 */
class ControlStructureChecker : VoidVisitorAdapter<Void>() {
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    /**
     * Visits IfStmt nodes in the AST, checks for misplaced semicolons, and recursively visits then and else statements.
     *
     * @param ifStmt the if statement node to visit
     * @param arg additional argument (not used)
     */
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

    /**
     * Visits ForStmt nodes in the AST, checks for misplaced semicolons, and recursively visits the body of the while statement.
     *
     * @param forStmt the while statement node to visit
     * @param arg additional argument (not used)
     */
    override fun visit(forStmt: ForStmt, arg: Void?) {
        super.visit(forStmt, arg)
        checkMisplacedSemicolon(forStmt, "for statement")
        forStmt.body.accept(this, arg)
    }

    /**
     * Visits WhileStmt nodes in the AST, checks for misplaced semicolons, and recursively visits the body of the while statement.
     *
     * @param whileStmt the while statement node to visit
     * @param arg additional argument (not used)
     */
    override fun visit(whileStmt: WhileStmt, arg: Void?) {
        super.visit(whileStmt, arg)
        checkMisplacedSemicolon(whileStmt, "while statement")
        whileStmt.body.accept(this, arg)
    }

    /**
     * Checks if a control structure statement contains a misplaced semicolon.
     * If a misplaced semicolon is found, it records the issue with the statement type and position.
     *
     * @param stmt the statement to check
     * @param stmtType the type of the statement (if, for, while)
     */
    private fun checkMisplacedSemicolon(stmt: Statement, stmtType: String) {
        val regex = """\b(if|while|for|switch|do)\s*\(?.*?\)?\s*;""".toRegex()
        if (stmt.toString().matches(regex)) {
            val begin = stmt.begin.getPair()
            val end = stmt.end.getPair()
            issues += RuleType.ControlStructureType(stmtType, begin)
        }
    }
}
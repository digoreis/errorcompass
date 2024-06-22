package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.model.StmtType
import pt.iscte.errorcompass.support.getLocation

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
        if(ifStmt.thenStmt is EmptyStmt){
            val begin = ifStmt.begin.getLocation()
            issues += RuleType.ControlStructureType(StmtType.IF, begin)
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
        if(forStmt.body is EmptyStmt) {
            val begin = forStmt.begin.getLocation()
            issues += RuleType.ControlStructureType(StmtType.FOR, begin)
        }
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
        if(whileStmt.body is EmptyStmt) {
            val begin = whileStmt.begin.getLocation()
            issues += RuleType.ControlStructureType(StmtType.WHILE, begin)
        }
        whileStmt.body.accept(this, arg)
    }
}
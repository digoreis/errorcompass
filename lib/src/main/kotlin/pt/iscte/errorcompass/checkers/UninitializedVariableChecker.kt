package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getName
import pt.iscte.errorcompass.support.getPair

/**
 * UninitializedVariableChecker is a visitor class that extends VoidVisitorAdapter to detect uninitialized variables
 * in Java source code using JavaParser. It maintains a list of uninitialized variable names and records issues
 * where these variables are used without initialization.
 */
class UninitializedVariableChecker : VoidVisitorAdapter<Void>() {
    private val uninitializedVariables: MutableSet<String> = HashSet()
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    /**
     * Visits VariableDeclarator nodes in the AST and adds variables without initializers to the uninitialized set.
     *
     * @param vd the variable declarator node to visit
     * @param arg additional argument (not used)
     */
    override fun visit(vd: VariableDeclarator, arg: Void?) {
        super.visit(vd, arg)
        if(!vd.initializer.isPresent) {
            uninitializedVariables.add(vd.name.asString())
        }
    }

    /**
     * Visits AssignExpr nodes in the AST and removes variables from the uninitialized set once they are assigned.
     *
     * @param ae the assign expression node to visit
     * @param arg additional argument (not used)
     */
    override fun visit(ae: AssignExpr, arg: Void?) {
        super.visit(ae, arg)
        uninitializedVariables.remove(ae.target.asNameExpr().name.asString())
    }

    /**
     * Visits NameExpr nodes in the AST and checks if they correspond to uninitialized variables.
     * Records issues for uninitialized variable usage.
     *
     * @param ne the name expression node to visit
     * @param arg additional argument (not used)
     */
    override fun visit(ne: NameExpr, arg: Void?) {
        super.visit(ne, arg)

        val variableName = ne.nameAsString
        val variableType = ne.calculateResolvedType().getName()

        if (uninitializedVariables.contains(variableName)) {
            val begin = ne.begin.getPair()
            issues += RuleType.UninitializedVariableType(variableName, variableType, begin)
        }
    }
}
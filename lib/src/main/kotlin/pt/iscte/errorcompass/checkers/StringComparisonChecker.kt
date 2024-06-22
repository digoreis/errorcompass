package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.StringLiteralExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getLocation

/**
 * StringComparisonChecker is a visitor class that extends VoidVisitorAdapter to check for invalid string comparisons
 * using the '==' operator against string literals. It stores identified issues in a list.
 */
class StringComparisonChecker : VoidVisitorAdapter<Void>() {
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    /**
     * Visits BinaryExpr nodes in the AST, checks for invalid string comparisons with string literals using '==',
     * and records any issues found.
     *
     * @param be the binary expression node to visit
     * @param arg additional argument (not used)
     */
    override fun visit(be: BinaryExpr, arg: Void?) {
        super.visit(be, arg)

        if (be.operator == BinaryExpr.Operator.EQUALS) {
            if (isStringComparisonWithLiteral(be)) {
                val location = be.begin.getLocation()
                issues += RuleType.StringComparisonType(location)
            }
        }
    }

    /**
     * Checks if the binary expression is a string comparison with a literal.
     *
     * @param be the binary expression to check
     * @return true if it is a string comparison with a literal, false otherwise
     */
    private fun isStringComparisonWithLiteral(be: BinaryExpr): Boolean {
        val left: Expression = be.left
        val right: Expression = be.right

        val isLeftStringLiteral = left is StringLiteralExpr
        val isRightStringLiteral = right is StringLiteralExpr

        if (isLeftStringLiteral && !isRightStringLiteral) {
            return isStringVariable(right)
        } else if (!isLeftStringLiteral && isRightStringLiteral) {
            return isStringVariable(left)
        }

        return false
    }

    /**
     * Checks if the expression is a string variable.
     *
     * @param expr the expression to check
     * @return true if the expression is a string variable, false otherwise
     */
    private fun isStringVariable(expr: Expression): Boolean {
        return try {
            expr.calculateResolvedType().describe().equals("java.lang.String")
        } catch (e: Exception) {
            false
        }
    }
}
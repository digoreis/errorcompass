package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.StringLiteralExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getPair

class StringComparisonChecker : VoidVisitorAdapter<Void>() {
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    override fun visit(be: BinaryExpr, arg: Void?) {
        super.visit(be, arg)

        if (be.operator == BinaryExpr.Operator.EQUALS) {
            if (isStringComparisonWithLiteral(be)) {
                val begin = be.begin.getPair()
                val end = be.end.getPair()
                issues += RuleType.StringComparisonType(begin)
            }
        }
    }

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

    private fun isStringVariable(expr: Expression): Boolean {
        return try {
            expr.calculateResolvedType().describe().equals("java.lang.String")
        } catch (e: Exception) {
            false
        }
    }
}
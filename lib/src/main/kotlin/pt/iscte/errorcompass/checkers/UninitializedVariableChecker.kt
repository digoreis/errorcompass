package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getName
import pt.iscte.errorcompass.support.getPair


class UninitializedVariableChecker : VoidVisitorAdapter<Void>() {
    private val uninitializedVariables: MutableSet<String> = HashSet()
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    override fun visit(vd: VariableDeclarator, arg: Void?) {
        super.visit(vd, arg)
        if(!vd.initializer.isPresent) {
            uninitializedVariables.add(vd.name.asString())
        }
    }

    override fun visit(ae: AssignExpr, arg: Void?) {
        super.visit(ae, arg)
        uninitializedVariables.remove(ae.target.asNameExpr().name.asString())
    }

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
package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import pt.iscte.errorcompass.support.getPair
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getName

class MethodReturnChecker : VoidVisitorAdapter<Void>() {
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    override fun visit(md: MethodDeclaration, arg: Void?) {
        super.visit(md, arg)
        val returnType = md.type.asString()
        if (returnType == "void") {
            return
        }

        if (!md.body.isPresent) {
            return
        }

        val hasGuaranteedReturn: Boolean = checkForGuaranteedReturn(md.body.get(), returnType, md.nameAsString)
        if (!hasGuaranteedReturn) {
            val begin = md.begin.getPair()
            this.issues += RuleType.MethodReturnType(md.nameAsString, begin)
        }
    }

    private fun checkStatements(statements: NodeList<Statement>, expectedType: String, methodName: String): Boolean {
        for (stmt in statements) {
            if (stmt is ReturnStmt) {
                if (stmt.expression.isPresent) {
                    val returnType = stmt.expression.get().calculateResolvedType().getName().split(".").last().lowercase()
                    if (returnType != expectedType.lowercase()) {
                        val begin = stmt.begin.getPair()
                        this.issues += RuleType.ReturnType(methodName, returnType, expectedType, begin)
                    }
                }
                return true
            } else if (stmt is IfStmt) {
                val thenReturns = checkForGuaranteedReturn(stmt.thenStmt, expectedType, methodName)
                val elseReturns = stmt.elseStmt.map { checkForGuaranteedReturn(it, expectedType, methodName) }.orElse(false)

                if (thenReturns && elseReturns) {
                    return true
                }
            } else if (stmt is BlockStmt) {
                if (checkStatements(stmt.statements, expectedType, methodName)) {
                    return true
                }
            } else if (stmt is SwitchStmt) {
                val allCasesReturn = stmt.entries.stream()
                    .allMatch { entry -> checkStatements(entry.statements, expectedType, methodName) }

                if (allCasesReturn) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkForGuaranteedReturn(stmt: Statement?, expectedType: String, methodName: String): Boolean {
        if (stmt is BlockStmt) {
            return checkStatements(stmt.statements, expectedType, methodName)
        } else if (stmt is ReturnStmt) {
            if (stmt.expression.isPresent) {
                val returnType = stmt.expression.get().calculateResolvedType().getName()
                if (returnType != expectedType) {
                    val begin = stmt.begin.getPair()

                    this.issues += RuleType.ReturnType(methodName, returnType, expectedType, begin)
                }
            }
            return true
        } else if (stmt is IfStmt) {
            val thenReturns = checkForGuaranteedReturn(stmt.thenStmt, expectedType, methodName)
            val elseReturns = stmt.elseStmt.map { checkForGuaranteedReturn(it, expectedType, methodName) }.orElse(false)

            return thenReturns && elseReturns
        } else if (stmt is SwitchStmt) {
            return stmt.entries.stream()
                .allMatch { entry -> checkStatements(entry.statements, expectedType, methodName) }
        }
        return false
    }
}
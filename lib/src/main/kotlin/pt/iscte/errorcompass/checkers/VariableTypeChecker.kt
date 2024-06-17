package pt.iscte.errorcompass.checkers

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.resolution.types.ResolvedType
import pt.iscte.errorcompass.model.RuleType
import pt.iscte.errorcompass.support.getName
import pt.iscte.errorcompass.support.getPair

class VariableTypeChecker(val cu: CompilationUnit) : VoidVisitorAdapter<Void>() {
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    override fun visit(n: VariableDeclarator?, arg: Void?) {
        super.visit(n, arg)
        n?.let {
            val variableType = it.typeAsString
            val variableName = it.nameAsString
            val initializer = it.initializer
            if (initializer.isPresent) {
                val initializerExpr = initializer.get()
                val initializerType = getInitializerType(initializerExpr)
                if (variableType.lowercase() != initializerType) {
                    val begin = n.begin.getPair()
                    val end = n.end.getPair()
                    this.issues += RuleType.VariableType(variableName, variableType, initializerType, begin)
                }
            }
        }
    }

    override fun visit(n: MethodCallExpr?, arg: Void?) {
        super.visit(n, arg)
        n?.let {
            val methodName = it.nameAsString
            val methodDeclaration = findMethodDeclaration(cu, methodName)

            val methodDeclaredParams = methodDeclaration?.parameters?.toList()
            val parameters = it.arguments.toList()

            methodDeclaredParams?.zip(parameters)?.forEach { (methodParameter, param) ->
                val calculateType = getName(param.calculateResolvedType())
                if(methodParameter.typeAsString.lowercase() != calculateType){
                    val begin = n.begin.getPair()
                    this.issues += RuleType.CallArgumentsType(methodParameter.nameAsString , methodName, calculateType, begin)
                }
            }
        }
    }

    override fun visit(n: ClassOrInterfaceDeclaration?, arg: Void?) {
        super.visit(n, arg)
        n?.let {
            it.constructors.forEach { constructor ->
                constructor.parameters.forEach { param ->
                    val paramType = param.typeAsString
                    val paramName = param.nameAsString
                    val initializerType = getInitializerType(param)
                    if (paramType != initializerType) {
                        val begin = n.begin.getPair()
                        val end = n.end.getPair()
                        this.issues += RuleType.ConstructorArgumentsType(it.nameAsString, paramName, initializerType, begin)
                    }
                }
            }
        }
    }

    private fun getInitializerType(initializer: Parameter): String {
        return initializer.typeAsString
    }

    private fun getInitializerType(initializer: Expression): String {
        val types = cu.types.map { it.nameAsString.lowercase() }
        return when (initializer) {
            is StringLiteralExpr -> "string"
            is IntegerLiteralExpr -> "int"
            is LongLiteralExpr -> "long"
            is BooleanLiteralExpr -> "bool"
            is DoubleLiteralExpr -> "double"
            else -> {
                val initializerType = getName(initializer.calculateResolvedType())
                if(types.contains(initializerType)){
                    initializerType
                } else {
                    "Unknown"
                }
            }
        }
    }

    private fun getName(type: ResolvedType): String {
        val types = cu.types.map { it.nameAsString.lowercase() }
        return if(type.isReferenceType) {
            type.asReferenceType().qualifiedName.split(".").last().lowercase()
        } else if(type.isPrimitive) {
            type.asPrimitive().name.lowercase()
        } else if(types.contains(type.getName().lowercase())) {
            type.getName().lowercase()
        } else {
            "Unknown"
        }
    }

    private fun findMethodDeclaration(cu: CompilationUnit, methodName: String): MethodDeclaration? {
        val methodDeclarationVisitor = object : VoidVisitorAdapter<Void>() {
            var methodDeclaration: MethodDeclaration? = null

            override fun visit(n: MethodDeclaration, arg: Void?) {
                super.visit(n, arg)
                if (n.nameAsString == methodName) {
                    methodDeclaration = n
                }
            }
        }

        methodDeclarationVisitor.visit(cu, null)
        return methodDeclarationVisitor.methodDeclaration
    }
}
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
import pt.iscte.errorcompass.support.getLocation
import pt.iscte.errorcompass.support.getName

/**
 * VariableTypeChecker is a visitor class that extends VoidVisitorAdapter to analyze and detect
 * type mismatches in variable declarations, method calls, and constructor parameters in Java source code
 * using JavaParser. It captures and reports issues where variables are initialized with incompatible types.
 *
 * @property cu The CompilationUnit representing the parsed Java source file.
 */
class VariableTypeChecker(val cu: CompilationUnit) : VoidVisitorAdapter<Void>() {
    var issues: List<RuleType> = emptyList()
        private set(value) {
            field = value
        }

    /**
     * Visits VariableDeclarator nodes in the AST to check for type mismatches between declared type
     * and initializer type. Records issues for any detected mismatches.
     *
     * @param n The VariableDeclarator node to visit
     * @param arg Additional argument (not used)
     */
    override fun visit(n: VariableDeclarator, arg: Void?) {
        super.visit(n, arg)
        val variableType = n.typeAsString
        val variableName = n.nameAsString
        val initializer = n.initializer
        if (initializer.isPresent) {
            val initializerExpr = initializer.get()
            val initializerType = getInitializerType(initializerExpr)
            if (variableType.lowercase() != initializerType) {
                val location = n.begin.getLocation()
                this.issues += RuleType.VariableType(variableName, variableType, initializerType, location)
            }
        }
    }

    /**
     * Visits MethodCallExpr nodes in the AST to check for type mismatches between method parameter types
     * and argument types. Records issues for any detected mismatches.
     *
     * @param n The MethodCallExpr node to visit
     * @param arg Additional argument (not used)
     */
    override fun visit(n: MethodCallExpr, arg: Void?) {
        super.visit(n, arg)
        val methodName = n.nameAsString
        val methodDeclaration = findMethodDeclaration(cu, methodName)

        val methodDeclaredParams = methodDeclaration?.parameters?.toList()
        val parameters = n.arguments.toList()

        methodDeclaredParams?.zip(parameters)?.forEach { (methodParameter, param) ->
            val calculateType = getName(param.calculateResolvedType())
            if(methodParameter.typeAsString.lowercase() != calculateType){
                val location = n.begin.getLocation()
                this.issues += RuleType.CallArgumentsType(methodParameter.nameAsString , methodName, calculateType, location)
            }
        }
    }

    /**
     * Visits ClassOrInterfaceDeclaration nodes in the AST to check for type mismatches between
     * constructor parameter types and initializer types. Records issues for any detected mismatches.
     *
     * @param n The ClassOrInterfaceDeclaration node to visit
     * @param arg Additional argument (not used)
     */
    override fun visit(n: ClassOrInterfaceDeclaration, arg: Void?) {
        super.visit(n, arg)
        n.constructors.forEach { constructor ->
            constructor.parameters.forEach { param ->
                val paramType = param.typeAsString
                val paramName = param.nameAsString
                val initializerType = getInitializerType(param)
                if (paramType != initializerType) {
                    val location = n.begin.getLocation()
                    this.issues += RuleType.ConstructorArgumentsType(n.nameAsString, paramName, initializerType, location)
                }
            }
        }
    }

    /**
     * Determines the type of initializer parameter.
     *
     * @param initializer The Parameter to determine the type for
     * @return The type of the initializer as a String
     */
    private fun getInitializerType(initializer: Parameter): String {
        return initializer.typeAsString
    }

    /**
     * Determines the type of initializer expression.
     *
     * @param initializer The Expression to determine the type for
     * @return The type of the initializer as a String
     */
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

    /**
     * Retrieves the name of a ResolvedType.
     *
     * @param type The ResolvedType to retrieve the name for
     * @return The name of the ResolvedType as a String
     */
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

    /**
     * Finds and retrieves a MethodDeclaration by its name.
     *
     * @param cu The CompilationUnit to search within
     * @param methodName The name of the method to find
     * @return The MethodDeclaration found, or null if not found
     */
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
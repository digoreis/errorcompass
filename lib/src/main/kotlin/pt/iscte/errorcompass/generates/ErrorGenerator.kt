package pt.iscte.errorcompass.generates

import pt.iscte.errorcompass.model.ErrorDescription
import pt.iscte.errorcompass.model.RuleType
import java.util.*

class ErrorGenerator {

    fun generate(rule: RuleType): ErrorDescription? {
        return when (rule) {
            is RuleType.VariableType -> ErrorDescription(rule.key, rule.location, message("en", rule.key, rule.variableName, rule.variableType, rule.initializerValue), emptyList())
            is RuleType.StringComparisonType -> ErrorDescription(rule.key, rule.location, message("en", rule.key), emptyList())
            is RuleType.ControlStructureType -> ErrorDescription(rule.key, rule.location, message("en", rule.key, rule.stmtType), emptyList())
            is RuleType.UninitializedVariableType -> ErrorDescription(rule.key, rule.location, message("en", rule.key, rule.variableName, rule.variableType), emptyList())
            is RuleType.CallArgumentsType -> ErrorDescription(rule.key, rule.location, message("en", rule.key, rule.parameterName, rule.methodName, rule.calculatedType), emptyList())
            is RuleType.ConstructorArgumentsType -> ErrorDescription(rule.key, rule.location, message("en", rule.key, rule.paramName, rule.objectName, rule.initializerValue), emptyList())
            is RuleType.ReturnType -> ErrorDescription(rule.key, rule.location, message("en", rule.key, rule.methodName, rule.source, rule.target), emptyList())
            is RuleType.MethodReturnType -> ErrorDescription(rule.key, rule.location, message("en", rule.key, rule.methodName), emptyList())
            else -> { null }
        }
    }

    private fun message(language: String, token: String, vararg args: String) : String {
        val locale = Locale(language)
        val messages = ResourceBundle.getBundle("messages", locale)
        val text = messages.getString("${token}_error".lowercase())
        return String.format(text, *args)
    }
}
package pt.iscte.errorcompass.generates

import pt.iscte.errorcompass.model.ErrorDescription
import pt.iscte.errorcompass.model.RuleType
import java.util.*

class ErrorGenerator {

    fun generate(rule: RuleType): ErrorDescription? {
        return when (rule) {
            is RuleType.VariableType -> ErrorDescription(rule.location, message("en", rule.key, rule.variableName, rule.variableType, rule.initializerValue))
            is RuleType.StringComparisonType -> ErrorDescription(rule.location, message("en", rule.key))
            is RuleType.DowncastPrecisionType -> ErrorDescription(rule.location, message("en", rule.key, rule.target, rule.source))
            is RuleType.ControlStructureType -> ErrorDescription(rule.location, message("en", rule.key, rule.stmtType))
            is RuleType.UninitializedVariableType -> ErrorDescription(rule.location, message("en", rule.key, rule.variableName, rule.variableType))
            is RuleType.CallArgumentsType -> ErrorDescription(rule.location, message("en", rule.key, rule.parameterName, rule.methodName, rule.calculatedType))
            is RuleType.ConstructorArgumentsType -> ErrorDescription(rule.location, message("en", rule.key, rule.paramName, rule.objectName, rule.initializerValue))
            is RuleType.ReturnType -> ErrorDescription(rule.location, message("en", rule.key, rule.methodName, rule.source, rule.target))
            is RuleType.MethodReturnType -> ErrorDescription(rule.location, message("en", rule.key, rule.methodName))
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
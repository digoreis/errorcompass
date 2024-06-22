package pt.iscte.errorcompass.model

sealed class RuleType(val key: String, val location: Location) {

    class ControlStructureType(val stmtType: String, loc: Location): RuleType("crtStr", loc) {

    }

    class MethodReturnType(val methodName: String, loc: Location): RuleType("noRtnSem", loc) {

    }
    class StringComparisonType(loc: Location): RuleType("strCmp", loc) {

    }
    class UninitializedVariableType(val variableName: String, val variableType: String, loc: Location): RuleType("uniVar", loc) {

    }
    class VariableType(val variableName: String, val variableType: String, val initializerValue: String, loc: Location): RuleType("valTyp", loc) {

    }
    class CallArgumentsType(val parameterName: String, val methodName: String, val calculatedType: String, loc: Location): RuleType("calTyp", loc) {

    }
    class ConstructorArgumentsType(val objectName: String, val paramName: String, val initializerValue: String, loc: Location): RuleType("constTyp", loc) {

    }
    class ReturnType(val methodName: String, val source: String, val target: String, loc: Location): RuleType("rtnTyp", loc) {

    }
}

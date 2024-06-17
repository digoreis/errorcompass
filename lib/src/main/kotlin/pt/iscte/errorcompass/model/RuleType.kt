package pt.iscte.errorcompass.model

sealed class RuleType(val key: String, val location: Pair<Int, Int>) {

    class ControlStructureType(val stmtType: String, loc: Pair<Int, Int>): RuleType("crtStr", loc) {

    }

    class DowncastPrecisionType(val source: String, val target: String, loc: Pair<Int, Int> ): RuleType("dowPre", loc) {

    }
    class MethodReturnType(val methodName: String, loc: Pair<Int, Int>): RuleType("noRtnSem", loc) {

    }
    class StringComparisonType(loc: Pair<Int, Int>): RuleType("strCmp", loc) {

    }
    class UninitializedVariableType(val variableName: String, val variableType: String, loc: Pair<Int, Int>): RuleType("uniVar", loc) {

    }
    class VariableType(val variableName: String, val variableType: String, val initializerValue: String, loc: Pair<Int, Int>): RuleType("valTyp", loc) {

    }
    class CallArgumentsType(val parameterName: String, val methodName: String, val calculatedType: String, loc: Pair<Int, Int>): RuleType("calTyp", loc) {

    }
    class ConstructorArgumentsType(val objectName: String, val paramName: String, val initializerValue: String, loc: Pair<Int, Int>): RuleType("constTyp", loc) {

    }
    class ReturnType(val methodName: String, val source: String, val target: String, loc: Pair<Int, Int>): RuleType("rtnTyp", loc) {

    }
}

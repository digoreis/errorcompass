package pt.iscte.errorcompass.model

data class ResultAnalyzer(val errors: List<ErrorDescription>)

data class ErrorDescription(val location: Pair<Int, Int>, val description: String)
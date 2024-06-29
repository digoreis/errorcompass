package pt.iscte.errorcompass.model

data class ResultAnalyzer(val errors: List<ErrorDescription>)

data class ErrorDescription(val errorCode: String, val errorLocation: Location, val description: String, val secondarySuggestions: List<Suggestion>)

data class Suggestion(val location: Location, val description: String)

data class Location(val line: Long, val position: Long)
package pt.iscte.errorcompass.support

import com.github.javaparser.resolution.types.ResolvedType
import java.util.Optional

fun ResolvedType.getName(): String {
    return when {
        this.isReferenceType -> this.asReferenceType().qualifiedName.lowercase()
        this.isPrimitive -> this.asPrimitive().name.lowercase()
        else -> "Unknown"
    }
}

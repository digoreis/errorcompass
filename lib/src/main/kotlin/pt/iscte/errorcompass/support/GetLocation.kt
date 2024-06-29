package pt.iscte.errorcompass.support

import com.github.javaparser.Position
import pt.iscte.errorcompass.model.Location
import java.util.*

fun Optional<Position>.getLocation(): Location {
    return Location(this.get().line.toLong(), this.get().column.toLong())
}
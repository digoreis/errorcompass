package pt.iscte.errorcompass.support

import com.github.javaparser.Position
import java.util.*

fun Optional<Position>.getPair(): Pair<Int, Int> {
    return Pair(this.get().line, this.get().column)
}
package jumpaku.commons.math

fun Int.isOdd(): Boolean = this.and(1) == 1

fun Int.isEven(): Boolean = !isOdd()
package jumpaku.commons.math.test.linear

import jumpaku.commons.math.linear.Matrix
import jumpaku.commons.math.test.isCloseTo
import jumpaku.commons.test.matcher
import org.hamcrest.TypeSafeMatcher

fun isCloseTo(actual: Matrix, expected: Matrix, error: Double = 1.0e-9): Boolean =
        actual.rowSize == expected.rowSize &&
                actual.columnSize == expected.columnSize &&
                actual.toDoubleArrays().flatMap { it.asIterable() }.zip(expected.toDoubleArrays().flatMap { it.asIterable() })
                        .all { (a, e) -> isCloseTo(a, e, error) }

fun closeTo(expected: Matrix, precision: Double = 1.0e-9): TypeSafeMatcher<Matrix> =
    matcher("close to <$expected> with precision $precision") { actual ->
        isCloseTo(actual, expected, precision)
    }


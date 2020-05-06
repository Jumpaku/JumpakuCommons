package jumpaku.commons.math.test.linear

import jumpaku.commons.math.linear.Vector
import jumpaku.commons.math.test.isCloseTo
import jumpaku.commons.test.matcher
import org.hamcrest.TypeSafeMatcher

fun isCloseTo(actual: Vector, expected: Vector, error: Double = 1.0e-9): Boolean =
        actual.size == expected.size &&
                actual.toDoubleArray().zip(expected.toDoubleArray())
                        .all { (a, e) -> isCloseTo(a, e, error) }

fun closeTo(expected: Vector, precision: Double = 1.0e-9): TypeSafeMatcher<Vector> =
    matcher("close to <$expected> with precision $precision") { actual ->
        isCloseTo(actual, expected, precision)
    }


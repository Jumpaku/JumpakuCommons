package jumpaku.commons.math.test

import org.apache.commons.math3.util.Precision
import org.hamcrest.Matcher
import org.hamcrest.Matchers


fun isCloseTo(actual: Double, expected: Double, error: Double = 1.0e-9): Boolean = Precision.equals(actual, expected, error)

fun closeTo(expected: Double, precision: Double = 1.0e-9): Matcher<Double> = Matchers.closeTo(expected, precision)


package jumpaku.commons.math

import jumpaku.commons.control.Result
import jumpaku.commons.control.orDefault
import jumpaku.commons.control.result
import org.apache.commons.math3.util.MathArrays

fun Double.lerp(vararg terms: Pair<Double, Double>): Double {
    val (cs, ds) = terms.unzip().let { (c, d) -> c.toDoubleArray() to d.toDoubleArray() }
    val c0 = 1 - MathArrays.linearCombination(cs, DoubleArray(cs.size) { 1.0 })
    return MathArrays.linearCombination(ds + this, cs + c0)
}

fun Double.lerp(t: Double, other: Double): Double = lerp(t to other)

fun Double.middle(other: Double): Double = lerp(0.5, other)

infix fun Double.tryDiv(divisor: Double): Result<Double> = result {
    if ((this / divisor).isFinite()) this / divisor
    else throw ArithmeticException("divide by zero")
}

fun Double.divOrDefault(divisor: Double, default: () -> Double): Double = tryDiv(divisor).value().orDefault(default)

fun sum(doubles: DoubleArray): Double =
        if (doubles.isEmpty()) 0.0 else MathArrays.linearCombination(doubles, DoubleArray(doubles.size) { 1.0 })
fun sum(doubles: List<Double>): Double = sum(doubles.toDoubleArray())
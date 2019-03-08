package jumpaku.commons.math

import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import org.apache.commons.math3.analysis.solvers.BrentSolver
import org.apache.commons.math3.util.MathArrays


internal fun Double.lerp(t: Double, other: Double): Double = MathArrays.linearCombination(1 - t, this, t, other)

internal fun Double.middle(other: Double): Double = lerp(0.5, other)

class Solver {

    fun solve(
        interval: ClosedRange<Double>,
        initial: Double = interval.run { start.middle(endInclusive) },
        function: (Double) -> Double
    ): Result<Double> {
        require(initial in interval) { "initial value($initial) out of interval($interval)" }
        fun f(x: Double): Double = function(interval.run { start.lerp(x, endInclusive).coerceIn(interval) })
        val x0 = interval.run { ((initial - start) / (endInclusive - start)).coerceIn(0.0..1.0) }
        return result {
            val x = BrentSolver(1e-15, 1e-10).solve(50, { f(it) }, 0.0, 1.0, x0)
            interval.run { start.lerp(x, endInclusive).coerceIn(interval) }
        }
    }
}


package jumpaku.commons.math

import jumpaku.commons.control.result
import jumpaku.commons.control.Result
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.univariate.BrentOptimizer
import org.apache.commons.math3.optim.univariate.SearchInterval
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction

class Optimizer {

    fun minimize(interval: ClosedRange<Double>, objectiveFunction: (Double) -> Double): Result<Pair<Double, Double>> = result {
        fun f(x: Double): Double = objectiveFunction(interval.run { start.lerp(x, endInclusive).coerceIn(interval) })
        val optimum = BrentOptimizer(1e-15, 1e-10).optimize(
                UnivariateObjectiveFunction { f(it) },
                MaxEval(50),
                GoalType.MINIMIZE,
                SearchInterval(0.0, 1.0))
        optimum.run { interval.run { start.lerp(point, endInclusive).coerceIn(interval) } to value }
    }

    fun maximize(interval: ClosedRange<Double>, f: (Double) -> Double): Result<Pair<Double, Double>> =
            minimize(interval) { -f(it) }.tryMap { (x, fx) -> x to -fx }
}
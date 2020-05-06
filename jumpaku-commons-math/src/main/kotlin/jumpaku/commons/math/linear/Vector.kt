package jumpaku.commons.math.linear

import jumpaku.commons.control.Option
import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import jumpaku.commons.math.tryDiv
import org.apache.commons.math3.util.FastMath
import org.apache.commons.math3.util.MathArrays
import kotlin.collections.AbstractList
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.emptyMap
import kotlin.collections.filterValues
import kotlin.collections.getValue
import kotlin.collections.intersect
import kotlin.collections.map
import kotlin.collections.mapKeys
import kotlin.collections.mapValues
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toList
import kotlin.collections.toMap
import kotlin.collections.zip


infix operator fun Double.times(v: Vector): Vector = v.times(this)

internal fun requireSameDimension(v0: Vector, v1: Vector) =
    require(v0.size == v1.size) { "Dimension mismatch ${v0.size} != ${v1.size}" }

interface Vector: List<Double> {

    fun mapIndexed(f: (Int, Double) -> Double): Vector = VectorImpl.Array((0 until size).map { f(it, get(it)) })

    fun map(f: (Double) -> Double): Vector = mapIndexed { _, value -> f(value) }

    infix operator fun times(a: Double): Vector

    infix operator fun div(divisor: Double): Result<Vector>

    operator fun unaryPlus(): Vector = this

    operator fun unaryMinus(): Vector = times(-1.0)

    infix operator fun plus(other: Vector): Vector

    infix operator fun minus(other: Vector): Vector {
        requireSameDimension(this, other)
        return plus(other.unaryMinus())
    }

    fun dot(other: Vector): Double

    fun square(): Double = dot(this)

    fun norm(): Double = FastMath.sqrt(square())

    fun distSquare(other: Vector): Double {
        requireSameDimension(this, other)
        return minus(other).square()
    }

    fun dist(other: Vector): Double {
        requireSameDimension(this, other)
        return FastMath.sqrt(distSquare(other))
    }

    fun normalize(): Option<Vector> = div(norm()).value()

    fun asRow(): Matrix

    fun asColumn(): Matrix

    fun toDoubleArray(): DoubleArray = DoubleArray(size) { get(it) }

    companion object {

        fun of(size: Int, data: Map<Int, Double>): Vector = VectorImpl.Sparse(size, data)

        fun of(data: List<Double>): Vector = VectorImpl.Array(data)

        fun of(data: DoubleArray): Vector = VectorImpl.Array(data)

        fun zeros(size: Int): Vector = of(size, emptyMap())

        fun ones(size: Int): Vector = of(DoubleArray(size) { 1.0 })
    }
}

internal sealed class VectorImpl(override val size: Int) : AbstractList<Double>(), Vector {

    override infix operator fun times(a: Double): Vector = when(this) {
        is Array -> map { it * a }
        is Sparse -> Sparse(size, data.mapValues { it.value * a })
    }

    override infix operator fun div(divisor: Double): Result<Vector> = result {
        when (this) {
            is Array -> map { it.tryDiv(divisor).orThrow() }
            is Sparse -> Sparse(size, data.mapValues { it.value.tryDiv(divisor).orThrow() })
        }
    }

    override infix operator fun plus(other: Vector): Vector {
        requireSameDimension(this, other)
        return when {
            this is Sparse && other is Sparse -> Sparse(size) {
                val result = mutableMapOf<Int, Double>()
                data.forEach { index, value -> result[index] = value }
                other.data.forEach { index, value -> result[index] = value + (result[index] ?: 0.0) }
                result.filterValues { 1.0.tryDiv(it).isSuccess }
            }
            else -> Array(toDoubleArray().zip(other.toDoubleArray(), Double::plus))
        }
    }

    override fun dot(other: Vector): Double {
        requireSameDimension(this, other)
        return when{
            this is Sparse && other is Sparse -> {
                val s = minOf(data, other.data, compareBy { it.size })
                val l = maxOf(other.data, data, compareBy { it.size })
                val keys = s.keys.intersect(l.keys).toList()
                if (keys.isEmpty()) 0.0 else MathArrays.linearCombination(
                    DoubleArray(keys.size) { data.getValue(keys[it]) },
                    DoubleArray(keys.size) { other.data.getValue(keys[it]) })
            }
            this is Sparse && other is Array -> {
                val keys = data.keys.toList()
                MathArrays.linearCombination(
                    DoubleArray(keys.size) { data.getValue(keys[it]) },
                    DoubleArray(keys.size) { other.data[keys[it]] })
            }
            this is Array && other is Sparse -> {
                val keys = other.data.keys.toList()
                MathArrays.linearCombination(
                    DoubleArray(keys.size) { data[keys[it]] },
                    DoubleArray(keys.size) { other.data.getValue(keys[it]) })
            }
            else -> MathArrays.linearCombination(toDoubleArray(), other.toDoubleArray())
        }
    }

    override fun asRow(): Matrix = when(this) {
        is Sparse -> Matrix.sparse(1, size, data.mapKeys { (index, _) -> Matrix.Key(0, index) })
        is Array -> Matrix.of(arrayOf(toDoubleArray()))
    }

    override fun asColumn(): Matrix = when(this) {
        is Sparse -> Matrix.sparse(size, 1, data.mapKeys { (index, _) -> Matrix.Key(index, 0) })
        is Array -> Matrix.of(Array(size) { doubleArrayOf(get(it)) })
    }

    class Sparse(size: Int, data: Map<Int, Double>): VectorImpl(size) {

        constructor(size: Int, builder: (size: Int) -> Map<Int, Double>): this(size, builder(size))

        val data: Map<Int, Double> = data.toMap()

        override operator fun get(index: Int): Double = data[index] ?: 0.0
    }

    class Array(data: List<Double>): VectorImpl(data.size) {

        constructor(data: DoubleArray): this(data.toList())

        val data: List<Double> = data.toList()

        override operator fun get(index: Int): Double = data[index]
    }
}

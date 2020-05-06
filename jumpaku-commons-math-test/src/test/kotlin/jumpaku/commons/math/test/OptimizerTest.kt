package jumpaku.commons.math.test

import jumpaku.commons.math.Optimizer
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.math.PI
import kotlin.math.cos

class OptimizerTest {

    @Test
    fun testMinimize() {
        println("Minimize")
        val (x, fx) = Optimizer().minimize(3.0..4.0) { cos(it) }.orThrow()
        assertThat(x, `is`(closeTo(PI)))
        assertThat(fx, `is`(closeTo(-1.0)))
    }

    @Test
    fun testMaximize() {
        println("Maximize")
        val (x, fx) = Optimizer().maximize(3.0..4.0) { -cos(it) }.orThrow()
        assertThat(x, `is`(closeTo(PI)))
        assertThat(fx, `is`(closeTo(1.0)))
    }
}
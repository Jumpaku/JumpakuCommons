package jumpaku.commons.math.test

import jumpaku.commons.math.sum
import jumpaku.commons.math.test.isCloseTo
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class DoublesTest {

    @Test
    fun testSum() {
        println("Sum")
        val doubles = doubleArrayOf(
                1.0,
                1e-9, 1e-9, 1e-9, 1e-9, 1e-9,
                1e-9, 1e-9, 1e-9, 1e-9, 1e-9,
                -1.0,
                -1e-9, -1e-9, -1e-9, -1e-9, -1e-9,
                -1e-9, -1e-9, -1e-9, -1e-9, -1e-9,
                1.0
        )
        val a = sum(doubles)
        assertThat(a, `is`(closeTo(1.0, 0.0)))

        val b = sum(doubles.toList())
        assertThat(b, `is`(closeTo(1.0, 0.0)))
    }
}
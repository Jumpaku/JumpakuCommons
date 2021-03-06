package jumpaku.commons.math.linear.test

import jumpaku.commons.math.linear.Matrix
import jumpaku.commons.math.linear.Solver
import jumpaku.commons.math.linear.Vector
import jumpaku.commons.math.test.linear.closeTo
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test


class SolverTest {

    val data = listOf(
        listOf(1.0, 0.0, 1.0, 1.0),
        listOf(0.0, 1.0, 2.0, -1.0),
        listOf(-2.0, 1.0, -1.0, 0.0),
        listOf(1.0, -1.0, 0.0, 1.0))
    val id = Matrix.identity(4)
    val di = Matrix.diagonal(listOf(1.0, -0.5, 2.0, 10.0))
    val sp = Matrix.sparse(4, 4, mutableMapOf<Matrix.Key, Double>().also { m ->
        for (i in 0 until 4) for (j in 0 until 4) data[i][j].let { if (it != 0.0) m[Matrix.Key(i, j)] = it }
    })
    val ar = Matrix.of(data)

    val b = Vector.of(listOf(7.0, 9.0, -5.0, 0.0)).asColumn()

    @Test
    fun testSolve() {
        val a_id = Solver.byQRDecomposition(id).orThrow().solve(b)
        val e_id = b
        assertThat(a_id, `is`(closeTo(e_id)))

        val a_di = Solver.byQRDecomposition(di).orThrow().solve(b)
        val e_di = Vector.of(listOf(7.0, -18.0, -2.5, 0.0)).asColumn()
        assertThat(a_di, `is`(closeTo(e_di)))

        val a_sp = Solver.byQRDecomposition(sp).orThrow().solve(b)
        val e_sp = Vector.of(listOf(3.0, 4.0, 3.0, 1.0)).asColumn()
        assertThat(a_sp, `is`(closeTo(e_sp)))

        val a_ar = Solver.byQRDecomposition(ar).orThrow().solve(b)
        val e_ar = e_sp
        assertThat(a_ar, `is`(closeTo(e_ar)))
    }

    @Test
    fun testInverse() {
        val i_id = Solver.byQRDecomposition(id).orThrow().inverse()
        assertThat(i_id*id, `is`(closeTo(id)))
        assertThat(id*i_id, `is`(closeTo(id)))

        val i_di = Solver.byQRDecomposition(di).orThrow().inverse()
        assertThat(i_di*di, `is`(closeTo(id)))
        assertThat(di*i_di, `is`(closeTo(id)))

        val i_sp = Solver.byQRDecomposition(sp).orThrow().inverse()
        assertThat(i_sp*sp, `is`(closeTo(id)))
        assertThat(sp*i_sp, `is`(closeTo(id)))

        val i_ar = Solver.byQRDecomposition(ar).orThrow().inverse()
        assertThat(i_ar*ar, `is`(closeTo(id)))
        assertThat(ar*i_ar, `is`(closeTo(id)))
    }
}

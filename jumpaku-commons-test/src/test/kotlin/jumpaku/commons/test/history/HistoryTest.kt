package jumpaku.commons.test.history

import jumpaku.commons.control.Option
import jumpaku.commons.control.orDefault
import jumpaku.commons.history.History
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Test

class HistoryTest {
    val update: (Option<Int>) -> Int = { it.map { it + 1 }.orDefault(1) }

    val h = History<Int>()

    @Test
    fun testExec() {
        println("Exec")
        val e0 = h.exec(update)
        assertThat(e0.current.orNull(), `is`(1))
        val e1 = h.exec(update).exec(update)
        assertThat(e1.current.orNull(), `is`(2))
    }

    @Test
    fun testUndo() {
        println("Undo")
        val u0 = h.undo()
        assertThat(u0.current.orNull(), `is`(nullValue()))
        val u1 = h.exec(update).exec(update).undo()
        assertThat(u1.current.orNull(), `is`(1))
        val u2 = h.exec(update).exec(update).exec(update).undo().undo()
        assertThat(u2.current.orNull(), `is`(1))
    }

    @Test
    fun testRedo() {
        println("Redo")
        val r0 = h.redo()
        assertThat(r0.current.orNull(), `is`(nullValue()))
        val r1 = h.exec(update).redo()
        assertThat(r1.current.orNull(), `is`(1))
        val r2 = h.exec(update).undo().redo()
        assertThat(r2.current.orNull(), `is`(1))
        val r3 = h.exec(update).exec(update).undo().undo().redo()
        assertThat(r3.current.orNull(), `is`(1))
        val r4 = h.exec(update).exec(update).undo().undo().redo().redo()
        assertThat(r4.current.orNull(), `is`(2))
    }

    @Test
    fun testRedoUndo() {
        println("Redo")
        val e = h.exec(update).exec(update).exec(update)
        val e0 = e.undo()
        assertThat(e0.current.orNull(), `is`(2))
        val e1 = e.undo().undo().redo().redo().undo().redo()
        assertThat(e1.current.orNull(), `is`(3))

        val e2 = e.undo().undo().undo().undo().redo().redo().undo().redo().redo().redo()
        assertThat(e2.current.orNull(), `is`(3))
        val e3 = e.redo().redo().redo().undo().undo().undo().undo().redo().redo().undo()
        assertThat(e3.current.orNull(), `is`(1))
    }
}
package jumpaku.commons.history.test

import jumpaku.commons.control.Option
import jumpaku.commons.control.orDefault
import jumpaku.commons.history.Command
import jumpaku.commons.history.History
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Test

class HistoryTest {
    object Action: (Option<Int>) -> Int {
        override fun invoke(p1: Option<Int>): Int =p1.map { it + 1 }.orDefault(1)
    }
    val redo: Command<Int, Action> = Command.Redo()
    val undo: Command<Int, Action> = Command.Undo()
    val update: Command<Int, Action> = Command.Do(Action)

    val h = History<Int, Action>()

    @Test
    fun testReproduce() {
        println("Reproduce")
        val e = h
            .exec(update)
            .exec(update)
            .exec(update)
            .redo()
            .redo()
            .redo()
            .undo()
            .undo()
            .undo()
            .undo()
            .redo()
            .redo()
            .undo()
            .exec(update)
        val a = History.reproduce(e.commands)
        assertThat(a.current.orThrow(), `is`(e.current.orThrow()))
    }
    @Test
    fun testExec() {
        println("Exec")
        val e0 = h.exec(update)
        assertThat(e0.current.orNull(), `is`(1))
        val e1 = h.exec(update).exec(update)
        assertThat(e1.current.orNull(), `is`(2))

        val e2 = h.exec(undo)
        assertThat(e2.current.orNull(), `is`(nullValue()))
        val e3 = h.exec(update).exec(update).exec(undo)
        assertThat(e3.current.orNull(), `is`(1))
        val e4 = h.exec(update).exec(update).exec(update).exec(undo).exec(undo)
        assertThat(e4.current.orNull(), `is`(1))

        val e5 = h.redo()
        assertThat(e5.current.orNull(), `is`(nullValue()))
        val e6 = h.exec(update).exec(redo)
        assertThat(e6.current.orNull(), `is`(1))
        val e7 = h.exec(update).exec(undo).exec(redo)
        assertThat(e7.current.orNull(), `is`(1))
        val e8 = h.exec(update).exec(update).exec(undo).exec(undo).exec(redo)
        assertThat(e8.current.orNull(), `is`(1))
        val e9 = h.exec(update).exec(update).exec(undo).exec(undo).exec(redo).exec(redo)
        assertThat(e9.current.orNull(), `is`(2))
    }

    @Test
    fun testDoAction() {
        println("DoAction")
        val e0 = h.doAction(Action)
        assertThat(e0.current.orNull(), `is`(1))
        val e1 = h.doAction(Action).doAction(Action)
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
    fun testCombinations() {
        println("Combinations")
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
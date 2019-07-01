package jumpaku.commons.history

import jumpaku.commons.control.None
import jumpaku.commons.control.Option
import jumpaku.commons.control.Some
import jumpaku.commons.control.toOption
import kotlin.math.min

sealed class Command<out Memento : Any> {

    class Do<Memento : Any>(val name: String = "", val update: (Option<Memento>) -> Memento) : Command<Memento>() {
        override fun toString(): String = "Do($name)"
    }

    object Redo : Command<Nothing>() {
        override fun toString(): String = "Redo"
    }

    object Undo : Command<Nothing>() {
        override fun toString(): String = "Undo"
    }
}

class History<Memento: Any>(
    val current: Option<Memento> = None,
    private val prev: List<Memento> = emptyList(),
    private val next: List<Memento> = emptyList()
) {
    fun exec(command: Command<Memento>): History<Memento> = when (command) {
        is Command.Do -> History(Some(command.update(current)), current + prev, emptyList())
        is Command.Undo -> History(prev.firstOrNull().toOption(), prev.drop(min(1, prev.size)), current + next)
        is Command.Redo -> if (next.isEmpty()) this else History(Some(next.first()), current + prev, next.drop(1))
    }

    fun undo(): History<Memento> = exec(Command.Undo)

    fun redo(): History<Memento> = exec(Command.Redo)

    fun exec(f: (Option<Memento>) -> Memento) = exec(Command.Do(update = f))
}
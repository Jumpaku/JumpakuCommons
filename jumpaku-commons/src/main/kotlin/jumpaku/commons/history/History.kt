package jumpaku.commons.history

import jumpaku.commons.control.None
import jumpaku.commons.control.Option
import jumpaku.commons.control.Some
import jumpaku.commons.control.toOption
import kotlin.math.min


class History<Memento : Any, Action : (Option<Memento>) -> Memento> private constructor(
    val current: Option<Memento>,
    private val prev: List<Memento>,
    private val next: List<Memento>,
    val commands: List<Command<Memento, Action>>
) {
    constructor() : this(None, emptyList(), emptyList(), emptyList())

    private val undo: Command.Undo<Memento, Action> = Command.Undo()

    private val redo: Command.Redo<Memento, Action> = Command.Redo()

    fun exec(command: Command<Memento, Action>): History<Memento, Action> = when (command) {
        is Command.Do -> History(Some(command.action(current)), current + prev, emptyList(), commands + command)
        is Command.Undo -> History(prev.firstOrNull().toOption(), prev.drop(min(1, prev.size)), current + next, commands + command)
        is Command.Redo -> if (next.isEmpty()) this else History(Some(next.first()), current + prev, next.drop(1), commands + command)
    }

    fun undo(): History<Memento, Action> = exec(undo)

    fun redo(): History<Memento, Action> = exec(redo)

    fun doAction(action: Action): History<Memento, Action> = exec(Command.Do(action))

    companion object {

        fun <Memento : Any, Action : (Option<Memento>) -> Memento> reproduce(commands: List<Command<Memento, Action>>)
                : History<Memento, Action> = commands.fold(History()) { h, c -> h.exec(c) }
    }
}
package jumpaku.commons.history

import jumpaku.commons.control.Option

sealed class Command<Memento : Any, Aciton : (Option<Memento>) -> Memento> {

    class Do<Memento : Any, Action : (Option<Memento>) -> Memento>(val action: Action, val name: String = "") : Command<Memento, Action>() {
        override fun toString(): String = "Do($name)"
    }

    class Redo<Memento : Any, Action : (Option<Memento>) -> Memento> : Command<Memento, Action>() {
        override fun toString(): String = "Redo"
    }

    class Undo<Memento : Any, Action : (Option<Memento>) -> Memento> : Command<Memento, Action>() {
        override fun toString(): String = "Undo"
    }
}
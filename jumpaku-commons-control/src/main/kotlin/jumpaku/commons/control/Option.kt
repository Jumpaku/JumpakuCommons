package jumpaku.commons.control

import java.util.*


sealed class Option<out T: Any>: Iterable<T> {

    val isEmpty: Boolean get() = this === None

    val isDefined: Boolean get() = !isEmpty

    fun orNull(): T? = (this as? Some)?.value

    fun orThrow(except: () -> Exception = { NoSuchElementException("None.orThrow()") }): T = orNull() ?: throw except()

    inline fun <U: Any> map(transform: (T) -> U): Option<U> = flatMap { Some(transform(it)) }

    inline fun <U: Any> flatMap(transform: (T) -> Option<U>): Option<U> = (this as? Some)?.let { transform(value) } ?: None

    inline fun filter(test: (T) -> Boolean): Option<T> = if (this is Some && test(value)) this else None

    inline fun ifPresent(action: (T) -> Unit): Option<T> = apply { forEach(action) }

    inline fun ifAbsent(action: () -> Unit): Option<T> = apply { if (this is None) action() }

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Option<*> -> false
        this is Some<*> && other is Some<*> -> value == other.value
        else -> this === other
    }

    override fun hashCode(): Int = if (this is Success<*>) value.hashCode() else 0

}

object None : Option<Nothing>() {

    object NoneIterator : Iterator<Nothing> {

        override fun hasNext(): Boolean = false

        override fun next(): Nothing = throw NoSuchElementException("next() of empty iterator")
    }

    override fun iterator(): Iterator<Nothing> = NoneIterator

    override fun toString(): String = "None"
}

class Some<out T: Any>(val value: T) : Option<T>() {

    override fun iterator(): Iterator<T> = object : Iterator<T> {

        var hasNext: Boolean = true

        override fun hasNext(): Boolean = hasNext

        override fun next(): T = when {
            hasNext() -> { hasNext = false; value }
            else -> throw NoSuchElementException("next() of empty iterator")
        }
    }

    override fun toString(): String = "Some($value)"
}

fun <T: Any> Option<Option<T>>.flatten(): Option<T> = flatMap { it }

inline fun <T: Any> Option<T>.orDefault(default: () -> T): T = orNull() ?: default()
fun <T: Any> Option<T>.orDefault(default: T): T = orNull() ?: default

infix fun <T: Any> Option<T>.or(other: Option<T>): Option<T> = ifAbsent { return other }

infix fun <T: Any> Option<T>.and(other: Option<T>): Option<T> = ifPresent { return other }

inline fun <T: Any> Option<T>.toResult(except: () -> Exception = { NoSuchElementException("None.orThrow()") }): Result<T> =
    result { (this as? Some)?.value ?: throw except() }

fun <T: Any> none(): Option<T> = None

fun <T: Any> some(value: T): Option<T> = Some(value)

fun <T: Any> option(nullable: T?): Option<T> = option { nullable }
inline fun <T: Any> option(nullable: () -> T?): Option<T> = nullable()?.let(::some) ?: none()

fun <T: Any> T?.toOption(): Option<T> = option { this }

inline fun <T: Any> optionWhen(condition: Boolean, supply: () -> T): Option<T> = if (condition) some(supply()) else none()

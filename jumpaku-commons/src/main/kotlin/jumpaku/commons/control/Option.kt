package jumpaku.commons.control

import com.github.salomonbrys.kotson.contains
import com.github.salomonbrys.kotson.jsonNull
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*


sealed class Option<out T: Any>: Iterable<T> {

    val isEmpty: Boolean get() = this === None

    val isDefined: Boolean get() = !isEmpty

    fun orNull(): T? = (this as? Some)?.value

    fun orThrow(except: ()->Exception = { NoSuchElementException("None.orThrow()") }): T = orNull() ?: throw except()

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

    companion object {

        fun fromJson(json: JsonElement): Option<JsonElement> =
            if ("value" in (json as JsonObject) && !json["value"].isJsonNull) some(json["value"])
            else none()
    }
}

fun <J : JsonElement> Option<J>.toJson(): JsonElement = map {
    jsonObject("value" to it)
}.orDefault(jsonObject("value" to jsonNull))

fun <J : JsonElement> Option<J>.toJsonString(): String = toJson().toString()

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

inline fun <T: Any> Option<T>.toResult(except: () -> Exception = { NoSuchElementException("None.orThrow()") }): Result<T> =
    result { (this as? Some)?.value ?: throw except() }

fun <T: Any> none(): Option<T> = None

fun <T: Any> some(value: T): Option<T> = Some(value)

fun <T: Any> option(nullable: T?): Option<T> = option { nullable }
inline fun <T: Any> option(nullable: () -> T?): Option<T> = nullable()?.let(::some) ?: none()

fun <T: Any> T?.toOption(): Option<T> = option { this }

inline fun <T: Any> optionWhen(condition: Boolean, supply: () -> T): Option<T> = if (condition) some(supply()) else none()
inline fun <T: Any> T.someIf(condition: T.() -> Boolean, supply: T.() -> T = { this }): Option<T> = if(condition()) some(supply()) else none()
inline fun <T: Any> T.someUnless(condition: T.() -> Boolean, supply: T.() -> T = { this }): Option<T> = someIf({ !condition() }, supply)


fun main() {
    runCatching {  }.isFailure
}
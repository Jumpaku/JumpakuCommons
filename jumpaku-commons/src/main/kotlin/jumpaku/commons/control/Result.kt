package jumpaku.commons.control


sealed class Result<V: Any> {

    fun value(): Option<V> = if (this is Success) some(value) else none()

    fun error(): Option<Exception> = if (this is Failure) some(error) else none()

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure

    inline fun <U : Any> tryMap(transform: (V) -> U): Result<U> = tryFlatMap { result { transform(it) } }

    inline fun <U : Any> tryFlatMap(transform: (V) -> Result<U>): Result<U> = when (this) {
        is Success -> try {
            transform(value)
        } catch (e: Exception) {
            Failure<U>(e)
        }
        is Failure -> Failure(error)
    }

    inline fun tryRecover(recovery: (Exception) -> V): Result<V> = when (this) {
        is Success -> this
        is Failure -> result { recovery(error) }
    }

    inline fun tryMapFailure(transform: (Exception) -> Exception): Result<V> = when (this) {
        is Success -> this
        is Failure -> Failure(
            try {
                transform(error)
            } catch (e: Exception) {
                e
            }
        )
    }

    fun orThrow(): V = when (this) {
        is Success -> value
        is Failure -> throw error
    }

    inline fun orRecover(recovery: (Exception) -> V): V = tryRecover(recovery).orThrow()

    inline fun onSuccess(action: (V) -> Unit): Result<V> = apply { value().forEach(action) }

    inline fun onFailure(action: (Exception) -> Unit) = apply { error().forEach(action) }
}

class Success<V: Any>(val value: V) : Result<V>() {

    override fun toString(): String = "Success($value)"
}

class Failure<V: Any>(val error: Exception) : Result<V>() {

    override fun toString(): String = "Failure($error)"
}

fun <T: Any> Result<Result<T>>.flatten(): Result<T> = tryFlatMap { it }

inline fun <T: Any> result(tryCompute: () -> T): Result<T> = try {
    Success(tryCompute())
} catch (e: Exception) {
    Failure(e)
}

fun <V: Any> success(value: V): Result<V> = Success(value)

fun <V: Any> failure(exception: Exception): Result<V> = Failure(exception)

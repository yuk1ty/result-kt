package com.github.yuk1ty.resultKt

import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok

/**
 * A discriminated union type that represents either success ([Ok]) or failure ([Err]).
 *
 * @param T The type of the value in case of success
 * @param E The type of the error in case of failure
 */
sealed interface Result<out T, out E> {
    companion object {
        /**
         * Runs the provided function and returns its result wrapped in [Ok] if it completes successfully,
         * or the caught exception wrapped in [Err] if it throws.
         *
         * @param block The function to run
         * @return [Ok] with the result if successful, [Err] with the exception if it throws
         * @see [runCatching]
         */
        inline fun <T> lift(crossinline block: () -> T): Result<T, Throwable> =
            try {
                Ok(block())
            } catch (e: Throwable) {
                Err(e)
            }
    }

    /**
     * Returns true if this [Result] is an [Ok] instance.
     *
     * @return true if this is [Ok], false otherwise
     */
    fun isOk(): Boolean

    /**
     * Returns true if this [Result] is an [Err] instance.
     *
     * @return true if this is [Err], false otherwise
     */
    fun isErr(): Boolean

    /**
     * Represents a successful result containing a value.
     *
     * @param T The type of the contained value
     * @property value The success value
     */
    @JvmInline
    value class Ok<T>(
        val value: T,
    ) : Result<T, Nothing> {
        override fun isOk(): Boolean = true

        override fun isErr(): Boolean = false
    }

    /**
     * Represents a failed result containing an error.
     *
     * @param E The type of the contained error
     * @property error The error value
     */
    @JvmInline
    value class Err<E>(
        val error: E,
    ) : Result<Nothing, E> {
        override fun isOk(): Boolean = false

        override fun isErr(): Boolean = true
    }
}

/**
 * Converts this [Result] to a nullable value.
 *
 * @return The contained value if this is [Ok], or null if this is [Err]
 */
fun <T, E> Result<T, E>.ok(): T? =
    when (this) {
        is Ok -> value
        is Err -> null
    }

/**
 * Converts this [Result] to a nullable error.
 *
 * @return The contained error if this is [Err], or null if this is [Ok]
 */
fun <T, E> Result<T, E>.err(): E? =
    when (this) {
        is Ok -> null
        is Err -> error
    }

/**
 * Returns the contained value if this is [Ok], otherwise throws an [UnwrapException].
 *
 * @return The contained value
 * @throws UnwrapException if this is [Err]
 */
fun <T, E> Result<T, E>.unwrap(): T =
    when (this) {
        is Ok -> value
        is Err -> throw UnwrapException("called `unwrap()` on an `Err` value: $error")
    }

/**
 * Returns the contained error if this is [Err], otherwise throws an [UnwrapException].
 *
 * @return The contained error
 * @throws UnwrapException if this is [Ok]
 */
fun <T, E> Result<T, E>.unwrapErr(): E =
    when (this) {
        is Ok -> throw UnwrapException("called `unwrapErr()` on an `Ok` value: $value")
        is Err -> error
    }

/**
 * Returns the contained value if this is [Ok], otherwise returns the provided default value.
 *
 * @param default The default value to return if this is [Err]
 * @return The contained value or the default value
 */
fun <T, E> Result<T, E>.unwrapOr(default: T): T =
    when (this) {
        is Ok -> value
        is Err -> default
    }

/**
 * Returns the contained value if this is [Ok], otherwise returns the result of applying
 * the provided function to the error value.
 *
 * @param f The function to apply to the error value
 * @return The contained value or the result of applying [f] to the error
 */
inline fun <T, E> Result<T, E>.unwrapOrElse(f: (E) -> T): T =
    when (this) {
        is Ok -> value
        is Err -> f(error)
    }

/**
 * Returns the contained value if this is [Ok], otherwise throws the contained error.
 *
 * @return The contained value
 * @throws E if this is [Err]
 */
fun <T, E : Throwable> Result<T, E>.unwrapOrThrow(): T =
    when (this) {
        is Ok -> value
        is Err -> throw error
    }

/**
 * Returns the contained value if this is [Ok], otherwise throws an [UnwrapException]
 * with the provided message.
 *
 * @param msg The error message
 * @return The contained value
 * @throws UnwrapException if this is [Err]
 */
fun <T, E> Result<T, E>.expect(msg: String): T =
    when (this) {
        is Ok -> value
        is Err -> throw UnwrapException(msg)
    }

/**
 * Returns the contained error if this is [Err], otherwise throws an [UnwrapException]
 * with the provided message.
 *
 * @param msg The error message
 * @return The contained error
 * @throws UnwrapException if this is [Ok]
 */
fun <T, E> Result<T, E>.expectErr(msg: String): E =
    when (this) {
        is Ok -> throw UnwrapException(msg)
        is Err -> error
    }

/**
 * Maps a [Result<T, E>] to [Result<U, E>] by applying the provided function to the contained
 * value if this is [Ok], otherwise returns the [Err] value unchanged.
 *
 * @param f The function to apply to the contained value
 * @return A new [Result] with the mapped value if this is [Ok], otherwise the original [Err]
 */
inline fun <T, U, E> Result<T, E>.map(f: (T) -> U): Result<U, E> =
    when (this) {
        is Ok -> Ok(f(value))
        is Err -> Err(error)
    }

/**
 * Maps a [Result<T, E>] to [U] by applying the provided function to the contained
 * value if this is [Ok], otherwise returns the provided default value.
 *
 * @param f The function to apply to the contained value
 * @param default The default value to return if this is [Err]
 * @return The result of applying [f] to the contained value or the default value
 */
inline fun <T, U, E> Result<T, E>.mapOr(
    f: (T) -> U,
    default: U,
): U =
    when (this) {
        is Ok -> f(value)
        is Err -> default
    }

/**
 * Maps a [Result<T, E>] to [U] by applying the first provided function to the contained
 * value if this is [Ok], otherwise applies the second function to the contained error.
 *
 * @param default The function to apply to the error value
 * @param f The function to apply to the contained value
 * @return The result of applying the appropriate function
 */
inline fun <T, U, E> Result<T, E>.mapOrElse(
    default: (E) -> U,
    f: (T) -> U,
): U =
    when (this) {
        is Ok -> f(value)
        is Err -> default(error)
    }

/**
 * Maps a [Result<T, E>] to [Result<T, F>] by applying the provided function to the contained
 * error if this is [Err], otherwise returns the [Ok] value unchanged.
 *
 * @param f The function to apply to the contained error
 * @return A new [Result] with the mapped error if this is [Err], otherwise the original [Ok]
 */
inline fun <T, E, F> Result<T, E>.mapErr(f: (E) -> F): Result<T, F> =
    when (this) {
        is Ok -> Ok(value)
        is Err -> Err(f(error))
    }

/**
 * Returns the result of applying the provided function to the contained value if this is [Ok],
 * otherwise returns the [Err] value unchanged.
 *
 * @param f The function to apply to the contained value
 * @return The result of applying [f] to the contained value or the original [Err]
 */
fun <T, U, E> Result<T, E>.flatMap(f: (T) -> Result<U, E>): Result<U, E> =
    when (this) {
        is Ok -> f(value)
        is Err -> Err(error)
    }

/**
 * Calls the provided function with the contained value if this is [Ok], then returns
 * the original [Result] unchanged.
 *
 * @param f The function to apply to the contained value
 * @return The original [Result]
 */
inline fun <T, E> Result<T, E>.inspect(f: (T) -> Unit): Result<T, E> {
    if (this is Ok) f(value)
    return this
}

/**
 * Calls the provided function with the contained error if this is [Err], then returns
 * the original [Result] unchanged.
 *
 * @param f The function to apply to the contained error
 * @return The original [Result]
 */
inline fun <T, E> Result<T, E>.inspectErr(f: (E) -> Unit): Result<T, E> {
    if (this is Err) f(error)
    return this
}

/**
 * Transposes a [Result] of a nullable value into a nullable [Result].
 *
 * @return [Ok] with the value if this is [Ok] and the value is not null,
 *         null if this is [Ok] and the value is null,
 *         [Err] with the error if this is [Err]
 */
fun <T, E> Result<T?, E>.transpose(): Result<T, E>? =
    when (this) {
        is Ok -> this.value?.let { Ok(it) }
        is Err -> Err(this.error)
    }

/**
 * Flattens a nested [Result] into a single [Result].
 *
 * @return The inner [Result] if this is [Ok], otherwise the [Err] value
 */
fun <T, E> Result<Result<T, E>, E>.flatten(): Result<T, E> =
    when (this) {
        is Ok -> value
        is Err -> Err(error)
    }

/**
 * Returns [other] if this is [Ok], otherwise returns this [Err].
 *
 * @param other The [Result] to return if this is [Ok]
 * @return [other] if this is [Ok], otherwise this [Err]
 */
fun <T, E> Result<T, E>.and(other: Result<T, E>): Result<T, E> =
    when (this) {
        is Ok -> other
        is Err -> this
    }

/**
 * Returns the result of applying the provided function to the contained value if this is [Ok],
 * otherwise returns the [Err] value unchanged. Alias for [flatMap].
 *
 * @param f The function to apply to the contained value
 * @return The result of applying [f] to the contained value or the original [Err]
 */
fun <T, E> Result<T, E>.andThen(f: (T) -> Result<T, E>): Result<T, E> = this.flatMap(f)

/**
 * Returns this [Result] if it is [Ok], otherwise returns [other].
 *
 * @param other The [Result] to return if this is [Err]
 * @return This [Result] if it is [Ok], otherwise [other]
 */
fun <T, E> Result<T, E>.or(other: Result<T, E>): Result<T, E> =
    when (this) {
        is Ok -> this
        is Err -> other
    }

/**
 * Returns this [Result] if it is [Ok], otherwise returns the result of applying
 * the provided function to the contained error.
 *
 * @param f The function to apply to the contained error
 * @return This [Result] if it is [Ok], otherwise the result of applying [f] to the error
 */
inline fun <T, E> Result<T, E>.orElse(f: (E) -> Result<T, E>): Result<T, E> =
    when (this) {
        is Ok -> this
        is Err -> f(error)
    }

/**
 * A type alias for a [Result] that cannot fail (the error type is [Nothing]).
 */
typealias InfallibleResult<T> = Result<T, Nothing>

/**
 * Exception thrown when unwrapping a [Result] fails.
 *
 * @property msg The error message
 */
data class UnwrapException(
    val msg: String,
) : RuntimeException(msg)

/**
 * Runs the provided function and returns its result wrapped in [Ok] if it completes successfully,
 * or the caught exception wrapped in [Err] if it throws.
 *
 * @param block The function to run
 * @return [Ok] with the result if successful, [Err] with the exception if it throws
 * @see [Result.lift]
 */
inline fun <T> runCatching(crossinline block: () -> T): Result<T, Throwable> =
    try {
        Ok(block())
    } catch (e: Throwable) {
        Err(e)
    }

/**
 * Runs the provided function and returns its result wrapped in [Ok] if it completes successfully,
 * or the caught exception wrapped in [Err] if it throws.
 *
 * @param block The function to run
 * @return [Ok] with the result if successful, [Err] with the exception if it throws
 * @see [Result.lift]
 */
inline infix fun <T, U> T.runCatching(crossinline block: T.() -> U): Result<U, Throwable> =
    try {
        Ok(block())
    } catch (e: Throwable) {
        Err(e)
    }

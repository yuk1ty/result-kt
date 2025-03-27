package com.github.yuk1ty.resultKt.binding

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok

/**
 * Shamelessly borrowed from kotlin-result's binding function, with bit fixes.
 * https://github.com/michaelbull/kotlin-result/blob/db27396a1f859f91f42f6121eff512e592b7cb4b/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Binding.kt
 */

/**
 * Provides a convenient way to chain multiple operations that return [Result] in a sequential manner.
 * This function allows for early return semantics similar to Rust's `?` operator.
 *
 * @param block A function that executes within a [ResultScope] context
 * @param V The success type of the returned [Result]
 * @param E The error type of the returned [Result]
 * @return [Ok] with the result of the block if all operations succeed, or the first [Err] encountered
 */
inline fun <V, E> result(crossinline block: ResultScope<E>.() -> V): Result<V, E> =
    with(ResultScopeImpl<E>()) {
        try {
            Ok(block())
        } catch (_: RaisingException) {
            result!!
        }
    }

/**
 * Internal exception used to implement the early return mechanism in the [result] function.
 * This exception is caught by the [result] function and should never be exposed to users.
 */
internal object RaisingException : Exception() {
    private fun readResolve(): Any = RaisingException
}

/**
 * Scope interface for the [result] function that provides the ability to unwrap [Result] values
 * or return early with an error.
 *
 * @param E The error type
 */
interface ResultScope<E> {
    /**
     * Unwraps a [Result] value or returns early with the error.
     * This is an operator function that mimics Rust's `?` operator.
     *
     * @return The unwrapped value if this is [Ok]
     * @throws RaisingException if this is [Err], which is caught by the [result] function
     */
    @Suppress("DANGEROUS_CHARACTERS")
    fun <V> Result<V, E>.`?`(): V = raise()

    /**
     * Unwraps a [Result] value or returns early with the error.
     *
     * @return The unwrapped value if this is [Ok]
     * @throws RaisingException if this is [Err], which is caught by the [result] function
     */
    fun <V> Result<V, E>.raise(): V
}

/**
 * Implementation of [ResultScope] used internally by the [result] function.
 *
 * @param E The error type
 */
@PublishedApi
internal class ResultScopeImpl<E> : ResultScope<E> {
    /**
     * Stores the first encountered error to be returned by the [result] function.
     */
    var result: Result<Nothing, E>? = null

    /**
     * Implementation of [ResultScope.raise] that unwraps a [Result] value or throws
     * [RaisingException] after storing the error.
     *
     * @return The unwrapped value if this is [Ok]
     * @throws RaisingException if this is [Err]
     */
    override fun <V> Result<V, E>.raise(): V {
        when (this) {
            is Ok -> return value
            is Err -> {
                result = Err(error)
                throw RaisingException
            }
        }
    }
}

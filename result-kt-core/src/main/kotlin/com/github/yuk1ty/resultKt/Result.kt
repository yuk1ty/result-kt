package com.github.yuk1ty.resultKt

import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok

sealed interface Result<out T, out E> {
    fun isOk(): Boolean
    fun isErr(): Boolean

    @JvmInline
    value class Ok<T>(val value: T) : Result<T, Nothing> {
        override fun isOk(): Boolean = true
        override fun isErr(): Boolean = false
    }

    @JvmInline
    value class Err<E>(val error: E) : Result<Nothing, E> {
        override fun isOk(): Boolean = false
        override fun isErr(): Boolean = true
    }
}

fun <T, E> Result<T, E>.ok(): T? = when (this) {
    is Ok -> value
    is Err -> null
}

fun <T, E> Result<T, E>.err(): E? = when (this) {
    is Ok -> null
    is Err -> error
}

fun <T, E> Result<T, E>.unwrap(): T = when (this) {
    is Ok -> value
    is Err -> throw UnwrapException("called `unwrap()` on an `Err` value: $error")
}

fun <T, E> Result<T, E>.unwrapErr(): E = when (this) {
    is Ok -> throw UnwrapException("called `unwrapErr()` on an `Ok` value: $value")
    is Err -> error
}

fun <T, E> Result<T, E>.unwrapOr(default: T): T = when (this) {
    is Ok -> value
    is Err -> default
}

inline fun <T, E> Result<T, E>.unwrapOrElse(f: (E) -> T): T = when (this) {
    is Ok -> value
    is Err -> f(error)
}

fun <T, E : Throwable> Result<T, E>.unwrapOrThrow(): T = when (this) {
    is Ok -> value
    is Err -> throw error
}

fun <T, E> Result<T, E>.expect(msg: String): T = when (this) {
    is Ok -> value
    is Err -> throw UnwrapException(msg)
}

fun <T, E> Result<T, E>.expectErr(msg: String): E = when (this) {
    is Ok -> throw UnwrapException(msg)
    is Err -> error
}

inline fun <T, U, E> Result<T, E>.map(f: (T) -> U): Result<U, E> = when (this) {
    is Ok -> Ok(f(value))
    is Err -> Err(error)
}

inline fun <T, U, E> Result<T, E>.mapOr(f: (T) -> U, default: U): U = when (this) {
    is Ok -> f(value)
    is Err -> default
}

inline fun <T, U, E> Result<T, E>.mapOrElse(default: (E) -> U, f: (T) -> U): U = when (this) {
    is Ok -> f(value)
    is Err -> default(error)
}

inline fun <T, E, F> Result<T, E>.mapErr(f: (E) -> F): Result<T, F> = when (this) {
    is Ok -> Ok(value)
    is Err -> Err(f(error))
}

fun <T, U, E> Result<T, E>.flatMap(f: (T) -> Result<U, E>): Result<U, E> = when (this) {
    is Ok -> f(value)
    is Err -> Err(error)
}

inline fun <T, E> Result<T, E>.inspect(f: (T) -> Unit): Result<T, E> {
    if (this is Ok) f(value)
    return this
}

inline fun <T, E> Result<T, E>.inspectErr(f: (E) -> Unit): Result<T, E> {
    if (this is Err) f(error)
    return this
}

fun <T, E> Result<T?, E>.transpose(): Result<T, E>? = when (this) {
    is Ok -> this.value?.let { Ok(it) }
    is Err -> Err(this.error)
}

fun <T, E> Result<Result<T, E>, E>.flatten(): Result<T, E> = when (this) {
    is Ok -> value
    is Err -> Err(error)
}

inline fun <T> runCatching(block: () -> T): Result<T, Throwable> = try {
    Ok(block())
} catch (e: Throwable) {
    Err(e)
}

fun <T, E> Result<T, E>.and(other: Result<T, E>): Result<T, E> = when (this) {
    is Ok -> other
    is Err -> this
}

fun <T, E> Result<T, E>.andThen(f: (T) -> Result<T, E>): Result<T, E> = this.flatMap(f)

fun <T, E> Result<T, E>.or(other: Result<T, E>): Result<T, E> = when (this) {
    is Ok -> this
    is Err -> other
}

inline fun <T, E> Result<T, E>.orElse(f: (E) -> Result<T, E>): Result<T, E> = when (this) {
    is Ok -> this
    is Err -> f(error)
}

typealias InfallibleResult<T> = Result<T, Nothing>

data class UnwrapException(val msg: String) : RuntimeException(msg)
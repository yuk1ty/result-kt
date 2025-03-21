package com.github.yuk1ty.resultKt.binding

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok

/**
 * Shamelessly borrowed from kotlin-result's binding function, with bit fixes.
 * https://github.com/michaelbull/kotlin-result/blob/db27396a1f859f91f42f6121eff512e592b7cb4b/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Binding.kt
 */

inline fun <V, E> result(crossinline block: ResultScope<E>.() -> V): Result<V, E> =
    with(ResultScopeImpl<E>()) {
        try {
            Ok(block())
        } catch (_: RaisingException) {
            result!!
        }
    }

internal object RaisingException : Exception() {
    private fun readResolve(): Any = RaisingException
}

interface ResultScope<E> {
    @Suppress("DANGEROUS_CHARACTERS")
    fun <V> Result<V, E>.`?`(): V = raise()

    fun <V> Result<V, E>.raise(): V
}

@PublishedApi
internal class ResultScopeImpl<E> : ResultScope<E> {
    var result: Result<Nothing, E>? = null

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

package com.github.yuk1ty.resultKt.binding

import com.github.yuk1ty.resultKt.*
import com.github.yuk1ty.resultKt.Result.Ok
import com.github.yuk1ty.resultKt.Result.Err

/**
 * Shamelessly borrowed from kotlin-result's binding function, with bit fixes.
 * https://github.com/michaelbull/kotlin-result/blob/db27396a1f859f91f42f6121eff512e592b7cb4b/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Binding.kt
 */

inline fun <V, E> binding(crossinline block: BindingScope<E>.() -> V): Result<V, E> {
    return with(BindingScopeImpl<E>()) {
        try {
            Ok(block())
        } catch (_: BindException) {
            result!!
        }
    }
}

internal object BindException : Exception() {
    private fun readResolve(): Any = BindException
}

interface BindingScope<E> {
    fun <V> Result<V, E>.bind(): V
}

@PublishedApi
internal class BindingScopeImpl<E> : BindingScope<E> {

    var result: Result<Nothing, E>? = null

    override fun <V> Result<V, E>.bind(): V {
        when (this) {
            is Ok -> return value
            is Err -> {
                result = Err(error)
                throw BindException
            }
        }
    }
}
package com.github.yuk1ty.resultKt.util

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Ok
import com.github.yuk1ty.resultKt.Result.Err

fun <T, E> List<Result<T, E>>.combine(): Result<List<T>, E> {
    val values = map { result ->
        when (result) {
            is Ok -> result.value
            is Err -> return result
        }
    }
    return Ok(values)
}

fun <T, E> List<Result<T, E>>.combineOrAccumulate(): Result<List<T>, List<E>> {
    val (values, errors) = this.run {
        val values = mutableListOf<T>()
        val errors = mutableListOf<E>()
        for (result in this) {
            when (result) {
                is Ok -> values += result.value
                is Err -> errors += result.error
            }
        }

        return@run Pair(values, errors)
    }
    return if (errors.isEmpty()) {
        Ok(values)
    } else {
        Err(errors)
    }
}
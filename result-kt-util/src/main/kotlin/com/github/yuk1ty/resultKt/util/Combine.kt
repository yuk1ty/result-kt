package com.github.yuk1ty.resultKt.util

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok

/**
 * Combines a list of [Result] values into a single [Result] containing a list of values.
 * If all results are [Ok], returns an [Ok] containing a list of all values.
 * If any result is [Err], returns the first [Err] encountered.
 *
 * @return [Ok] with a list of all values if all results are [Ok], or the first [Err] encountered
 */
fun <T, E> List<Result<T, E>>.combine(): Result<List<T>, E> {
    val values =
        map { result ->
            when (result) {
                is Ok -> result.value
                is Err -> return result
            }
        }
    return Ok(values)
}

/**
 * Combines a list of [Result] values into a single [Result] containing a list of values,
 * accumulating all errors if any are present.
 * If all results are [Ok], returns an [Ok] containing a list of all values.
 * If any results are [Err], returns an [Err] containing a list of all errors.
 *
 * @return [Ok] with a list of all values if all results are [Ok], or [Err] with a list of all errors
 */
fun <T, E> List<Result<T, E>>.combineOrAccumulate(): Result<List<T>, List<E>> {
    val (values, errors) =
        this.run {
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

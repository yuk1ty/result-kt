package com.github.yuk1ty.resultKt.util

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok

/**
 * Applies one of two provided functions depending on the variant of this [Result].
 * If this is [Ok], applies [okFn] to the contained value.
 * If this is [Err], applies [errFn] to the contained error.
 *
 * @param okFn The function to apply if this is [Ok]
 * @param errFn The function to apply if this is [Err]
 * @param T The type of the contained value in [Ok]
 * @param U The type of the result of both functions
 * @param E The type of the contained error in [Err]
 * @return The result of applying the appropriate function
 */
inline fun <T, U, E> Result<T, E>.fold(
    okFn: (T) -> U,
    errFn: (E) -> U,
): U =
    when (this) {
        is Ok -> okFn(value)
        is Err -> errFn(error)
    }

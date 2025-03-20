package com.github.yuk1ty.resultKt.util

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok

inline fun <T, U, E> Result<T, E>.fold(okFn: (T) -> U, errFn: (E) -> U): U = when (this) {
    is Ok -> okFn(value)
    is Err -> errFn(error)
}

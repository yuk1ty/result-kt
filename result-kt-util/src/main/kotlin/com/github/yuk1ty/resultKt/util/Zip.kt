package com.github.yuk1ty.resultKt.util

import com.github.yuk1ty.resultKt.*
import com.github.yuk1ty.resultKt.Result.Ok
import com.github.yuk1ty.resultKt.Result.Err

private typealias Producer<T, E> = () -> Result<T, E>

fun <A, B, C> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    f: (A, B) -> C
): Result<C, Nothing> {
    return first().flatMap { second().map { second -> f(it, second) } }
}

fun <A, B, C, D> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    third: Producer<C, Nothing>,
    f: (A, B, C) -> D
): Result<D, Nothing> {
    return first().flatMap { one ->
        second().flatMap { two ->
            third().map { three -> f(one, two, three) }
        }
    }
}

fun <A, B, C, D, E> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    third: Producer<C, Nothing>,
    fourth: Producer<D, Nothing>,
    f: (A, B, C, D) -> E
): Result<E, Nothing> {
    return first().flatMap { one ->
        second().flatMap { two ->
            third().flatMap { three ->
                fourth().map { four -> f(one, two, three, four) }
            }
        }
    }
}

fun <A, B, C, D, E, F> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    third: Producer<C, Nothing>,
    fourth: Producer<D, Nothing>,
    fifth: Producer<E, Nothing>,
    f: (A, B, C, D, E) -> F
): Result<F, Nothing> {
    return first().flatMap { one ->
        second().flatMap { two ->
            third().flatMap { three ->
                fourth().flatMap { four ->
                    fifth().map { five -> f(one, two, three, four, five) }
                }
            }
        }
    }
}

inline fun <A, B, C, E> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    f: (A, B) -> C
): Result<C, List<E>> {
    val firstResult = first()
    val secondResult = second()

    val results = listOf(firstResult, secondResult)

    return if (results.all { it.isOk() }) {
        val firstValue = firstResult.unwrap()
        val secondValue = secondResult.unwrap()
        Ok(f(firstValue, secondValue))
    } else {
        val errors = results.filter { it.isErr() }.map { it.unwrapErr() }
        Err(errors)
    }
}

inline fun <A, B, C, D, E> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    third: Producer<C, E>,
    f: (A, B, C) -> D
): Result<D, List<E>> {
    val firstResult = first()
    val secondResult = second()
    val thirdResult = third()

    val results = listOf(firstResult, secondResult, thirdResult)

    return if (results.all { it.isOk() }) {
        val firstValue = firstResult.unwrap()
        val secondValue = secondResult.unwrap()
        val thirdValue = thirdResult.unwrap()
        Ok(f(firstValue, secondValue, thirdValue))
    } else {
        val errors = results.filter { it.isErr() }.map { it.unwrapErr() }
        Err(errors)
    }
}

inline fun <A, B, C, D, E, F> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    third: Producer<C, E>,
    fourth: Producer<D, E>,
    f: (A, B, C, D) -> F
): Result<F, List<E>> {
    val firstResult = first()
    val secondResult = second()
    val thirdResult = third()
    val fourthResult = fourth()

    val results = listOf(firstResult, secondResult, thirdResult, fourthResult)

    return if (results.all { it.isOk() }) {
        val firstValue = firstResult.unwrap()
        val secondValue = secondResult.unwrap()
        val thirdValue = thirdResult.unwrap()
        val fourthValue = fourthResult.unwrap()
        Ok(f(firstValue, secondValue, thirdValue, fourthValue))
    } else {
        val errors = results.filter { it.isErr() }.map { it.unwrapErr() }
        Err(errors)
    }
}

inline fun <A, B, C, D, E, F, G> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    third: Producer<C, E>,
    fourth: Producer<D, E>,
    fifth: Producer<F, E>,
    f: (A, B, C, D, F) -> G
): Result<G, List<E>> {
    val firstResult = first()
    val secondResult = second()
    val thirdResult = third()
    val fourthResult = fourth()
    val fifthResult = fifth()

    val results = listOf(firstResult, secondResult, thirdResult, fourthResult, fifthResult)

    return if (results.all { it.isOk() }) {
        val firstValue = firstResult.unwrap()
        val secondValue = secondResult.unwrap()
        val thirdValue = thirdResult.unwrap()
        val fourthValue = fourthResult.unwrap()
        val fifthValue = fifthResult.unwrap()
        Ok(f(firstValue, secondValue, thirdValue, fourthValue, fifthValue))
    } else {
        val errors = results.filter { it.isErr() }.map { it.unwrapErr() }
        Err(errors)
    }
}
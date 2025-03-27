package com.github.yuk1ty.resultKt.util

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok
import com.github.yuk1ty.resultKt.flatMap
import com.github.yuk1ty.resultKt.map
import com.github.yuk1ty.resultKt.unwrap
import com.github.yuk1ty.resultKt.unwrapErr

/**
 * Type alias for a function that produces a [Result].
 *
 * @param T The type of the value in case of success
 * @param E The type of the error in case of failure
 */
private typealias Producer<T, E> = () -> Result<T, E>

/**
 * Combines the results of two producers using a combining function.
 * Returns [Ok] with the combined result if both producers succeed, or the first [Err] encountered.
 *
 * @param first The first producer
 * @param second The second producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the combined result
 * @return [Ok] with the combined result if both producers succeed, or the first [Err] encountered
 */
fun <A, B, C> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    f: (A, B) -> C,
): Result<C, Nothing> = first().flatMap { second().map { second -> f(it, second) } }

/**
 * Combines the results of three producers using a combining function.
 * Returns [Ok] with the combined result if all producers succeed, or the first [Err] encountered.
 *
 * @param first The first producer
 * @param second The second producer
 * @param third The third producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the third producer's value
 * @param D The type of the combined result
 * @return [Ok] with the combined result if all producers succeed, or the first [Err] encountered
 */
fun <A, B, C, D> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    third: Producer<C, Nothing>,
    f: (A, B, C) -> D,
): Result<D, Nothing> =
    first().flatMap { one ->
        second().flatMap { two ->
            third().map { three -> f(one, two, three) }
        }
    }

/**
 * Combines the results of four producers using a combining function.
 * Returns [Ok] with the combined result if all producers succeed, or the first [Err] encountered.
 *
 * @param first The first producer
 * @param second The second producer
 * @param third The third producer
 * @param fourth The fourth producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the third producer's value
 * @param D The type of the fourth producer's value
 * @param E The type of the combined result
 * @return [Ok] with the combined result if all producers succeed, or the first [Err] encountered
 */
fun <A, B, C, D, E> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    third: Producer<C, Nothing>,
    fourth: Producer<D, Nothing>,
    f: (A, B, C, D) -> E,
): Result<E, Nothing> =
    first().flatMap { one ->
        second().flatMap { two ->
            third().flatMap { three ->
                fourth().map { four -> f(one, two, three, four) }
            }
        }
    }

/**
 * Combines the results of five producers using a combining function.
 * Returns [Ok] with the combined result if all producers succeed, or the first [Err] encountered.
 *
 * @param first The first producer
 * @param second The second producer
 * @param third The third producer
 * @param fourth The fourth producer
 * @param fifth The fifth producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the third producer's value
 * @param D The type of the fourth producer's value
 * @param E The type of the fifth producer's value
 * @param F The type of the combined result
 * @return [Ok] with the combined result if all producers succeed, or the first [Err] encountered
 */
fun <A, B, C, D, E, F> zip(
    first: Producer<A, Nothing>,
    second: Producer<B, Nothing>,
    third: Producer<C, Nothing>,
    fourth: Producer<D, Nothing>,
    fifth: Producer<E, Nothing>,
    f: (A, B, C, D, E) -> F,
): Result<F, Nothing> =
    first().flatMap { one ->
        second().flatMap { two ->
            third().flatMap { three ->
                fourth().flatMap { four ->
                    fifth().map { five -> f(one, two, three, four, five) }
                }
            }
        }
    }

/**
 * Combines the results of two producers using a combining function, accumulating errors if any.
 * Returns [Ok] with the combined result if both producers succeed, or [Err] with a list of all errors.
 *
 * @param first The first producer
 * @param second The second producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the combined result
 * @param E The type of the error
 * @return [Ok] with the combined result if both producers succeed, or [Err] with a list of all errors
 */
inline fun <A, B, C, E> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    f: (A, B) -> C,
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

/**
 * Combines the results of three producers using a combining function, accumulating errors if any.
 * Returns [Ok] with the combined result if all producers succeed, or [Err] with a list of all errors.
 *
 * @param first The first producer
 * @param second The second producer
 * @param third The third producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the third producer's value
 * @param D The type of the combined result
 * @param E The type of the error
 * @return [Ok] with the combined result if all producers succeed, or [Err] with a list of all errors
 */
inline fun <A, B, C, D, E> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    third: Producer<C, E>,
    f: (A, B, C) -> D,
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

/**
 * Combines the results of four producers using a combining function, accumulating errors if any.
 * Returns [Ok] with the combined result if all producers succeed, or [Err] with a list of all errors.
 *
 * @param first The first producer
 * @param second The second producer
 * @param third The third producer
 * @param fourth The fourth producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the third producer's value
 * @param D The type of the fourth producer's value
 * @param E The type of the error
 * @param F The type of the combined result
 * @return [Ok] with the combined result if all producers succeed, or [Err] with a list of all errors
 */
inline fun <A, B, C, D, E, F> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    third: Producer<C, E>,
    fourth: Producer<D, E>,
    f: (A, B, C, D) -> F,
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

/**
 * Combines the results of five producers using a combining function, accumulating errors if any.
 * Returns [Ok] with the combined result if all producers succeed, or [Err] with a list of all errors.
 *
 * @param first The first producer
 * @param second The second producer
 * @param third The third producer
 * @param fourth The fourth producer
 * @param fifth The fifth producer
 * @param f The function to combine the results
 * @param A The type of the first producer's value
 * @param B The type of the second producer's value
 * @param C The type of the third producer's value
 * @param D The type of the fourth producer's value
 * @param F The type of the fifth producer's value
 * @param G The type of the combined result
 * @param E The type of the error
 * @return [Ok] with the combined result if all producers succeed, or [Err] with a list of all errors
 */
inline fun <A, B, C, D, E, F, G> zipOrAccumulate(
    first: Producer<A, E>,
    second: Producer<B, E>,
    third: Producer<C, E>,
    fourth: Producer<D, E>,
    fifth: Producer<F, E>,
    f: (A, B, C, D, F) -> G,
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

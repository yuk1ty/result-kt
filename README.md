# result-kt

Another implementation of `Result` type in Kotlin, hugely inspired by Rust's `Result` type.

## Motivation

Kotlin doesn't have a `Result` type like Rust, which is a type that represents either success or failure of an operation. This library aims to provide a `Result` type in Kotlin that is similar to Rust's `Result` type.

This library doesn't aim to provide a monadic type of `Result`. It sticks to provide a taste of imperative programming since Kotlin is not a functional programming language. The point is different from existing great prior arts – [Arrow](https://arrow-kt.io/) and [kotlin-result](https://github.com/michaelbull/kotlin-result).
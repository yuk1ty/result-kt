package com.github.yuk1ty.resultKt.binding

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Ok
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.runCatching as ktRunCatching
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class BindingTest : DescribeSpec({
    describe("try block and ? operator") {
        it("should return Ok(42) if the string '42' passed") {
            fun parse(s: String): Result<Int, Throwable> {
                return ktRunCatching { s.toInt() }
            }

            fun lift(s: String): Result<String, Throwable> {
                return Ok(s)
            }

            val result = result {
                val lifted = lift("42").raise()
                val parsed = parse(lifted).`?`()
                var sum = 0
                for (i in 0..parsed) {
                    sum += parsed
                }
                sum
            }

            result shouldBe Ok(1806)
        }

        it("should return Err(NumberFormatException) if the string '42a' passed") {
            fun parse(s: String): Result<Int, Throwable> {
                return ktRunCatching { s.toInt() }
            }

            fun lift(s: String): Result<String, Throwable> {
                return Ok(s)
            }

            val result = result {
                val lifted = lift("42a").`?`()
                val parsed = parse(lifted).`?`()
                // The logic suspended here.
                // Here won't run.
                var sum = 0
                for (i in 0..parsed) {
                    sum += parsed
                }
                sum
            }

            result.shouldBeTypeOf<Err<NumberFormatException>>()
        }
    }
})
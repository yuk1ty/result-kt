package com.github.yuk1ty.resultKt.util

import com.github.yuk1ty.resultKt.Result
import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class FoldableTest :
    DescribeSpec({
        describe("fold()") {
            it("should return mapped value if Result is Ok") {
                val result = Ok(42)
                result.fold({ it + 1 }, { "error" }) shouldBe 43
            }

            it("should return mapped error if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.fold({ it + 1 }, { it.replaceFirstChar { it.titlecase() } }) shouldBe "Error"
            }
        }
    })

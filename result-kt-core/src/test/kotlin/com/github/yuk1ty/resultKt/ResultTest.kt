package com.github.yuk1ty.resultKt

import com.github.yuk1ty.resultKt.Result.Err
import com.github.yuk1ty.resultKt.Result.Ok
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import com.github.yuk1ty.resultKt.runCatching as ktRunCatching

class ResultTest :
    DescribeSpec({
        describe("ok()") {
            it("should return value if Result is Ok") {
                val result = Ok(42)
                result.ok() shouldBe 42
            }

            it("should return null if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.ok() shouldBe null
            }
        }

        describe("err()") {
            it("should return error if Result is Err") {
                val result = Err("error")
                result.err() shouldBe "error"
            }

            it("should return null if Result is Ok") {
                val result: Result<Int, String> = Ok(42)
                result.err() shouldBe null
            }
        }

        describe("unwrap()") {
            it("should return value if Result is Ok") {
                val result = Ok(42)
                result.unwrap() shouldBe 42
            }

            it("should throw UnwrapException if Result is Err") {
                val result: Result<Int, String> = Err("error")
                shouldThrow<UnwrapException> { result.unwrap() }
            }
        }

        describe("unwrapErr()") {
            it("should return error if Result is Err") {
                val result = Err("error")
                result.unwrapErr() shouldBe "error"
            }

            it("should throw UnwrapException if Result is Ok") {
                val result: Result<Int, String> = Ok(42)
                shouldThrow<UnwrapException> { result.unwrapErr() }
            }
        }

        describe("unwrapOr()") {
            it("should return value if Result is Ok") {
                val result = Ok(42)
                result.unwrapOr(0) shouldBe 42
            }

            it("should return default value if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.unwrapOr(0) shouldBe 0
            }
        }

        describe("unwrapOrElse()") {
            it("should return value if Result is Ok") {
                val result = Ok(42)
                result.unwrapOrElse { 0 } shouldBe 42
            }

            it("should return value from lambda if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.unwrapOrElse { 0 } shouldBe 0
            }
        }

        describe("unwrapOrThrow()") {
            it("should return value if Result is Ok") {
                val result = Ok(42)
                result.unwrapOrThrow() shouldBe 42
            }

            it("should throw error if Result is Err") {
                val result: Result<Int, Exception> = Err(Exception("error"))
                val error = shouldThrow<Exception> { result.unwrapOrThrow() }
                error.message shouldBe "error"
            }
        }

        describe("expect()") {
            it("should return value if Result is Ok") {
                val result = Ok(42)
                result.expect("error") shouldBe 42
            }

            it("should throw UnwrapException if Result is Err") {
                val result: Result<Int, String> = Err("error")
                val error = shouldThrow<UnwrapException> { result.expect("message") }
                error.message shouldBe "message"
            }
        }

        describe("expectErr()") {
            it("should return error if Result is Err") {
                val result = Err("error")
                result.expectErr("message") shouldBe "error"
            }

            it("should throw UnwrapException if Result is Ok") {
                val result: Result<Int, String> = Ok(42)
                val error = shouldThrow<UnwrapException> { result.expectErr("message") }
                error.message shouldBe "message"
            }
        }

        describe("map()") {
            it("should return new Result with mapped value if Result is Ok") {
                val result = Ok(42)
                result.map { it + 1 } shouldBe Ok(43)
            }

            it("should return Err if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.map { it.toString() } shouldBe Err("error")
            }
        }

        describe("mapOr()") {
            it("should return mapped value if Result is Ok") {
                val result = Ok(42)
                result.mapOr({ it + 1 }, 0) shouldBe 43
            }

            it("should return default value if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.mapOr({ it.toString() }, "default") shouldBe "default"
            }
        }

        describe("mapOrElse()") {
            it("should return mapped value if Result is Ok") {
                val result = Ok(42)
                result.mapOrElse({ "error" }, { it + 1 }) shouldBe 43
            }

            it("should return value from lambda if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.mapOrElse({ it.replaceFirstChar { it.titlecase() } }, { 0 }) shouldBe "Error"
            }
        }

        describe("mapErr()") {
            it("should return new Result with mapped error if Result is Err") {
                val result = Err("error")
                result.mapErr { it.replaceFirstChar { it.titlecase() } } shouldBe Err("Error")
            }

            it("should return Ok if Result is Ok") {
                val result: Result<Int, String> = Ok(42)
                result.mapErr { it.toInt() } shouldBe Ok(42)
            }
        }

        describe("flatMap()") {
            it("should return new Result with mapped value if Result is Ok") {
                val result = Ok(42)
                result.flatMap { Ok(it + 1) } shouldBe Ok(43)
            }

            it("should return Err if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.flatMap { Ok(it.toString()) } shouldBe Err("error")
            }
        }

        describe("inspect()") {
            it("should return the same Result if Result is Ok") {
                val result = Ok(42)
                result.inspect { println(it) } shouldBe Ok(42)
            }

            it("should return the same Result if Result is Err") {
                val result: Result<Int, String> = Err("error")
                // Nothing will be printed
                result.inspect { println(it) } shouldBe Err("error")
            }
        }

        describe("inspectErr()") {
            it("should return the same Result if Result is Ok") {
                val result = Ok(42)
                // Nothing will be printed
                result.inspectErr<Int, Unit> { println(it) } shouldBe Ok(42)
            }

            it("should return the same Result if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.inspectErr { println(it) } shouldBe Err("error")
            }
        }

        describe("transpose()") {
            it("should return Ok if Result is Ok and value is not null") {
                val result = Ok(42)
                result.transpose() shouldBe Ok(42)
            }

            it("should return null if Result is Ok and value is null") {
                val result: Result<Int?, String> = Ok(null)
                result.transpose() shouldBe null
            }

            it("should return Err if Result is Err") {
                val result: Result<Int, String> = Err("error")
                result.transpose() shouldBe Err("error")
            }
        }

        describe("flatten()") {
            it("should return value if Result is Ok") {
                val result = Ok(Ok(42))
                result.flatten() shouldBe Ok(42)
            }

            it("should return Err if Result is Err") {
                val result: Result<Result<Int, String>, String> = Err("error")
                result.flatten() shouldBe Err("error")
            }
        }

        describe("runCatching()") {
            it("should return Ok if block does not throw error") {
                ktRunCatching { 42 } shouldBe Ok(42)
            }

            it("should return Err if block throws error") {
                data class CustomException(
                    override val message: String,
                ) : Exception(message)
                ktRunCatching { throw CustomException("error") } shouldBe Err(CustomException("error"))
            }
        }

        describe("and()") {
            it("should return other Result if self is Ok") {
                val self = Ok(42)
                val other = Ok(43)
                self.and(other) shouldBe Ok(43)
            }

            it("should return self if self is Err") {
                val self: Result<Int, String> = Err("error")
                val other = Ok(43)
                self.and(other) shouldBe Err("error")
            }
        }

        describe("or()") {
            it("should return self if self is Ok") {
                val self = Ok(42)
                val other = Ok(43)
                self.or(other) shouldBe Ok(42)
            }

            it("should return other Result if self is Err") {
                val self: Result<Int, String> = Err("error")
                val other = Ok(43)
                self.or(other) shouldBe Ok(43)
            }
        }
    })

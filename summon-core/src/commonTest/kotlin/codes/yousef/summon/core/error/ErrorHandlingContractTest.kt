package codes.yousef.summon.core.error

import kotlin.test.*

class ErrorHandlingContractTest {
    @Test
    fun resultAccessorsCoverSuccessAndFailureWithoutEvaluatingUnusedFallbacks() {
        val success: SummonResult<Int, String> = SummonResult.Success(7)
        val failure: SummonResult<Int, String> = SummonResult.Failure("bad")
        assertTrue(success.isSuccess()); assertFalse(success.isFailure())
        assertFalse(failure.isSuccess()); assertTrue(failure.isFailure())
        assertEquals(7, success.getOrNull()); assertNull(failure.getOrNull())
        assertNull(success.getErrorOrNull()); assertEquals("bad", failure.getErrorOrNull())
        assertEquals(7, success.getOrElse { error("must not run") })
        assertEquals(9, failure.getOrElse { 9 })
        assertEquals(7, success.getOrThrow())
        assertFailsWith<SummonException> { failure.getOrThrow() }
    }

    @Test
    fun handlerMapsThrownValuesValidationAndNullability() {
        assertEquals("ok", (ErrorHandler.runCatching { "ok" } as SummonResult.Success).value)
        assertIs<SummonResult.Failure<String, Throwable>>(ErrorHandler.runCatching { error("no") })
        assertIs<SummonResult.Success<Unit, ValidationException>>(ErrorHandler.validate(true) { "unused" })
        assertEquals("invalid", (ErrorHandler.validate(false) { "invalid" } as SummonResult.Failure).error.message)
        assertIs<SummonResult.Success<Unit, ValidationException>>(ErrorHandler.validateAll(true to "unused"))
        val invalid = ErrorHandler.validateAll(false to "first", true to "unused", false to "second") as SummonResult.Failure
        assertEquals(listOf("first", "second"), invalid.error.errors)
        assertEquals("value", (ErrorHandler.requireNotNull("value") { "unused" } as SummonResult.Success).value)
        assertEquals("missing", (ErrorHandler.requireNotNull<String>(null) { "missing" } as SummonResult.Failure).error.message)
    }

    @Test
    fun validationExtensionsAndMessagesCoverBoundaries() {
        assertEquals(1, 1.requireInRange(1..2) { "range" })
        assertFailsWith<IllegalArgumentException> { 3.requireInRange(1..2) { "range" } }
        assertEquals(1, 1.requirePositive())
        assertFailsWith<IllegalArgumentException> { 0.requirePositive() }
        assertEquals("x", "x".requireNotBlank())
        assertFailsWith<IllegalArgumentException> { " ".requireNotBlank() }
        assertIs<SummonResult.Success<String, String>>("x".toSummonResult())
        assertEquals("none", (null as String?).toSummonResult("none").getErrorOrNull())
        assertEquals("Component not found: Name", ErrorHandler.Messages.componentNotFound("Name"))
        assertEquals("Invalid value for field: null", ErrorHandler.Messages.invalidValue("field", null))
        assertEquals("size must be between 1 and 3, but was 4", ErrorHandler.Messages.outOfRange("size", 4, 1, 3))
    }
}

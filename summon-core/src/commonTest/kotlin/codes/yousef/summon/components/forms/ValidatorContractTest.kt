package codes.yousef.summon.components.forms

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidatorContractTest {
    @Test
    fun builtInValidatorsAcceptBoundariesAndRejectInvalidValues() {
        fun assertValidator(validator: Validator, invalid: String, valid: String, expected: String) {
            assertEquals(expected, validator.validate(invalid))
            assertNull(validator.validate(valid))
        }

        assertValidator(Validator.required(), " \t", "x", "This field is required")
        assertValidator(Validator.email(), "bad", "name+tag@example.test", "Must be a valid email address")
        assertNull(Validator.email().validate(""))
        assertValidator(Validator.minLength(3), "ab", "abc", "Must be at least 3 characters")
        assertValidator(Validator.maxLength(3), "abcd", "abc", "Must be at most 3 characters")
        assertValidator(Validator.pattern("[A-Z]+"), "abc", "ABC", "Invalid format")
        assertNull(Validator.pattern("[A-Z]+").validate(""))
        assertValidator(Validator.min(2.5), "2.4", "2.5", "Must be at least 2.5")
        assertEquals("Must be a number", Validator.min(2.5).validate("x"))
        assertValidator(Validator.max(2.5), "2.6", "2.5", "Must be at most 2.5")
        assertEquals("Must be a number", Validator.max(2.5).validate("x"))
        assertValidator(Validator.url(), "ftp://example.test", "https://example.test:8443/a?b=c", "Must be a valid URL")
        assertNull(Validator.url().validate(""))
        assertValidator(Validator.matches("same"), "other", "same", "Values do not match")
        assertValidator(Validator.custom { if (it == "bad") "custom" else null }, "bad", "good", "custom")

        assertEquals("first", validateValue("", listOf(Validator.required("first"), Validator.minLength(2, "second"))))
        assertEquals(listOf("first", "second"), validateValueAll("", listOf(Validator.required("first"), Validator.minLength(2, "second"))))
        assertNull(validateValue("valid", emptyList()))
    }

    @Test
    fun factoriesPreserveCustomMessagesAndResultModels() {
        assertEquals("required", Validator.required("required").validate(""))
        assertEquals("email", Validator.email("email").validate("bad"))
        assertEquals("min", Validator.minLength(2, "min").validate("x"))
        assertEquals("max", Validator.maxLength(1, "max").validate("xx"))
        assertEquals("pattern", Validator.pattern("x", "pattern").validate("y"))
        assertEquals("number-min", Validator.min(2.0, "number-min").validate("1"))
        assertEquals("number-max", Validator.max(2.0, "number-max").validate("3"))
        assertEquals("url", Validator.url("url").validate("invalid"))
        assertEquals("matches", Validator.matches("x", "matches").validate("y"))
        assertEquals(ValidationResult.Sync("error"), ValidationResult.Sync("error"))
        assertEquals(ValidationResult.Async(true, null), ValidationResult.Async(true, null))
        assertEquals(ServerValidationRequest("name", "value"), ServerValidationRequest("name", "value"))
        assertEquals(ServerValidationResponse(false, "error"), ServerValidationResponse(false, "error"))
    }

    @Test
    fun validationStateTracksSynchronousAndAsynchronousErrors() = runTest {
        val state = FormValidationState()
        state.registerField("name", listOf(Validator.required("name required")))
        state.registerField("age", listOf(Validator.min(18.0, "adult only")))
        assertEquals("name required", state.validateField("name", ""))
        assertTrue(state.hasError("name"))
        assertFalse(state.isValid())
        assertEquals(mapOf("name" to null, "age" to "adult only"), state.validateAll(mapOf("name" to "Ada", "age" to "17")))
        assertEquals("adult only", state.getError("age"))

        state.registerAsyncValidator("name", object : AsyncValidator {
            override suspend fun validate(value: String): String? = if (value == "taken") "already taken" else null
        })
        assertEquals("name required", state.validateFieldAsync("name", ""))
        assertEquals("already taken", state.validateFieldAsync("name", "taken"))
        assertNull(state.validateFieldAsync("name", "available"))
        assertNull(state.validateFieldAsync("unregistered", "anything"))

        state.clearFieldError("age")
        assertFalse(state.hasError("age"))
        state.clearAllErrors()
        assertTrue(state.isValid())
        assertNull(state.getError("name"))
    }
}

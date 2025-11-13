package codes.yousef.summon.components.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Mock FormField implementations for testing
private class MockValidField(override val label: String?, override val value: String) : FormField {
    override fun validate(): Boolean = true
}

private class MockInvalidField(override val label: String?, override val value: String) : FormField {
    override fun validate(): Boolean = false
}

class FormStateTest { // Renamed from FormTest to FormStateTest to be specific

    @Test
    fun testRegisterField() {
        val formState = FormState()
        val field1 = MockValidField("name", "test")
        formState.registerField(field1)
        // Test internal state if possible, or rely on validate/submit tests
        // For now, we assume registration works if validate/submit work correctly.
    }

    @Test
    fun testValidate_AllValid() {
        val formState = FormState()
        formState.registerField(MockValidField("field1", "value1"))
        formState.registerField(MockValidField("field2", "value2"))
        assertTrue(formState.validate(), "Validation should pass when all fields are valid.")
    }

    @Test
    fun testValidate_SomeInvalid() {
        val formState = FormState()
        formState.registerField(MockValidField("field1", "value1"))
        formState.registerField(MockInvalidField("field2", "value2"))
        formState.registerField(MockValidField("field3", "value3"))
        assertFalse(formState.validate(), "Validation should fail when at least one field is invalid.")
    }

    @Test
    fun testValidate_AllInvalid() {
        val formState = FormState()
        formState.registerField(MockInvalidField("field1", "value1"))
        formState.registerField(MockInvalidField("field2", "value2"))
        assertFalse(formState.validate(), "Validation should fail when all fields are invalid.")
    }

    @Test
    fun testValidate_NoFields() {
        val formState = FormState()
        assertTrue(formState.validate(), "Validation should pass when there are no fields.")
    }

    @Test
    fun testSubmit_ValidForm() {
        val formState = FormState()
        val field1 = MockValidField("name", "Alice")
        val field2 = MockValidField("email", "alice@example.com")
        val field3 = MockValidField(null, "anonValue") // Field with null label
        formState.registerField(field1)
        formState.registerField(field2)
        formState.registerField(field3)

        var submittedData: Map<String, String>? = null
        val onSubmitLambda: (Map<String, String>) -> Unit = { data -> submittedData = data }

        val submitResult = formState.submit(onSubmitLambda)

        assertTrue(submitResult, "Submit should return true for a valid form.")
        assertEquals(3, submittedData?.size, "Submitted data should contain 3 entries.")
        assertEquals("Alice", submittedData?.get("name"), "Name field value mismatch.")
        assertEquals("alice@example.com", submittedData?.get("email"), "Email field value mismatch.")
        // Check if null label uses hashcode (or similar placeholder)
        assertEquals("anonValue", submittedData?.get(field3.hashCode().toString()), "Null label field value mismatch.")

    }

    @Test
    fun testSubmit_InvalidForm() {
        val formState = FormState()
        val field1 = MockValidField("name", "Bob")
        val field2 = MockInvalidField("email", "bob@") // Invalid field
        formState.registerField(field1)
        formState.registerField(field2)

        var onSubmitCalled = false
        val onSubmitLambda: (Map<String, String>) -> Unit = { onSubmitCalled = true }

        val submitResult = formState.submit(onSubmitLambda)

        assertFalse(submitResult, "Submit should return false for an invalid form.")
        assertFalse(onSubmitCalled, "onSubmit lambda should not be called for an invalid form.")
    }

    @Test
    fun testSubmit_NoFields() {
        val formState = FormState()
        var submittedData: Map<String, String>? = null
        val onSubmitLambda: (Map<String, String>) -> Unit = { data -> submittedData = data }

        val submitResult = formState.submit(onSubmitLambda)

        assertTrue(submitResult, "Submit should return true for a form with no fields.")
        assertTrue(submittedData?.isEmpty() ?: false, "Submitted data should be empty for a form with no fields.")
    }
} 
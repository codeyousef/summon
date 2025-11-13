package codes.yousef.summon.runtime

import codes.yousef.summon.core.FlowContentCompat
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for the FormContent typealias.
 */
class FormContentTest {

    @Test
    fun testFormContentIsFlowContent() {
        // This test verifies that FormContent is a typealias for FlowContentCompat

        // Get the KClass objects for both types
        val formContentClass: KClass<*> = FormContent::class
        val flowContentClass: KClass<*> = FlowContentCompat::class

        // For a typealias, the KClass objects should be the same
        assertTrue(formContentClass == flowContentClass,
            "FormContent should be a typealias for FlowContentCompat"
        )
    }
}

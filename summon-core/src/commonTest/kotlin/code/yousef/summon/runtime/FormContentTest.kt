package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.reflect.KClass
import kotlinx.html.FlowContent

/**
 * Tests for the FormContent typealias.
 */
class FormContentTest {

    @Test
    fun testFormContentIsFlowContent() {
        // This test verifies that FormContent is a typealias for FlowContent

        // Get the KClass objects for both types
        val formContentClass: KClass<*> = FormContent::class
        val flowContentClass: KClass<*> = FlowContent::class

        // For a typealias, the KClass objects should be the same
        assertTrue(formContentClass == flowContentClass, 
            "FormContent should be a typealias for FlowContent")
    }
}

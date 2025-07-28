package code.yousef.example.springboot

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Unit tests for Summon components in the Spring Boot example.
 * Tests the integration between Spring Boot and the Summon UI framework.
 */
class SummonComponentsTest {
    
    private val renderer = SummonRenderer()

    @Test
    fun `HeroComponent renders with correct structure and content`() {
        // Given
        val username = "TestUser"

        // When
        val result = renderer.renderComponent { HeroComponent(username) }

        // Then
        assertTrue(result.contains("Welcome to Spring Boot + Summon, $username!"))
        assertTrue(result.contains("Build reactive web applications"))
        assertTrue(result.contains("Get Started"))
        assertContainsClass(result, "hero-section")
    }

    @Test
    fun `CounterComponent manages state correctly`() {
        // Given
        val initialValue = 5

        // When
        val result = renderer.renderComponent { CounterComponent(initialValue) }

        // Then
        assertTrue(result.contains(initialValue.toString()))
        assertTrue(result.contains("Increment"))
        assertTrue(result.contains("Decrement"))
        assertTrue(result.contains("Reset"))
        assertContainsClass(result, "counter-component")
    }

    @Test
    fun `FeatureCardsComponent renders all feature cards`() {
        // When
        val result = renderer.renderComponent { FeatureCardsComponent() }

        // Then
        assertTrue(result.contains("Key Features"))
        assertTrue(result.contains("Reactive UI"))
        assertTrue(result.contains("Spring Integration"))
        assertTrue(result.contains("Type-Safe"))
        assertContainsClass(result, "feature-cards")
    }

    @Test
    fun `ContactFormComponent renders form elements`() {
        // When
        val result = renderer.renderComponent { ContactFormComponent() }

        // Then
        assertTrue(result.contains("Contact Us"))
        assertTrue(result.contains("Name"))
        assertTrue(result.contains("Email"))
        assertTrue(result.contains("Subject"))
        assertTrue(result.contains("Message"))
        assertTrue(result.contains("Send Message"))
        assertContainsClass(result, "contact-form")
    }

    @Test
    fun `UserTableComponent renders user data correctly`() {
        // Given
        val users = listOf(
            User(1, "John Doe", "john@example.com", "admin", true),
            User(2, "Jane Smith", "jane@example.com", "user", false)
        )

        // When
        val result = renderer.renderComponent { UserTableComponent(users) }

        // Then
        assertTrue(result.contains("John Doe"))
        assertTrue(result.contains("jane@example.com"))
        assertTrue(result.contains("admin"))
        assertTrue(result.contains("user"))
        assertContainsClass(result, "user-table")
    }

    @Test
    fun `DashboardComponent renders statistics`() {
        // When
        val result = renderer.renderComponent { DashboardComponent() }

        // Then
        assertTrue(result.contains("Dashboard"))
        assertTrue(result.contains("Users"))
        assertTrue(result.contains("Revenue"))
        assertTrue(result.contains("Products"))
        assertTrue(result.contains("Orders"))
        assertContainsClass(result, "dashboard")
    }

    @Test
    fun `CounterComponent state updates work`() {
        // Given
        val initialValue = 0

        // When
        val result = renderer.renderComponent { CounterComponent(initialValue) }

        // Then
        assertTrue(result.contains("0"))

        // Test with updated value
        val updatedResult = renderer.renderComponent { CounterComponent(10) }
        assertTrue(updatedResult.contains("10"))
    }

    @Test
    fun `Components use Summon Modifier system for styling`() {
        // When
        val result = renderer.renderComponent { HeroComponent("Test") }

        // Then - Should contain Summon-generated CSS properties
        assertContainsStyle(result, "background-color")
        assertContainsStyle(result, "padding")
        assertContainsStyle(result, "text-align")
    }

    // Helper functions

    private fun assertContainsClass(html: String, className: String) {
        assertTrue(
            html.contains("class=\"$className") || html.contains("class='$className") ||
                    html.contains(" $className ") || html.contains(" $className\"") ||
                    html.contains(" $className'"),
            "HTML should contain class '$className'"
        )
    }

    private fun assertContainsStyle(html: String, styleProperty: String) {
        assertTrue(
            html.contains("style=\"") && html.contains("$styleProperty:"),
            "HTML should contain style property '$styleProperty'"
        )
    }

    @Test
    fun `AddUserFormComponent renders form fields with correct name attributes`() {
        // When
        val result = renderer.renderComponent { AddUserFormComponent() }
        
        // Then - Check that form fields have proper name attributes
        assertTrue(result.contains("Add New User"))
        
        // Check name field
        assertTrue(result.contains("name=\"name\"") || result.contains("name='name'"), 
            "Expected name field to have name='name' attribute. HTML: $result")
        
        // Check email field  
        assertTrue(result.contains("name=\"email\"") || result.contains("name='email'"),
            "Expected email field to have name='email' attribute. HTML: $result")
            
        // Check role field
        assertTrue(result.contains("name=\"role\"") || result.contains("name='role'"),
            "Expected role field to have name='role' attribute. HTML: $result")
            
        // Also check that form has correct action
        assertTrue(result.contains("action=\"/users\"") || result.contains("action='/users'"))
        assertTrue(result.contains("method=\"POST\"") || result.contains("method='POST'"))
    }
}
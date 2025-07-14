package code.yousef.example.springboot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for Summon components in the Spring Boot example.
 * Following TDD approach - these tests will fail initially.
 */
class SummonComponentsTest {
    
    @Test
    fun `HeroComponent renders with correct structure and content`() {
        // Given
        val username = "TestUser"
        
        // When
        val result = HeroComponent(username)
        
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
        val result = CounterComponent(initialValue)
        
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
        val result = FeatureCardsComponent()
        
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
        val result = ContactFormComponent()
        
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
        val result = UserTableComponent(users)
        
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
        val result = DashboardComponent()
        
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
        val counter = mutableStateOf(0)
        
        // When
        val result = CounterComponentWithState(counter)
        
        // Update state
        counter.value = 10
        val updatedResult = CounterComponentWithState(counter)
        
        // Then
        assertTrue(result.contains("0"))
        assertTrue(updatedResult.contains("10"))
    }
    
    @Test
    fun `Components use Summon Modifier system for styling`() {
        // When
        val result = HeroComponent("Test")
        
        // Then - Should contain Summon-generated CSS properties
        assertContainsStyle(result, "background")
        assertContainsStyle(result, "padding")
        assertContainsStyle(result, "text-align")
    }
    
    // Helper functions
    
    private fun assertContainsClass(html: String, className: String) {
        assertTrue(html.contains("class=\"$className") || html.contains("class='$className") || 
                   html.contains(" $className ") || html.contains(" $className\"") || 
                   html.contains(" $className'"),
                  "HTML should contain class '$className'")
    }
    
    private fun assertContainsStyle(html: String, styleProperty: String) {
        assertTrue(html.contains("style=") && html.contains("$styleProperty:"),
                  "HTML should contain style property '$styleProperty'")
    }
}
package code.yousef.summon.annotation

import kotlin.test.*

class AppRegistryTest {
    
    @BeforeTest
    fun setup() {
        // Clear the registry before each test
        AppRegistry.clear()
    }
    
    @Test
    fun testDefaultAppEntry() {
        // When no custom app is registered, getAppEntry should return the default
        val appEntry = AppRegistry.getAppEntry()
        assertNotNull(appEntry)
        
        // The default should be SummonApp (we can't directly compare functions, but we know it's the default)
        // We could test the behavior by rendering it, but that would require a full renderer setup
    }
    
    @Test
    fun testRegisterCustomApp() {
        var customAppCalled = false
        var contentCalled = false
        
        val customApp: @Composable ((@Composable () -> Unit) -> Unit) = { content ->
            customAppCalled = true
            content()
        }
        
        // Register the custom app
        AppRegistry.registerApp(customApp)
        
        // Get the registered app
        val appEntry = AppRegistry.getAppEntry()
        
        // Test that our custom app is called
        appEntry {
            contentCalled = true
        }
        
        assertTrue(customAppCalled, "Custom app should have been called")
        assertTrue(contentCalled, "Content should have been called")
    }
    
    @Test
    fun testMultipleRegistrationsWarning() {
        var lastAppCalled = false
        
        val firstApp: @Composable ((@Composable () -> Unit) -> Unit) = { content ->
            content()
        }
        
        val secondApp: @Composable ((@Composable () -> Unit) -> Unit) = { content ->
            lastAppCalled = true
            content()
        }
        
        // Register multiple apps
        AppRegistry.registerApp(firstApp)
        AppRegistry.registerApp(secondApp) // This should log a warning
        
        // Get the registered app - should be the last one
        val appEntry = AppRegistry.getAppEntry()
        
        // Test that the last registered app is used
        appEntry { }
        
        assertTrue(lastAppCalled, "Last registered app should have been called")
    }
    
    @Test
    fun testClearRegistry() {
        val customApp: @Composable ((@Composable () -> Unit) -> Unit) = { content ->
            content()
        }
        
        // Register a custom app
        AppRegistry.registerApp(customApp)
        
        // Clear the registry
        AppRegistry.clear()
        
        // After clearing, should return default app
        val appEntry = AppRegistry.getAppEntry()
        
        // This should be the default SummonApp again
        // We can't directly test this without a full render setup
        assertNotNull(appEntry)
    }
}
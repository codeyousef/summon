package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CompositionSystemTest {
    
    /**
     * Test class to capture composition results
     */
    class TestResult {
        var compositionCount = 0
        var latestValue: String = ""
    }
    
    /**
     * Test composable function that generates content based on state
     */
    @Composable
    fun TestComposable(state: SummonMutableState<String>, result: TestResult) {
        result.compositionCount++
        result.latestValue = state.value
    }
    
    /**
     * Test composable with multiple states and composition tracking
     */
    @Composable
    fun ComplexComposable(
        firstName: SummonMutableState<String>,
        lastName: SummonMutableState<String>,
        result: TestResult
    ) {
        result.compositionCount++
        result.latestValue = "${firstName.value} ${lastName.value}"
        
        // Use remember to test slot system
        val remembered = remember { "Cached Value" }
        assertTrue(remembered == "Cached Value", "Remember should work correctly")
    }
    
    @Test
    fun testBasicComposition() {
        // Create a composer
        val composer = JvmComposer.create()
        val result = TestResult()
        
        // Set up composition context
        CompositionLocal.provideComposer(composer) {
            // Create a state
            val state = mutableStateOf("Initial Value")
            
            // First composition
            TestComposable(state, result)
            
            // Verify first composition occurred
            assertEquals(1, result.compositionCount)
            assertEquals("Initial Value", result.latestValue)
            
            // Change state and trigger recomposition
            state.value = "Updated Value"
            
            // Since we're not in a real UI environment, manually process recompositions
            RecomposerHolder.recomposer.processRecompositions {
                TestComposable(state, result)
            }
            
            // Verify recomposition occurred
            assertEquals(2, result.compositionCount)
            assertEquals("Updated Value", result.latestValue)
        }
        
        // Clean up
        composer.dispose()
    }
    
    @Test
    fun testRememberAcrossRecompositions() {
        // Create a composer
        val composer = JvmComposer.create()
        
        // Test values to be remembered
        var rememberedValue: String? = null
        var recompositionCount = 0
        
        // Set up composition context
        CompositionLocal.provideComposer(composer) {
            // First composition
            recompositionCount++
            
            // Remember should preserve this value across recompositions
            rememberedValue = remember { "Cached through recomposition" }
            
            // Trigger a recomposition
            if (recompositionCount == 1) {
                composer.reportChanged()
                
                // Since we're not in a real UI environment, manually process recompositions
                RecomposerHolder.recomposer.processRecompositions {
                    // The remembered block will be executed again
                }
            }
        }
        
        // Verify remember worked
        assertEquals("Cached through recomposition", rememberedValue)
        assertEquals(2, recompositionCount)
        
        // Clean up
        composer.dispose()
    }
    
    @Test
    fun testComplexStateAndRecomposition() {
        // Create a composer
        val composer = JvmComposer.create()
        val result = TestResult()
        
        // Set up composition context
        CompositionLocal.provideComposer(composer) {
            // Create multiple states
            val firstName = mutableStateOf("John")
            val lastName = mutableStateOf("Doe")
            
            // First composition
            ComplexComposable(firstName, lastName, result)
            
            // Verify first composition
            assertEquals(1, result.compositionCount)
            assertEquals("John Doe", result.latestValue)
            
            // Change first name only
            firstName.value = "Jane"
            
            // Process recomposition
            RecomposerHolder.recomposer.processRecompositions {
                ComplexComposable(firstName, lastName, result)
            }
            
            // Verify recomposition
            assertEquals(2, result.compositionCount)
            assertEquals("Jane Doe", result.latestValue)
            
            // Change last name only
            lastName.value = "Smith"
            
            // Process recomposition
            RecomposerHolder.recomposer.processRecompositions {
                ComplexComposable(firstName, lastName, result)
            }
            
            // Verify recomposition again
            assertEquals(3, result.compositionCount)
            assertEquals("Jane Smith", result.latestValue)
        }
        
        // Clean up
        composer.dispose()
    }
    
    @Test
    fun testDisposableEffect() {
        // Create a composer
        val composer = JvmComposer.create()
        
        // Tracking for disposable effect
        var setupCalled = 0
        var cleanupCalled = 0
        
        // Set up composition context
        CompositionLocal.provideComposer(composer) {
            // Simple test of disposable effect
            // We'll just track that the dispose function gets called
            setupCalled++
            
            // Register a disposable that should be called when disposed
            composer.registerDisposable {
                cleanupCalled++
            }
        }
        
        // Verify effect setup was called
        assertEquals(1, setupCalled)
        assertEquals(0, cleanupCalled)
        
        // Dispose the composer, which should trigger cleanup
        composer.dispose()
        
        // Verify cleanup was called
        assertEquals(1, setupCalled)
        assertEquals(1, cleanupCalled)
    }
} 
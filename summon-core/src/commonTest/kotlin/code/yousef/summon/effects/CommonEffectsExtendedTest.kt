package code.yousef.summon.effects

import code.yousef.summon.runtime.Composable
import kotlin.test.*
import code.yousef.summon.effects.TestCompositionScope

class CommonEffectsExtendedTest {

    @Test
    fun testUseDocumentTitle() {
        val scope = TestCompositionScope()
        val testTitle = "Test Title"

        // Call the useDocumentTitle function
        try {
            scope.useDocumentTitle(testTitle)
        } catch (e: Exception) {
            fail("Failed to invoke useDocumentTitle: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testUseKeyboardShortcut() {
        val scope = TestCompositionScope()
        val testKey = "A"
        val testModifiers = setOf(KeyModifier.CTRL)
        var handlerCalled = false

        // Call the useKeyboardShortcut function
        try {
            scope.useKeyboardShortcut(testKey, testModifiers) { 
                handlerCalled = true 
            }
        } catch (e: Exception) {
            fail("Failed to invoke useKeyboardShortcut: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testUseClickOutside() {
        val scope = TestCompositionScope()
        val elementRef = ElementRef()
        var handlerCalled = false

        // Call the useClickOutside function
        try {
            scope.useClickOutside(elementRef) { 
                handlerCalled = true 
            }
        } catch (e: Exception) {
            fail("Failed to invoke useClickOutside: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testUseLocation() {
        val scope = TestCompositionScope()

        // Call the useLocation function
        val locationState = try {
            scope.useLocation()
        } catch (e: Exception) {
            fail("Failed to invoke useLocation: ${e.message}")
            null
        }

        // Verify compose was called and state was returned
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
        assertNotNull(locationState, "useLocation should return a state")

        // Verify initial state
        assertEquals("/", locationState?.value?.pathname, "Initial pathname should be '/'")
        assertEquals("", locationState?.value?.search, "Initial search should be empty")
        assertEquals("", locationState?.value?.hash, "Initial hash should be empty")
    }

    @Test
    fun testUseLocalStorage() {
        val scope = TestCompositionScope()
        val testKey = "testKey"
        val initialValue = "initialValue"

        // Call the useLocalStorage function
        val storageState = try {
            scope.useLocalStorage(testKey, initialValue) { it }
        } catch (e: Exception) {
            fail("Failed to invoke useLocalStorage: ${e.message}")
            null
        }

        // Verify compose was called and state was returned
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
        assertNotNull(storageState, "useLocalStorage should return a state")

        // Verify initial state
        assertEquals(initialValue, storageState?.value, "Initial value should be set")
    }

    @Test
    fun testUseMediaQuery() {
        val scope = TestCompositionScope()
        val testQuery = "(min-width: 768px)"

        // Call the useMediaQuery function
        val mediaQueryState = try {
            scope.useMediaQuery(testQuery)
        } catch (e: Exception) {
            fail("Failed to invoke useMediaQuery: ${e.message}")
            null
        }

        // Verify compose was called and state was returned
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
        assertNotNull(mediaQueryState, "useMediaQuery should return a state")

        // Verify initial state
        assertFalse(mediaQueryState?.value ?: true, "Initial value should be false")
    }

    @Test
    fun testUseWindowSize() {
        val scope = TestCompositionScope()

        // Call the useWindowSize function
        val windowSizeState = try {
            scope.useWindowSize()
        } catch (e: Exception) {
            fail("Failed to invoke useWindowSize: ${e.message}")
            null
        }

        // Verify compose was called and state was returned
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
        assertNotNull(windowSizeState, "useWindowSize should return a state")

        // Verify initial state
        assertEquals(800, windowSizeState?.value?.width, "Initial width should be 800")
        assertEquals(600, windowSizeState?.value?.height, "Initial height should be 600")
    }
}

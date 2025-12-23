package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.getPlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Test-driven development tests for SSR functionality.
 * These tests are designed to fail initially and demonstrate the issues with the current SSR implementation.
 * Once the implementation is fixed, these tests should pass.
 */
class ServerSideRenderingJvmTest {

    @Test
    fun testRenderComposableWithText() {
        // This test should fail initially because renderComposableRoot doesn't properly handle @Composable functions
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            Text("Hello, SSR World!", modifier = Modifier())
        }

        println("HTML Output: $html")

        // Verify HTML contains expected content
        assertTrue(html.contains("Hello, SSR World!"), "HTML should contain the text content")
        assertTrue(html.contains("<html"), "HTML should contain html tag")
        assertTrue(html.contains("<body"), "HTML should contain body tag")
        assertTrue(html.contains("<span"), "Text should render as span element")
    }

    @Test
    fun testRenderComposableWithButton() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            Button(
                onClick = { println("Button clicked") },
                label = "Click Me",
                modifier = Modifier()
            )
        }

        println("Button HTML: $html")

        // Verify button renders correctly
        assertTrue(html.contains("<button"), "Should render button element")
        assertTrue(html.contains("Click Me"), "Button should contain text")
    }

    @Test
    fun testRenderComposableWithState() {
        // This test will fail because mutableStateOf and remember won't work without composition context
        val renderer = PlatformRenderer()

        @Composable
        fun CounterComponent() {
            val count = remember { mutableStateOf(0) }
            Column(modifier = Modifier()) {
                Text("Count: ${count.value}", modifier = Modifier())
                Button(
                    onClick = { count.value++ },
                    label = "Increment",
                    modifier = Modifier()
                )
            }
        }

        val html = renderer.renderComposableRoot {
            CounterComponent()
        }

        println("Counter HTML: $html")

        // Verify state-based rendering works
        assertTrue(html.contains("Count: 0"), "Should render initial count")
        assertTrue(html.contains("Increment"), "Should render increment button")
        assertTrue(html.contains("<button"), "Should render button element")
    }

    @Test
    fun testRenderNestedComposables() {
        val renderer = PlatformRenderer()

        @Composable
        fun NestedComponent() {
            Column(modifier = Modifier()) {
                Text("Outer Text", modifier = Modifier())
                Row(modifier = Modifier()) {
                    Text("Left Text", modifier = Modifier())
                    Text("Right Text", modifier = Modifier())
                }
                Text("Bottom Text", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            NestedComponent()
        }

        println("Nested HTML: $html")

        // Verify nested structure
        assertTrue(html.contains("Outer Text"), "Should contain outer text")
        assertTrue(html.contains("Left Text"), "Should contain left text")
        assertTrue(html.contains("Right Text"), "Should contain right text")
        assertTrue(html.contains("Bottom Text"), "Should contain bottom text")
    }

    @Test
    fun testPlatformRendererAccessDuringSSR() {
        // This test will fail because getPlatformRenderer() is not properly initialized
        val renderer = PlatformRenderer()

        @Composable
        fun ComponentThatUsesRenderer() {
            // This should work during SSR
            val currentRenderer = getPlatformRenderer()
            assertNotNull(currentRenderer, "Should be able to access platform renderer during SSR")

            Text("Renderer accessed successfully", modifier = Modifier())
        }

        val html = renderer.renderComposableRoot {
            ComponentThatUsesRenderer()
        }

        assertTrue(html.contains("Renderer accessed successfully"), "Should render content after accessing renderer")
    }

    @Test
    fun testSSRWithActualComponents() {
        // Full integration test using real Summon components
        val renderer = PlatformRenderer()

        @Composable
        fun App() {
            Column(modifier = Modifier()) {
                Text("Welcome to Summon SSR", modifier = Modifier())

                Row(modifier = Modifier()) {
                    Button(
                        onClick = { println("Login") },
                        label = "Login",
                        modifier = Modifier()
                    )

                    Button(
                        onClick = { println("Register") },
                        label = "Register",
                        modifier = Modifier()
                    )
                }

                Text("© 2024 Summon Framework", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            App()
        }

        println("App HTML: $html")

        // Verify complete app structure
        assertTrue(html.contains("Welcome to Summon SSR"), "Should contain welcome text")
        assertTrue(html.contains("Login"), "Should contain login button")
        assertTrue(html.contains("Register"), "Should contain register button")
        assertTrue(html.contains("© 2024 Summon Framework"), "Should contain footer")
        assertTrue(html.contains("<button"), "Should contain button elements")
    }

    @Test
    fun testServerSideRenderUtilsWithComposable() {
        // Test the SSR utility functions
        @Composable
        fun SimpleApp() {
            Text("Hello from ServerSideRenderUtils", modifier = Modifier())
        }

        // This will fail because ServerSideRenderUtils doesn't have proper implementation
        try {
            val html = ServerSideRenderUtils.renderPageToString(
                rootComposable = { SimpleApp() }
            )

            assertTrue(html.contains("Hello from ServerSideRenderUtils"), "Should render composable content")
            assertTrue(html.contains("<html"), "Should contain HTML structure")
        } catch (e: Exception) {
            fail("ServerSideRenderUtils should be able to render composables, but failed with: ${e.message}")
        }
    }

    @Test
    fun testSSRWithHydration() {
        val renderer = PlatformRenderer()

        @Composable
        fun HydrationApp() {
            val isClient = remember { mutableStateOf(false) }
            Text("Hydration: ${if (isClient.value) "Client" else "Server"}", modifier = Modifier())
        }

        val html = renderer.renderComposableRootWithHydration {
            HydrationApp()
        }

        println("Hydration HTML: $html")

        // Should render server-side content and include hydration data
        assertTrue(html.contains("Hydration: Server"), "Should render server-side state")
        assertTrue(
            html.contains("data-summon-hydrate") || html.contains("hydration"),
            "Should contain hydration attributes or data"
        )
    }
}
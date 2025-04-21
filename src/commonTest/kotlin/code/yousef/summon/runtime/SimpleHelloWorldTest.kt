package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertNotNull

class SimpleHelloWorldTest {

    @Test
    fun testRender() {
        // This test simply verifies that the render method can be called without throwing exceptions
        SimpleHelloWorld.render()

        // If we reach this point, the test passes
        // We can't easily verify the console output in a multiplatform test
        assertNotNull(SimpleHelloWorld, "SimpleHelloWorld should not be null")
    }
}

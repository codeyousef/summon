package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertNotNull

class HelloMainTest {

    @Test
    fun testMain() {
        // This test simply verifies that the main function can be called without throwing exceptions
        main()

        // If we reach this point, the test passes
        // We can't easily verify the console output in a multiplatform test
        // Just assert that we can call the function without errors
        assertNotNull(::main, "main function should not be null")
    }
}
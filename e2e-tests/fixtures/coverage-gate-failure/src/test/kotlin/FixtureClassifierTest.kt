package fixture

import kotlin.test.Test
import kotlin.test.assertEquals

class FixtureClassifierTest {
    @Test
    fun positiveValueIsClassified() {
        assertEquals("positive", classify(1))
    }
}

package codes.yousef.summon.state

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RememberSaveableContractTest {
    @AfterTest
    fun clearRegistry() = clearSaveableStates()

    @Test
    fun changedValuesSurviveNewStateInstancesUntilExplicitlyCleared() {
        val first = rememberSaveable("counter", 1)
        assertFalse(hasSaveableState("counter"))
        first.value = 7
        assertTrue(hasSaveableState("counter"))
        assertEquals(7, rememberSaveable("counter", 99).value)
        assertEquals(7, rememberWithIdentifier("counter", 100).value)

        clearSaveableStates()
        assertFalse(hasSaveableState("counter"))
        assertEquals(2, rememberSaveable("counter", 2).value)
    }

    @Test
    fun registryPreservesTypedValuesAndNullsWithoutUnsafeCasts() {
        SaveableStateRegistry.set("text", "value")
        SaveableStateRegistry.set("nullable", null)
        assertEquals("value", SaveableStateRegistry.get<String>("text"))
        assertNull(SaveableStateRegistry.get<String>("nullable"))
        assertNull(SaveableStateRegistry.get<String>("missing"))
    }
}

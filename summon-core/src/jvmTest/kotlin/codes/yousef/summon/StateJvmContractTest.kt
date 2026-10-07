package codes.yousef.summon

import codes.yousef.summon.state.mutableStateOf
import java.nio.file.Files
import java.util.prefs.Preferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StateJvmContractTest {
    @Test
    fun observerReportsCurrentAndSubsequentValuesUntilRemoved() {
        val state = mutableStateOf(1)
        val values = mutableListOf<Int>()
        val observer: (Int) -> Unit = { values += it }
        val stateObserver = state.createObserver()
        stateObserver.addObserver(observer)
        state.value = 2
        stateObserver.removeObserver(observer)
        state.value = 3
        assertEquals(listOf(1, 2), values)
        assertEquals("State(value=3)", state.toJvmString())
    }

    @Test
    fun filePersistenceLoadsExistingContentAndWritesChanges() {
        val directory = Files.createTempDirectory("summon-state-")
        val file = directory.resolve("nested/state.txt")
        try {
            Files.createDirectories(file.parent)
            Files.writeString(file, "41")
            val state = mutableStateOf(0)
            state.persistToFile(file.toString(), Int::toString, String::toInt)
            assertEquals(41, state.value)
            state.value = 42
            assertEquals("42", Files.readString(file))

            val invalid = directory.resolve("invalid.txt")
            Files.writeString(invalid, "not-an-int")
            val fallback = mutableStateOf(7)
            fallback.persistToFile(invalid.toString(), Int::toString, String::toInt)
            assertEquals(7, fallback.value)
        } finally {
            directory.toFile().deleteRecursively()
        }
    }

    @Test
    fun preferencesSupportEveryDocumentedPrimitiveAndRejectObjects() {
        val nodeName = "codes.yousef.summon.tests.${System.nanoTime()}"
        val preferences = Preferences.userRoot().node(nodeName)
        try {
            val stringState = mutableStateOf("first").also { it.persistToPreferences("string", nodeName) }
            val intState = mutableStateOf(1).also { it.persistToPreferences("int", nodeName) }
            val longState = mutableStateOf(1L).also { it.persistToPreferences("long", nodeName) }
            val booleanState = mutableStateOf(true).also { it.persistToPreferences("boolean", nodeName) }
            val floatState = mutableStateOf(1.0f).also { it.persistToPreferences("float", nodeName) }
            val doubleState = mutableStateOf(1.0).also { it.persistToPreferences("double", nodeName) }

            stringState.value = "second"
            intState.value = 2
            longState.value = 2L
            booleanState.value = false
            floatState.value = 2.0f
            doubleState.value = 2.0
            assertEquals("second", preferences.get("string", null))
            assertEquals(2, preferences.getInt("int", 0))
            assertEquals(2L, preferences.getLong("long", 0))
            assertEquals(false, preferences.getBoolean("boolean", true))
            assertEquals(2.0f, preferences.getFloat("float", 0f))
            assertEquals(2.0, preferences.getDouble("double", 0.0))
            assertFailsWith<IllegalArgumentException> {
                mutableStateOf(listOf(1)).persistToPreferences("unsupported", nodeName)
            }
            assertTrue(preferences.keys().isNotEmpty())
        } finally {
            preferences.removeNode()
            Preferences.userRoot().flush()
        }
    }
}

package codes.yousef.summon.desktop.storage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JvmSyncedStorageContractTest {
    @Test
    fun instancesSynchronizeAndUnsubscribedOwnersStopReceivingUpdates() {
        val key = "sync-${System.nanoTime()}"
        val first = createSyncedStorage(key, 0, Int::toString, String::toInt)
        val second = createSyncedStorage(key, 0, Int::toString, String::toInt)
        val firstEvents = mutableListOf<Int>()
        val secondEvents = mutableListOf<Int>()
        val unsubscribeFirst = first.addChangeListener(firstEvents::add)
        second.addChangeListener(secondEvents::add)

        first.value = 7
        assertEquals(7, second.value)
        assertEquals(listOf(7), firstEvents)
        assertEquals(listOf(7), secondEvents)
        assertTrue(first.exists())

        unsubscribeFirst()
        unsubscribeFirst()
        second.value = 9
        assertEquals(listOf(7), firstEvents)
        assertEquals(listOf(7, 9), secondEvents)

        second.clear()
        assertFalse(first.exists())
        assertEquals(0, first.value)
        assertEquals(listOf(7, 9, 0), secondEvents)
    }

    @Test
    fun serializationAndListenerFailuresRemainIsolated() {
        val key = "failure-${System.nanoTime()}"
        val storage = createSyncedStorage(
            key,
            "default",
            serializer = { value -> if (value == "reject") error("reject") else value },
            deserializer = { value -> if (value == "bad") error("bad") else value }
        )
        val events = mutableListOf<String>()
        storage.addChangeListener { error("listener failure") }
        storage.addChangeListener(events::add)

        storage.value = "good"
        assertEquals("good", storage.value)
        assertEquals(listOf("good"), events)
        storage.value = "reject"
        assertEquals("good", storage.value)

        val corruptor = createSyncedStorage(key, "unused", { it }, { it })
        corruptor.value = "bad"
        assertEquals("default", storage.value)
        assertEquals(listOf("good", "default"), events)
    }
}

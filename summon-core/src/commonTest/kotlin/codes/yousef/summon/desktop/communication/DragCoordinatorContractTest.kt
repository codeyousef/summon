package codes.yousef.summon.desktop.communication

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DragCoordinatorContractTest {
    @Test
    fun everyDragProtocolMessageRoundTripsWithoutLosingIdentity() {
        val data = DragData(
            dragId = "drag-1",
            dataType = "private-record",
            payload = "opaque-reference",
            sourceWindow = "window-a",
            metadata = mapOf("effect" to "copy")
        )
        val messages = listOf<DragMessage>(
            DragMessage.DragStart(data),
            DragMessage.DragMove("drag-1", 12.5, -3.0),
            DragMessage.DragEnd("drag-1", cancelled = true),
            DragMessage.DropAccepted("drag-1", "window-b")
        )
        messages.forEach { message ->
            assertEquals(message, deserializeDragMessage(message.serialize()))
        }
    }

    @Test
    fun dragModelsIncludeEveryProtocolFieldInIdentity() {
        val data = DragData("id", "type", "payload", "source", mapOf("key" to "value"))
        listOf(
            data.copy(dragId = "other"),
            data.copy(dataType = "other"),
            data.copy(payload = "other"),
            data.copy(sourceWindow = "other"),
            data.copy(metadata = emptyMap())
        ).forEach { assertNotEquals(data, it) }
        assertNotEquals(DragMessage.DragMove("id", 1.0, 2.0), DragMessage.DragMove("id", 2.0, 2.0))
        assertNotEquals(DragMessage.DragEnd("id", false), DragMessage.DragEnd("id", true))
        assertNotEquals(DragMessage.DropAccepted("id", "a"), DragMessage.DropAccepted("id", "b"))
    }
}

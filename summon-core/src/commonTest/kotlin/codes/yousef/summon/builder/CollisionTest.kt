package codes.yousef.summon.builder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for CollisionDetector (Hit Testing) functionality.
 *
 * TEST DIRECTIVE: Place two boxes at known coordinates.
 * Simulate drag over Box B. Assert Box B has class `drop-target`.
 *
 * Note: Visual class changes require browser environment.
 * These tests verify the collision detection logic.
 */
class CollisionTest {

    @Test
    fun testPointInRect() {
        val rect = Rect(x = 100.0, y = 100.0, width = 200.0, height = 150.0)

        // Point inside
        assertTrue(CollisionDetector.pointInRect(150.0, 125.0, rect))
        assertTrue(CollisionDetector.pointInRect(100.0, 100.0, rect)) // Top-left corner
        assertTrue(CollisionDetector.pointInRect(299.0, 249.0, rect)) // Near bottom-right

        // Point outside
        assertFalse(CollisionDetector.pointInRect(50.0, 50.0, rect)) // Top-left of rect
        assertFalse(CollisionDetector.pointInRect(350.0, 300.0, rect)) // Bottom-right of rect
        assertFalse(CollisionDetector.pointInRect(150.0, 50.0, rect)) // Above
        assertFalse(CollisionDetector.pointInRect(150.0, 300.0, rect)) // Below
    }

    @Test
    fun testFindDropTargetWithSingleZone() {
        CollisionDetector.clearDropZones()

        val zone = DropZone(
            id = "zone-1",
            rect = Rect(0.0, 0.0, 100.0, 100.0)
        )
        CollisionDetector.registerDropZone(zone)

        // Point inside zone
        val target = CollisionDetector.findDropTarget(50.0, 50.0)
        assertEquals("zone-1", target?.id)

        // Point outside zone
        val noTarget = CollisionDetector.findDropTarget(150.0, 150.0)
        assertNull(noTarget)
    }

    @Test
    fun testFindDropTargetWithMultipleZones() {
        CollisionDetector.clearDropZones()

        // Box A at top-left
        val boxA = DropZone(
            id = "box-a",
            rect = Rect(0.0, 0.0, 100.0, 100.0)
        )

        // Box B at bottom-right
        val boxB = DropZone(
            id = "box-b",
            rect = Rect(200.0, 200.0, 100.0, 100.0)
        )

        CollisionDetector.registerDropZone(boxA)
        CollisionDetector.registerDropZone(boxB)

        // Drag over Box A
        val targetA = CollisionDetector.findDropTarget(50.0, 50.0)
        assertEquals("box-a", targetA?.id)

        // Drag over Box B
        val targetB = CollisionDetector.findDropTarget(250.0, 250.0)
        assertEquals("box-b", targetB?.id)

        // Drag between boxes (no target)
        val noTarget = CollisionDetector.findDropTarget(150.0, 150.0)
        assertNull(noTarget)
    }

    @Test
    fun testOverlappingZones() {
        CollisionDetector.clearDropZones()

        // Zone 1 (registered first, lower priority)
        val zone1 = DropZone(
            id = "zone-1",
            rect = Rect(0.0, 0.0, 200.0, 200.0)
        )

        // Zone 2 overlaps zone 1 (registered second, higher priority)
        val zone2 = DropZone(
            id = "zone-2",
            rect = Rect(100.0, 100.0, 150.0, 150.0)
        )

        CollisionDetector.registerDropZone(zone1)
        CollisionDetector.registerDropZone(zone2)

        // Point only in zone 1
        val onlyZone1 = CollisionDetector.findDropTarget(50.0, 50.0)
        assertEquals("zone-1", onlyZone1?.id)

        // Point in overlap area - should return zone registered later (zone-2)
        val overlap = CollisionDetector.findDropTarget(150.0, 150.0)
        assertEquals("zone-2", overlap?.id)
    }

    @Test
    fun testUnregisterDropZone() {
        CollisionDetector.clearDropZones()

        val zone = DropZone("removable", Rect(0.0, 0.0, 100.0, 100.0))
        CollisionDetector.registerDropZone(zone)

        // Verify it's registered
        assertEquals("removable", CollisionDetector.findDropTarget(50.0, 50.0)?.id)

        // Unregister
        CollisionDetector.unregisterDropZone("removable")

        // Verify it's gone
        assertNull(CollisionDetector.findDropTarget(50.0, 50.0))
    }

    @Test
    fun testClearDropZones() {
        CollisionDetector.registerDropZone(DropZone("z1", Rect(0.0, 0.0, 50.0, 50.0)))
        CollisionDetector.registerDropZone(DropZone("z2", Rect(100.0, 100.0, 50.0, 50.0)))

        CollisionDetector.clearDropZones()

        assertNull(CollisionDetector.findDropTarget(25.0, 25.0))
        assertNull(CollisionDetector.findDropTarget(125.0, 125.0))
    }

    @Test
    fun testEdgeCasesForCollision() {
        CollisionDetector.clearDropZones()

        val zone = DropZone("edge-zone", Rect(100.0, 100.0, 100.0, 100.0))
        CollisionDetector.registerDropZone(zone)

        // Exactly on boundaries
        assertTrue(CollisionDetector.pointInRect(100.0, 100.0, zone.rect)) // Top-left
        assertTrue(CollisionDetector.pointInRect(199.99, 199.99, zone.rect)) // Near bottom-right

        // Just outside
        assertFalse(CollisionDetector.pointInRect(99.99, 100.0, zone.rect))
        assertFalse(CollisionDetector.pointInRect(100.0, 99.99, zone.rect))
        assertFalse(CollisionDetector.pointInRect(200.01, 150.0, zone.rect))
        assertFalse(CollisionDetector.pointInRect(150.0, 200.01, zone.rect))
    }

    @Test
    fun testUpdateDropZoneRect() {
        CollisionDetector.clearDropZones()

        val zone = DropZone("movable", Rect(0.0, 0.0, 50.0, 50.0))
        CollisionDetector.registerDropZone(zone)

        // Initial position
        assertEquals("movable", CollisionDetector.findDropTarget(25.0, 25.0)?.id)
        assertNull(CollisionDetector.findDropTarget(200.0, 200.0))

        // Update the zone's position
        CollisionDetector.updateDropZone("movable", Rect(200.0, 200.0, 50.0, 50.0))

        // New position
        assertNull(CollisionDetector.findDropTarget(25.0, 25.0))
        assertEquals("movable", CollisionDetector.findDropTarget(225.0, 225.0)?.id)
    }
}

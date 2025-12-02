package codes.yousef.summon.builder

/**
 * Collision detection utilities for drag-and-drop in visual builder mode.
 *
 * Provides hit testing to determine which drop zone the cursor is over
 * during drag operations.
 *
 * ## Features
 *
 * - **Point-in-Rect Testing**: Check if a point is inside a rectangle
 * - **Drop Zone Registration**: Track potential drop targets
 * - **Visual Feedback**: Add/remove CSS classes for hover states
 *
 * ## Usage
 *
 * ```kotlin
 * // Register drop zones
 * CollisionDetector.registerDropZone("container-1", DropZone(
 *     id = "container-1",
 *     bounds = Bounds(x = 0.0, y = 0.0, width = 200.0, height = 400.0)
 * ))
 *
 * // During drag move
 * val hitZone = CollisionDetector.findDropZone(mouseX, mouseY)
 * if (hitZone != null) {
 *     CollisionDetector.highlightZone(hitZone.id)
 * }
 *
 * // On drop
 * val targetZone = CollisionDetector.findDropZone(dropX, dropY)
 * if (targetZone != null) {
 *     PropertyBridge.moveComponent(draggedId, targetZone.id)
 * }
 * ```
 *
 * @since 1.0.0
 */
object CollisionDetector {
    private val dropZones = mutableMapOf<String, DropZone>()
    private var currentHighlight: String? = null
    
    /**
     * Callback for applying visual feedback to a drop zone.
     */
    var onHighlight: ((zoneId: String?, add: Boolean) -> Unit)? = null
    
    /**
     * Registers a drop zone for collision detection.
     *
     * @param id Unique identifier for the zone
     * @param zone The drop zone definition
     */
    fun registerDropZone(id: String, zone: DropZone) {
        dropZones[id] = zone
    }
    
    /**
     * Unregisters a drop zone.
     *
     * @param id The zone ID to remove
     */
    fun unregisterDropZone(id: String) {
        dropZones.remove(id)
        if (currentHighlight == id) {
            clearHighlight()
        }
    }
    
    /**
     * Updates the bounds of a registered drop zone.
     *
     * @param id The zone ID
     * @param bounds New bounding rectangle
     */
    fun updateZoneBounds(id: String, bounds: Bounds) {
        dropZones[id]?.let { zone ->
            dropZones[id] = zone.copy(bounds = bounds)
        }
    }
    
    /**
     * Finds the drop zone containing the given point.
     *
     * If multiple zones overlap, returns the first matching zone.
     * For more sophisticated handling, use [findAllDropZones].
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return The drop zone at the point, or null if none
     */
    fun findDropZone(x: Double, y: Double): DropZone? {
        return dropZones.values.find { zone ->
            zone.enabled && zone.bounds.contains(x, y)
        }
    }
    
    /**
     * Finds all drop zones containing the given point.
     *
     * Useful for handling nested containers where a point might
     * be inside multiple zones.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return List of matching drop zones, sorted by area (smallest first)
     */
    fun findAllDropZones(x: Double, y: Double): List<DropZone> {
        return dropZones.values
            .filter { zone -> zone.enabled && zone.bounds.contains(x, y) }
            .sortedBy { it.bounds.area() }
    }
    
    /**
     * Highlights a drop zone (adds visual feedback).
     *
     * @param zoneId The zone to highlight
     */
    fun highlightZone(zoneId: String) {
        if (currentHighlight != zoneId) {
            clearHighlight()
            currentHighlight = zoneId
            onHighlight?.invoke(zoneId, true)
        }
    }
    
    /**
     * Clears any current highlight.
     */
    fun clearHighlight() {
        currentHighlight?.let { id ->
            onHighlight?.invoke(id, false)
        }
        currentHighlight = null
    }
    
    /**
     * Gets the currently highlighted zone ID.
     */
    fun getHighlightedZone(): String? = currentHighlight
    
    /**
     * Clears all registered drop zones.
     */
    fun clearAll() {
        clearHighlight()
        dropZones.clear()
    }
    
    /**
     * Returns all registered drop zone IDs.
     */
    fun getZoneIds(): Set<String> = dropZones.keys.toSet()
}

/**
 * Represents a drop zone for drag-and-drop operations.
 *
 * @property id Unique identifier
 * @property bounds Bounding rectangle
 * @property acceptTypes Component types this zone accepts (empty = all)
 * @property enabled Whether the zone is currently accepting drops
 * @property data Custom data associated with the zone
 */
data class DropZone(
    val id: String,
    val bounds: Bounds,
    val acceptTypes: Set<String> = emptySet(),
    val enabled: Boolean = true,
    val data: Map<String, Any> = emptyMap()
) {
    /**
     * Checks if this zone accepts a component of the given type.
     */
    fun acceptsType(type: String): Boolean {
        return acceptTypes.isEmpty() || acceptTypes.contains(type)
    }
}

/**
 * Represents a bounding rectangle.
 */
data class Bounds(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
) {
    val right: Double get() = x + width
    val bottom: Double get() = y + height
    
    /**
     * Checks if a point is inside this rectangle.
     */
    fun contains(px: Double, py: Double): Boolean {
        return px >= x && px <= right && py >= y && py <= bottom
    }
    
    /**
     * Checks if this rectangle intersects another.
     */
    fun intersects(other: Bounds): Boolean {
        return !(right < other.x || other.right < x ||
                 bottom < other.y || other.bottom < y)
    }
    
    /**
     * Returns the area of this rectangle.
     */
    fun area(): Double = width * height
    
    /**
     * Creates a new Bounds from DOMRect-like values.
     */
    companion object {
        fun fromRect(x: Double, y: Double, width: Double, height: Double) =
            Bounds(x, y, width, height)
    }
}

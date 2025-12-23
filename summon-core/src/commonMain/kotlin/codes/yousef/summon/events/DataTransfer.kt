package codes.yousef.summon.events

/**
 * Holds the data that is being dragged during a drag and drop operation.
 */
class DataTransfer {
    private val data = mutableMapOf<String, String>()
    // In a real implementation, this would wrap the native DataTransfer object
    
    /**
     * Sets the data for the given format.
     */
    fun setData(format: String, value: String) {
        data[format] = value
    }
    
    /**
     * Retrieves the data for the given format.
     */
    fun getData(format: String): String {
        return data[format] ?: ""
    }
    
    /**
     * Removes the data associated with the given format.
     */
    fun clearData(format: String? = null) {
        if (format != null) {
            data.remove(format)
        } else {
            data.clear()
        }
    }
}

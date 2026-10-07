package codes.yousef.summon.effects.jvm

import codes.yousef.summon.effects.ClipboardAPI
import codes.yousef.summon.effects.CompositionScope
import codes.yousef.summon.effects.onMountWithCleanup
import codes.yousef.summon.runtime.Composable
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

/**
 * JVM implementation of the ClipboardAPI interface
 */
class JvmClipboardAPI : ClipboardAPI {
    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    /**
     * Executes the read text operation.
     *
     * @return The resulting value.
     */
    override fun readText(): String {
        return try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                clipboard.getData(DataFlavor.stringFlavor) as String
            } else {
                ""
            }
        } catch (e: Exception) {
            // Handle exceptions (UnsupportedFlavorException, IOException, etc.)
            ""
        }
    }

    /**
     * Executes the write text operation.
     *
     * @param text The text value.
     */
    override fun writeText(text: String) {
        val selection = StringSelection(text)
        clipboard.setContents(selection, selection)
    }

    /**
     * Returns whether this value has text.
     *
     * @return The resulting value.
     */
    override fun hasText(): Boolean {
        return clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)
    }

    /** Clears the operation. */
    override fun clear() {
        clipboard.setContents(StringSelection(""), null)
    }

    // JVM-specific methods
    /**
     * Returns available data flavors.
     *
     * @return The resulting value.
     */
    fun getAvailableDataFlavors(): Array<DataFlavor> {
        return clipboard.availableDataFlavors
    }

    /**
     * Returns data.
     *
     * @param flavor The flavor value.
     * @return The resulting value.
     */
    fun getData(flavor: DataFlavor): Any? {
        return try {
            clipboard.getData(flavor)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Effect for clipboard API (JVM implementation)
 *
 * @return ClipboardAPI object for reading/writing to the clipboard
 */
@Composable
fun CompositionScope.useClipboard(): ClipboardAPI {
    val clipboard = JvmClipboardAPI()

    onMountWithCleanup {
        // Initialize any necessary resources

        // Return cleanup function
        {
            // Clean up any resources
        }
    }

    return clipboard
} 
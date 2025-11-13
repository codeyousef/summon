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

    override fun writeText(text: String) {
        val selection = StringSelection(text)
        clipboard.setContents(selection, selection)
    }

    override fun hasText(): Boolean {
        return clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)
    }

    override fun clear() {
        clipboard.setContents(StringSelection(""), null)
    }

    // JVM-specific methods
    fun getAvailableDataFlavors(): Array<DataFlavor> {
        return clipboard.availableDataFlavors
    }

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
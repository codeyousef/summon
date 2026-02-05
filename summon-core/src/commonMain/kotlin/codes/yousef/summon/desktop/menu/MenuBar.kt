/**
 * # Menu Bar
 *
 * Application menu bar component for desktop-like navigation.
 * Renders as a horizontal navigation bar in web environments.
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.menu

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Represents a keyboard shortcut for a menu item.
 */
data class KeyboardShortcut(
    /** The main key (e.g., "S", "N", "F1") */
    val key: String,
    /** Whether Ctrl/Cmd is required */
    val ctrl: Boolean = false,
    /** Whether Shift is required */
    val shift: Boolean = false,
    /** Whether Alt is required */
    val alt: Boolean = false,
    /** Whether Meta/Win key is required */
    val meta: Boolean = false
) {
    /**
     * Returns a human-readable representation of the shortcut.
     */
    fun toDisplayString(): String {
        val parts = mutableListOf<String>()
        if (ctrl) parts.add("Ctrl")
        if (shift) parts.add("Shift")
        if (alt) parts.add("Alt")
        if (meta) parts.add("Meta")
        parts.add(key)
        return parts.joinToString("+")
    }
}

/**
 * Represents a single menu item.
 */
data class MenuItem(
    /** Display label for the menu item */
    val label: String,
    /** Action to perform when clicked */
    val onClick: (() -> Unit)? = null,
    /** Whether the item is disabled */
    val disabled: Boolean = false,
    /** Keyboard shortcut for this item */
    val shortcut: KeyboardShortcut? = null,
    /** Icon name (optional) */
    val icon: String? = null,
    /** Submenu items (if this is a parent menu) */
    val submenu: List<MenuItem>? = null,
    /** Whether this is a separator line */
    val isSeparator: Boolean = false,
    /** Whether this item is checked (for toggle items) */
    val checked: Boolean? = null
) {
    companion object {
        /**
         * Creates a separator menu item.
         */
        fun separator() = MenuItem(
            label = "",
            isSeparator = true
        )
    }
}

/**
 * Represents a top-level menu in the menu bar.
 */
data class Menu(
    /** Display label for the menu */
    val label: String,
    /** Items in this menu */
    val items: List<MenuItem>,
    /** Whether the menu is disabled */
    val disabled: Boolean = false
)

/**
 * Builder class for creating menus with a DSL.
 */
class MenuBuilder {
    private val items = mutableListOf<MenuItem>()

    /**
     * Adds an item to the menu.
     */
    fun item(
        label: String,
        shortcut: KeyboardShortcut? = null,
        icon: String? = null,
        disabled: Boolean = false,
        checked: Boolean? = null,
        onClick: () -> Unit
    ) {
        items.add(
            MenuItem(
                label = label,
                onClick = onClick,
                shortcut = shortcut,
                icon = icon,
                disabled = disabled,
                checked = checked
            )
        )
    }

    /**
     * Adds a separator to the menu.
     */
    fun separator() {
        items.add(MenuItem.separator())
    }

    /**
     * Adds a submenu.
     */
    fun submenu(label: String, icon: String? = null, builder: MenuBuilder.() -> Unit) {
        val subBuilder = MenuBuilder()
        subBuilder.builder()
        items.add(
            MenuItem(
                label = label,
                icon = icon,
                submenu = subBuilder.build()
            )
        )
    }

    internal fun build(): List<MenuItem> = items.toList()
}

/**
 * Builder class for creating a menu bar with a DSL.
 */
class MenuBarBuilder {
    private val menus = mutableListOf<Menu>()

    /**
     * Adds a menu to the menu bar.
     */
    fun menu(label: String, disabled: Boolean = false, builder: MenuBuilder.() -> Unit) {
        val menuBuilder = MenuBuilder()
        menuBuilder.builder()
        menus.add(
            Menu(
                label = label,
                items = menuBuilder.build(),
                disabled = disabled
            )
        )
    }

    internal fun build(): List<Menu> = menus.toList()
}

/**
 * Creates a list of menus using the menu bar DSL.
 *
 * Example:
 * ```kotlin
 * val menus = menuBar {
 *     menu("File") {
 *         item("New", shortcut = KeyboardShortcut("N", ctrl = true)) { createNew() }
 *         item("Open", shortcut = KeyboardShortcut("O", ctrl = true)) { openFile() }
 *         separator()
 *         item("Exit") { exit() }
 *     }
 *     menu("Edit") {
 *         item("Undo", shortcut = KeyboardShortcut("Z", ctrl = true)) { undo() }
 *         item("Redo", shortcut = KeyboardShortcut("Y", ctrl = true)) { redo() }
 *     }
 * }
 * ```
 */
fun menuBar(builder: MenuBarBuilder.() -> Unit): List<Menu> {
    val menuBarBuilder = MenuBarBuilder()
    menuBarBuilder.builder()
    return menuBarBuilder.build()
}

/**
 * Renders a menu bar as a horizontal navigation component.
 *
 * On web platforms, this renders as a styled nav element with dropdown menus.
 * The menus open on hover or click and close when clicking outside.
 *
 * @param menus List of menus to display
 * @param modifier Styling modifier
 */
@Composable
fun MenuBar(
    menus: List<Menu>,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderMenuBar(menus, modifier)
}

/**
 * Renders a menu bar using the DSL builder.
 *
 * @param modifier Styling modifier
 * @param builder DSL builder for menus
 */
@Composable
fun MenuBar(
    modifier: Modifier = Modifier(),
    builder: MenuBarBuilder.() -> Unit
) {
    MenuBar(menuBar(builder), modifier)
}

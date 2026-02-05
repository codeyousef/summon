/**
 * # System Tray
 *
 * System tray icon functionality for desktop-like applications.
 * This is a no-op on web platforms as browsers don't support system tray icons.
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.tray

import codes.yousef.summon.desktop.menu.MenuItem

/**
 * Represents a system tray icon.
 * On web platforms, this is a no-op stub.
 */
interface TrayIcon {
    /** The tooltip text shown on hover */
    val tooltip: String

    /** The icon image URL or data URI */
    val iconUrl: String?

    /** Updates the tray icon image */
    fun setIcon(url: String)

    /** Updates the tooltip text */
    fun setTooltip(text: String)

    /** Sets the context menu items */
    fun setMenu(items: List<MenuItem>)

    /** Removes the tray icon */
    fun remove()
}

/**
 * Result of showing a notification from the system tray.
 */
sealed class NotificationResult {
    /** Notification was shown successfully */
    object Success : NotificationResult()

    /** Notification permission was denied */
    object PermissionDenied : NotificationResult()

    /** Notifications are not supported on this platform */
    object NotSupported : NotificationResult()

    /** An error occurred while showing the notification */
    data class Error(val message: String) : NotificationResult()
}

/**
 * Creates a system tray icon.
 *
 * On web platforms, this returns null as system tray icons are not supported.
 * However, you can use the Web Notifications API as an alternative for
 * showing notifications to users.
 *
 * @param tooltip The tooltip text shown on hover
 * @param iconUrl The icon image URL (optional)
 * @param onClick Callback when the tray icon is clicked
 * @param menuItems Context menu items shown on right-click
 * @return TrayIcon instance or null if not supported
 */
expect fun createTrayIcon(
    tooltip: String,
    iconUrl: String? = null,
    onClick: (() -> Unit)? = null,
    menuItems: List<MenuItem> = emptyList()
): TrayIcon?

/**
 * Checks if system tray icons are supported on the current platform.
 * Returns false on web platforms.
 */
expect fun isSystemTraySupported(): Boolean

/**
 * Shows a notification using the Web Notifications API.
 * This can serve as an alternative to system tray notifications on web.
 *
 * @param title The notification title
 * @param body The notification body text
 * @param iconUrl The icon URL (optional)
 * @param onClick Callback when the notification is clicked
 * @return NotificationResult indicating success or failure
 */
expect suspend fun showNotification(
    title: String,
    body: String,
    iconUrl: String? = null,
    onClick: (() -> Unit)? = null
): NotificationResult

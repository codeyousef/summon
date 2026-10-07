@file:JvmName("SystemTrayJvm")

package codes.yousef.summon.desktop.tray

import codes.yousef.summon.desktop.menu.MenuItem

/**
 * JVM implementation of SystemTray.
 * Server-side JVM doesn't support system tray operations.
 */

actual fun createTrayIcon(
    tooltip: String,
    iconUrl: String?,
    onClick: (() -> Unit)?,
    menuItems: List<MenuItem>
): TrayIcon? {
    // System tray is not supported on server-side JVM
    println("createTrayIcon is not supported on JVM")
    return null
}

/**
 * Returns whether system tray supported.
 *
 * @return The resulting value.
 */
actual fun isSystemTraySupported(): Boolean = false

/**
 * Executes the show notification operation.
 *
 * @param title The title value.
 * @param body The body value.
 * @param iconUrl The icon url value.
 * @param onClick Callback invoked when click.
 * @return The resulting value.
 */
actual suspend fun showNotification(
    title: String,
    body: String,
    iconUrl: String?,
    onClick: (() -> Unit)?
): NotificationResult {
    // Notifications are not supported on server-side JVM
    println("showNotification is not supported on JVM: $title - $body")
    return NotificationResult.NotSupported
}

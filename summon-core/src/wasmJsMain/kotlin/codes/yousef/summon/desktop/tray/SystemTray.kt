package codes.yousef.summon.desktop.tray

import codes.yousef.summon.desktop.menu.MenuItem

/**
 * WASM stub implementation of TrayIcon.
 * System tray is not supported in web browsers.
 */
private class WasmTrayIcon(
    override val tooltip: String,
    override val iconUrl: String?
) : TrayIcon {
    override fun setIcon(url: String) {
        // No-op on web
    }

    override fun setTooltip(text: String) {
        // No-op on web
    }

    override fun setMenu(items: List<MenuItem>) {
        // No-op on web
    }

    override fun remove() {
        // No-op on web
    }
}

actual fun createTrayIcon(
    tooltip: String,
    iconUrl: String?,
    onClick: (() -> Unit)?,
    menuItems: List<MenuItem>
): TrayIcon? {
    // System tray is not supported in web browsers
    return null
}

actual fun isSystemTraySupported(): Boolean = false

/**
 * WASM implementation - Web Notifications API support is limited.
 */
actual suspend fun showNotification(
    title: String,
    body: String,
    iconUrl: String?,
    onClick: (() -> Unit)?
): NotificationResult {
    // Web Notifications API requires complex JS interop in WASM
    // Return not supported for now
    return NotificationResult.NotSupported
}

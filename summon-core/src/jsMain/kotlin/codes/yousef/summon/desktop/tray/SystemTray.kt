package codes.yousef.summon.desktop.tray

import codes.yousef.summon.desktop.menu.MenuItem
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * JS stub implementation of TrayIcon.
 * System tray is not supported in web browsers.
 */
private class JsTrayIcon(
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
 * JS implementation using the Web Notifications API.
 */
actual suspend fun showNotification(
    title: String,
    body: String,
    iconUrl: String?,
    onClick: (() -> Unit)?
): NotificationResult {
    // Check if Notifications API is available
    if (js("typeof Notification === 'undefined'") as Boolean) {
        return NotificationResult.NotSupported
    }

    return suspendCancellableCoroutine { cont ->
        try {
            // Check current permission
            val permission = js("Notification.permission") as String

            when (permission) {
                "granted" -> {
                    // Permission already granted, show notification
                    showNotificationInternal(title, body, iconUrl, onClick)
                    cont.resume(NotificationResult.Success)
                }

                "denied" -> {
                    cont.resume(NotificationResult.PermissionDenied)
                }

                else -> {
                    // Request permission
                    val promise = js("Notification.requestPermission()")
                    promise.then { result: String ->
                        if (result == "granted") {
                            showNotificationInternal(title, body, iconUrl, onClick)
                            cont.resume(NotificationResult.Success)
                        } else {
                            cont.resume(NotificationResult.PermissionDenied)
                        }
                    }.catch { error: dynamic ->
                        cont.resume(NotificationResult.Error(error.toString()))
                    }
                }
            }
        } catch (e: Exception) {
            cont.resume(NotificationResult.Error(e.message ?: "Unknown error"))
        }
    }
}

private fun showNotificationInternal(
    title: String,
    body: String,
    iconUrl: String?,
    onClick: (() -> Unit)?
) {
    val options = js("{}")
    options["body"] = body
    if (iconUrl != null) {
        options["icon"] = iconUrl
    }

    val notification = js("new Notification(title, options)")

    if (onClick != null) {
        notification.onclick = {
            onClick()
            notification.close()
        }
    }
}

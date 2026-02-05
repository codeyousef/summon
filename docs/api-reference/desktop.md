# Desktop Features API Reference

Multi-window and desktop-like functionality for web applications built with Summon.

**Package**: `codes.yousef.summon.desktop`
**Since**: 0.7.0

---

## Overview

The Desktop features package provides APIs for building rich, desktop-like web applications:

- **WindowManager** - Open and control browser windows/tabs
- **BroadcastChannel** - Cross-tab real-time messaging
- **SyncedStorage** - Cross-tab reactive state persistence
- **FileDialog** - Native file picker dialogs
- **MenuBar** - Application menu bar component
- **SystemTray** - Web notifications (tray icon stub)
- **DragCoordinator** - Cross-window drag and drop
- **PictureInPicture** - Floating PiP windows

---

## Window Management

### WindowManager

Manages browser windows and tabs programmatically.

**Package**: `codes.yousef.summon.desktop.window`

```kotlin
object WindowManager {
    /** Unique ID for the current window/tab */
    val currentWindowId: String?

    /** Opens a new window/tab */
    fun open(url: String, target: String = "_blank", options: WindowOptions = WindowOptions()): WindowReference?

    /** Gets screen dimensions */
    fun getScreenInfo(): ScreenInfo

    /** Gets current window bounds */
    fun getCurrentWindowBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>>

    /** Moves the window */
    fun moveTo(x: Int, y: Int)

    /** Resizes the window */
    fun resizeTo(width: Int, height: Int)

    /** Focuses the window */
    fun focus()

    /** Checks if popups are blocked */
    fun arePopupsLikelyBlocked(): Boolean
}
```

### WindowOptions

```kotlin
data class WindowOptions(
    val width: Int? = null,
    val height: Int? = null,
    val left: Int? = null,
    val top: Int? = null,
    val title: String? = null,
    val menubar: Boolean = false,
    val toolbar: Boolean = false,
    val location: Boolean = false,
    val status: Boolean = false,
    val resizable: Boolean = true,
    val scrollbars: Boolean = true
)
```

### WindowReference

```kotlin
interface WindowReference {
    fun close()
    fun focus()
    fun isClosed(): Boolean
    fun getLocation(): String?
    fun navigate(url: String)
    fun postMessage(message: String, targetOrigin: String = "*")
}
```

### ScreenInfo

```kotlin
data class ScreenInfo(
    val width: Int,
    val height: Int,
    val availWidth: Int,
    val availHeight: Int,
    val colorDepth: Int,
    val pixelDepth: Int,
    val devicePixelRatio: Double
)
```

**Example:**

```kotlin
// Open a new window
val window = WindowManager.open(
    url = "/settings",
    options = WindowOptions(
        width = 800,
        height = 600,
        title = "Settings"
    )
)

// Close it later
window?.close()

// Get screen info
val screen = WindowManager.getScreenInfo()
println("Screen: ${screen.width}x${screen.height}")
println("DPI: ${screen.devicePixelRatio}")

// Use window ID for coordination
val myId = WindowManager.currentWindowId
println("This window: $myId")
```

### Platform Support

| Feature         | JS             | WASM           | JVM            |
|-----------------|----------------|----------------|----------------|
| open()          | window.open()  | window.open()  | Returns null   |
| close()         | window.close() | window.close() | No-op          |
| focus()         | window.focus() | window.focus() | No-op          |
| Screen info     | screen API     | screen API     | Default values |
| currentWindowId | sessionStorage | sessionStorage | Generated UUID |

---

## Cross-Tab Communication

### BroadcastChannel

Real-time messaging between browser tabs/windows sharing the same origin.

**Package**: `codes.yousef.summon.desktop.communication`

```kotlin
interface SummonBroadcastChannel<T> {
    val name: String
    fun postMessage(message: T)
    fun onMessage(handler: (T) -> Unit): () -> Unit
    fun close()
    fun isOpen(): Boolean
}

/** Create a string message channel */
fun createBroadcastChannel(name: String): SummonBroadcastChannel<String>

/** Create a typed channel with serialization */
fun <T> createTypedBroadcastChannel(
    name: String,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SummonBroadcastChannel<T>
```

**Example:**

```kotlin
// Basic usage
val channel = createBroadcastChannel("my-channel")

// Listen for messages from other tabs
val unsubscribe = channel.onMessage { message ->
    println("Received: $message")
}

// Send a message to all other tabs
channel.postMessage("Hello from this tab!")

// Clean up
unsubscribe()
channel.close()

// Typed messages with serialization
@Serializable
data class UserAction(val type: String, val userId: String)

val typedChannel = createTypedBroadcastChannel<UserAction>(
    name = "user-actions",
    serializer = { Json.encodeToString(it) },
    deserializer = { Json.decodeFromString(it) }
)

typedChannel.onMessage { action ->
    when (action.type) {
        "login" -> handleLogin(action.userId)
        "logout" -> handleLogout(action.userId)
    }
}

typedChannel.postMessage(UserAction("login", "user123"))
```

### Platform Support

| Platform | Implementation                          |
|----------|-----------------------------------------|
| JS       | BroadcastChannel API                    |
| WASM     | BroadcastChannel API                    |
| JVM      | In-process event bus (no cross-process) |

---

## Synced Storage

### SyncedStorage

Cross-tab reactive storage using localStorage and storage events.

**Package**: `codes.yousef.summon.desktop.storage`

```kotlin
interface SyncedStorage<T> {
    var value: T
    fun clear()
    fun exists(): Boolean
    fun addChangeListener(listener: (T) -> Unit): () -> Unit
}

/** Create synced storage */
fun <T> createSyncedStorage(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SyncedStorage<T>

/** Composable synced storage */
@Composable
fun <T> rememberSynced(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SyncedStorage<T>
```

### Convenience Functions

```kotlin
// Factory functions
fun createSyncedStringStorage(key: String, defaultValue: String = ""): SyncedStorage<String>
fun createSyncedIntStorage(key: String, defaultValue: Int = 0): SyncedStorage<Int>
fun createSyncedBooleanStorage(key: String, defaultValue: Boolean = false): SyncedStorage<Boolean>
fun createSyncedDoubleStorage(key: String, defaultValue: Double = 0.0): SyncedStorage<Double>
fun createSyncedLongStorage(key: String, defaultValue: Long = 0L): SyncedStorage<Long>

// Composable convenience
@Composable fun rememberSyncedString(key: String, defaultValue: String = ""): SyncedStorage<String>
@Composable fun rememberSyncedInt(key: String, defaultValue: Int = 0): SyncedStorage<Int>
@Composable fun rememberSyncedBoolean(key: String, defaultValue: Boolean = false): SyncedStorage<Boolean>
```

**Example:**

```kotlin
// Outside composables
val userPrefs = createSyncedStorage(
    key = "user-theme",
    defaultValue = "light",
    serializer = { it },
    deserializer = { it }
)

val theme = userPrefs.value  // Read
userPrefs.value = "dark"     // Write (syncs to other tabs)

// In composables (triggers recomposition on changes)
@Composable
fun ThemeSwitcher() {
    val theme = rememberSyncedString(key = "user-theme", defaultValue = "light")

    Button(onClick = {
        theme.value = if (theme.value == "light") "dark" else "light"
    }) {
        Text("Toggle Theme: ${theme.value}")
    }
}

// Complex objects
@Serializable
data class UserSettings(val theme: String, val language: String)

val settings = createSyncedStorage(
    key = "user-settings",
    defaultValue = UserSettings("light", "en"),
    serializer = { Json.encodeToString(it) },
    deserializer = { Json.decodeFromString(it) }
)
```

### Platform Support

| Platform | Implementation                         |
|----------|----------------------------------------|
| JS       | localStorage + storage event           |
| WASM     | localStorage + storage event           |
| JVM      | In-memory only (no cross-process sync) |

---

## File Dialogs

### File System Access API

Native file picker dialogs using the File System Access API.

**Package**: `codes.yousef.summon.desktop.dialog`

```kotlin
/** Open file dialog */
suspend fun showOpenFileDialog(options: FileDialogOptions = FileDialogOptions()): List<FileInfo>?

/** Save file dialog */
suspend fun showSaveFileDialog(options: SaveDialogOptions = SaveDialogOptions()): SaveDialogResult?

/** Directory picker */
suspend fun showDirectoryPicker(title: String? = null, startIn: String? = null): DirectoryHandle?

/** Feature detection */
fun isFileSystemAccessSupported(): Boolean
```

### Options

```kotlin
data class FileDialogOptions(
    val types: List<FileTypeFilter> = emptyList(),
    val multiple: Boolean = false,
    val title: String? = null,
    val startIn: String? = null
)

data class SaveDialogOptions(
    val suggestedName: String? = null,
    val types: List<FileTypeFilter> = emptyList(),
    val title: String? = null,
    val startIn: String? = null
)

data class FileTypeFilter(
    val description: String,
    val accept: Map<String, List<String>>  // MIME type to extensions
)
```

**Example:**

```kotlin
// Open file picker
val files = showOpenFileDialog(FileDialogOptions(
    types = listOf(
        FileTypeFilter(
            description = "Text files",
            accept = mapOf("text/plain" to listOf(".txt", ".md"))
        )
    ),
    multiple = true
))

files?.forEach { file ->
    println("Selected: ${file.name} (${file.size} bytes)")
}

// Save file dialog
val result = showSaveFileDialog(SaveDialogOptions(
    suggestedName = "document.txt",
    types = listOf(
        FileTypeFilter("Text files", mapOf("text/plain" to listOf(".txt")))
    )
))

result?.write("Hello, World!")

// Directory picker
val dir = showDirectoryPicker()
dir?.let {
    println("Selected directory: ${it.name}")
    val files = it.listFiles()
    println("Contains ${files.size} items")
}

// Feature detection
if (isFileSystemAccessSupported()) {
    // Use native dialogs
} else {
    // Fall back to input[type=file]
}
```

---

## Menu Bar

### MenuBar Component

Application menu bar with dropdown menus and keyboard shortcuts.

**Package**: `codes.yousef.summon.desktop.menu`

```kotlin
@Composable
fun MenuBar(menus: List<Menu>, modifier: Modifier = Modifier())

@Composable
fun MenuBar(modifier: Modifier = Modifier(), builder: MenuBarBuilder.() -> Unit)
```

### Data Classes

```kotlin
data class KeyboardShortcut(
    val key: String,
    val ctrl: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val meta: Boolean = false
) {
    fun toDisplayString(): String  // e.g., "Ctrl+S"
}

data class MenuItem(
    val label: String,
    val onClick: (() -> Unit)? = null,
    val disabled: Boolean = false,
    val shortcut: KeyboardShortcut? = null,
    val icon: String? = null,
    val submenu: List<MenuItem>? = null,
    val isSeparator: Boolean = false,
    val checked: Boolean? = null
) {
    companion object {
        fun separator(): MenuItem
    }
}

data class Menu(
    val label: String,
    val items: List<MenuItem>,
    val disabled: Boolean = false
)
```

### DSL Builder

```kotlin
fun menuBar(builder: MenuBarBuilder.() -> Unit): List<Menu>

class MenuBarBuilder {
    fun menu(label: String, disabled: Boolean = false, builder: MenuBuilder.() -> Unit)
}

class MenuBuilder {
    fun item(
        label: String,
        shortcut: KeyboardShortcut? = null,
        icon: String? = null,
        disabled: Boolean = false,
        checked: Boolean? = null,
        onClick: () -> Unit
    )
    fun separator()
    fun submenu(label: String, icon: String? = null, builder: MenuBuilder.() -> Unit)
}
```

**Example:**

```kotlin
MenuBar {
    menu("File") {
        item("New", shortcut = KeyboardShortcut("N", ctrl = true)) { createNew() }
        item("Open", shortcut = KeyboardShortcut("O", ctrl = true)) { openFile() }
        item("Save", shortcut = KeyboardShortcut("S", ctrl = true)) { save() }
        separator()
        submenu("Recent Files") {
            item("document1.txt") { openRecent("document1.txt") }
            item("document2.txt") { openRecent("document2.txt") }
        }
        separator()
        item("Exit") { exit() }
    }
    menu("Edit") {
        item("Undo", shortcut = KeyboardShortcut("Z", ctrl = true)) { undo() }
        item("Redo", shortcut = KeyboardShortcut("Y", ctrl = true)) { redo() }
        separator()
        item("Cut", shortcut = KeyboardShortcut("X", ctrl = true)) { cut() }
        item("Copy", shortcut = KeyboardShortcut("C", ctrl = true)) { copy() }
        item("Paste", shortcut = KeyboardShortcut("V", ctrl = true)) { paste() }
    }
    menu("View") {
        item("Dark Mode", checked = isDarkMode) { toggleDarkMode() }
        item("Full Screen", shortcut = KeyboardShortcut("F11")) { toggleFullScreen() }
    }
}
```

---

## System Tray / Notifications

### System Tray (Web Stub)

System tray icons are not supported in browsers. The API provides stubs and falls back to Web Notifications.

**Package**: `codes.yousef.summon.desktop.tray`

```kotlin
interface TrayIcon {
    val tooltip: String
    val iconUrl: String?
    fun setIcon(url: String)
    fun setTooltip(text: String)
    fun setMenu(items: List<MenuItem>)
    fun remove()
}

/** Always returns null on web platforms */
fun createTrayIcon(
    tooltip: String,
    iconUrl: String? = null,
    onClick: (() -> Unit)? = null,
    menuItems: List<MenuItem> = emptyList()
): TrayIcon?

fun isSystemTraySupported(): Boolean  // false on web

/** Web Notifications API fallback */
suspend fun showNotification(
    title: String,
    body: String,
    iconUrl: String? = null,
    onClick: (() -> Unit)? = null
): NotificationResult
```

### NotificationResult

```kotlin
sealed class NotificationResult {
    object Success : NotificationResult()
    object PermissionDenied : NotificationResult()
    object NotSupported : NotificationResult()
    data class Error(val message: String) : NotificationResult()
}
```

**Example:**

```kotlin
// Check if tray is supported (false on web)
if (isSystemTraySupported()) {
    val tray = createTrayIcon(
        tooltip = "My App",
        iconUrl = "/icon.png",
        onClick = { showWindow() }
    )
} else {
    // Use web notifications instead
    when (val result = showNotification(
        title = "My App",
        body = "You have a new message!",
        iconUrl = "/icon.png",
        onClick = { focusWindow() }
    )) {
        is NotificationResult.Success -> println("Notification shown")
        is NotificationResult.PermissionDenied -> println("Permission denied")
        is NotificationResult.NotSupported -> println("Notifications not supported")
        is NotificationResult.Error -> println("Error: ${result.message}")
    }
}
```

---

## Cross-Window Drag and Drop

### DragCoordinator

Coordinates drag and drop operations between browser windows using BroadcastChannel.

**Package**: `codes.yousef.summon.desktop.communication`

```kotlin
@Serializable
data class DragData(
    val dragId: String,
    val dataType: String,
    val payload: String,
    val sourceWindow: String,
    val metadata: Map<String, String> = emptyMap()
)

interface DragEventListener {
    fun onDragStart(data: DragData)
    fun onDragMove(dragId: String, x: Double, y: Double)
    fun onDragEnd(dragId: String, cancelled: Boolean)
    fun onDropAccepted(dragId: String, targetWindow: String)
}

expect class DragCoordinator(channelName: String) {
    fun startDrag(data: DragData)
    fun updateDragPosition(dragId: String, x: Double, y: Double)
    fun endDrag(dragId: String, cancelled: Boolean = false)
    fun acceptDrop(dragId: String)
    fun addListener(listener: DragEventListener)
    fun removeListener(listener: DragEventListener)
    fun close()
}
```

**Example:**

```kotlin
val coordinator = DragCoordinator("my-app-dnd")

// Listen for drags from other windows
coordinator.addListener(object : DragEventListener {
    override fun onDragStart(data: DragData) {
        // Show drop indicator
        showDropZone = true
        currentDragData = data
    }

    override fun onDragMove(dragId: String, x: Double, y: Double) {
        // Update visual feedback
    }

    override fun onDragEnd(dragId: String, cancelled: Boolean) {
        // Hide drop indicator
        showDropZone = false
        currentDragData = null
    }

    override fun onDropAccepted(dragId: String, targetWindow: String) {
        // Handle successful drop in another window
        if (targetWindow != WindowManager.currentWindowId) {
            removeItem(dragId)
        }
    }
})

// Start a drag operation
fun onDragStart(item: Item) {
    coordinator.startDrag(DragData(
        dragId = item.id,
        dataType = "item",
        payload = Json.encodeToString(item),
        sourceWindow = WindowManager.currentWindowId ?: ""
    ))
}

// Accept a drop
fun onDrop() {
    currentDragData?.let { data ->
        val item = Json.decodeFromString<Item>(data.payload)
        addItem(item)
        coordinator.acceptDrop(data.dragId)
    }
}

// Clean up
coordinator.close()
```

---

## Picture-in-Picture

### Document PiP API

Floating windows that stay on top for video players, mini controls, etc.

**Package**: `codes.yousef.summon.desktop.pip`

```kotlin
data class PipOptions(
    val width: Int = 300,
    val height: Int = 200,
    val copyStyleSheets: Boolean = true
)

interface PipWindow {
    val width: Int
    val height: Int
    val isOpen: Boolean
    fun close()
    fun focus()
}

sealed class PipResult {
    data class Success(val window: PipWindow) : PipResult()
    object NotSupported : PipResult()
    object UserDenied : PipResult()
    data class Error(val message: String) : PipResult()
}

fun isPictureInPictureSupported(): Boolean

suspend fun requestPictureInPicture(options: PipOptions = PipOptions()): PipResult

@Composable
expect fun PictureInPictureContent(
    window: PipWindow,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)
```

**Example:**

```kotlin
var pipWindow by remember { mutableStateOf<PipWindow?>(null) }

// Request PiP (must be triggered by user gesture)
Button(
    onClick = {
        scope.launch {
            when (val result = requestPictureInPicture(
                PipOptions(width = 400, height = 300)
            )) {
                is PipResult.Success -> pipWindow = result.window
                is PipResult.NotSupported -> showToast("PiP not supported")
                is PipResult.UserDenied -> showToast("PiP request denied")
                is PipResult.Error -> showToast("Error: ${result.message}")
            }
        }
    },
    label = "Open Mini Player"
)

// Render content in PiP window
pipWindow?.let { window ->
    PictureInPictureContent(window) {
        Column {
            VideoPlayer(src = currentVideo)
            Row {
                Button(onClick = { previous() }) { Icon("skip-back") }
                Button(onClick = { togglePlay() }) { Icon(if (playing) "pause" else "play") }
                Button(onClick = { next() }) { Icon("skip-forward") }
            }
        }
    }
}

// Close PiP
fun closeMiniPlayer() {
    pipWindow?.close()
    pipWindow = null
}
```

---

## Browser Support

| Feature            | Chrome | Firefox | Safari | Edge |
|--------------------|--------|---------|--------|------|
| WindowManager      | Full   | Full    | Full   | Full |
| BroadcastChannel   | 54+    | 38+     | 15.4+  | 79+  |
| SyncedStorage      | Full   | Full    | Full   | Full |
| File System Access | 86+    | No      | No     | 86+  |
| Document PiP       | 111+   | No      | No     | 111+ |
| Web Notifications  | 22+    | 22+     | 7+     | Full |

---

## See Also

- [Components API](components.md) - UI components
- [State API](state.md) - State management
- [Events API](events.md) - Event handling
- [Modifier API](modifier.md) - Styling

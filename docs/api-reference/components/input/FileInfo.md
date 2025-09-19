# FileInfo

FileInfo represents metadata about files selected by users, providing a cross-platform abstraction for file information
and operations.

## Overview

The FileInfo class encapsulates essential file metadata including name, size, and MIME type. It provides a
platform-agnostic interface for working with user-selected files across browser and JVM environments.

### Key Features

- **Cross-platform**: Consistent API across JS and JVM platforms
- **File Metadata**: Access to name, size, and MIME type
- **Destructuring Support**: Component functions for easy unpacking
- **Type Safety**: Strongly typed file information
- **Platform Optimized**: Platform-specific implementations for best performance

## API Reference

### FileInfo

```kotlin
expect class FileInfo {
    val name: String    // The name of the file
    val size: Long      // The size of the file in bytes
    val type: String    // The MIME type of the file

    // Destructuring support
    operator fun component1(): String  // name
    operator fun component2(): Long    // size
    operator fun component3(): String  // type
}
```

**Properties:**

- `name`: The original filename including extension
- `size`: File size in bytes
- `type`: MIME type (e.g., "image/jpeg", "application/pdf")

**Component Functions:**

- `component1()`: Returns the file name
- `component2()`: Returns the file size
- `component3()`: Returns the MIME type

## Usage Examples

### Basic File Information Display

```kotlin
@Composable
fun FileInfoDisplayExample() {
    var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        FileUpload(
            onFilesSelected = { selectedFiles = it },
            multiple = true,
            buttonLabel = "Select Files"
        )

        if (selectedFiles.isNotEmpty()) {
            Text("Selected Files:", style = Typography.H6)

            selectedFiles.forEach { file ->
                FileInfoCard(file = file)
            }
        }
    }
}

@Composable
fun FileInfoCard(file: FileInfo) {
    Card(
        modifier = Modifier()
            .width(400.px)
            .border(Border.solid(1.px, Colors.Gray.LIGHT))
            .borderRadius(BorderRadius.MD)
            .padding(Spacing.MD)
    ) {
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Text(
                file.name,
                style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM)
            )

            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
            ) {
                Text(
                    "Size: ${formatFileSize(file.size)}",
                    style = Typography.BODY2
                )
                Text(
                    "Type: ${file.type}",
                    style = Typography.BODY2.copy(color = Colors.Gray.MAIN)
                )
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
```

### File Type Categorization

```kotlin
@Composable
fun FileCategoryExample() {
    var uploadedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    val categorizedFiles = remember(uploadedFiles) {
        uploadedFiles.groupBy { getFileCategory(it) }
    }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        FileUpload(
            onFilesSelected = { uploadedFiles = it },
            multiple = true,
            buttonLabel = "Upload Files"
        )

        if (categorizedFiles.isNotEmpty()) {
            categorizedFiles.forEach { (category, files) ->
                FileCategorySection(category = category, files = files)
            }
        }
    }
}

@Composable
fun FileCategorySection(category: FileCategory, files: List<FileInfo>) {
    Card(
        modifier = Modifier()
            .width(Width.FULL)
            .backgroundColor(category.backgroundColor)
            .borderRadius(BorderRadius.MD)
            .padding(Spacing.MD)
    ) {
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(
                modifier = Modifier()
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.SM)
            ) {
                Icon(name = category.icon, color = category.iconColor)
                Text(
                    "${category.displayName} (${files.size})",
                    style = Typography.H6.copy(color = category.textColor)
                )
            }

            files.forEach { file ->
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                        .padding(Spacing.XS)
                ) {
                    Text(file.name, style = Typography.BODY2)
                    Text(formatFileSize(file.size), style = Typography.CAPTION)
                }
            }
        }
    }
}

enum class FileCategory(
    val displayName: String,
    val icon: String,
    val backgroundColor: Color,
    val iconColor: Color,
    val textColor: Color
) {
    IMAGE("Images", "image", Colors.Success.LIGHT, Colors.Success.MAIN, Colors.Success.DARK),
    DOCUMENT("Documents", "description", Colors.Info.LIGHT, Colors.Info.MAIN, Colors.Info.DARK),
    VIDEO("Videos", "videocam", Colors.Warning.LIGHT, Colors.Warning.MAIN, Colors.Warning.DARK),
    AUDIO("Audio", "audiotrack", Colors.Primary.LIGHT, Colors.Primary.MAIN, Colors.Primary.DARK),
    ARCHIVE("Archives", "archive", Colors.Secondary.LIGHT, Colors.Secondary.MAIN, Colors.Secondary.DARK),
    OTHER("Other", "attach_file", Colors.Gray.LIGHT, Colors.Gray.MAIN, Colors.Gray.DARK)
}

private fun getFileCategory(file: FileInfo): FileCategory {
    return when {
        file.type.startsWith("image/") -> FileCategory.IMAGE
        file.type.startsWith("video/") -> FileCategory.VIDEO
        file.type.startsWith("audio/") -> FileCategory.AUDIO
        file.type in listOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        ) -> FileCategory.DOCUMENT
        file.type in listOf(
            "application/zip",
            "application/x-rar-compressed",
            "application/x-tar"
        ) -> FileCategory.ARCHIVE
        else -> FileCategory.OTHER
    }
}
```

### File Destructuring

```kotlin
@Composable
fun FileDestructuringExample() {
    var selectedFile by remember { mutableStateOf<FileInfo?>(null) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        FileUpload(
            onFilesSelected = { files ->
                selectedFile = files.firstOrNull()
            },
            buttonLabel = "Select Single File"
        )

        selectedFile?.let { file ->
            // Destructuring the FileInfo
            val (name, size, type) = file

            Card(
                modifier = Modifier()
                    .width(350.px)
                    .backgroundColor(Colors.Info.LIGHT)
                    .padding(Spacing.MD)
            ) {
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("File Details", style = Typography.H6)

                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                    ) {
                        Text("Name:", style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM))
                        Text(name, style = Typography.BODY2)
                    }

                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                    ) {
                        Text("Size:", style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM))
                        Text(formatFileSize(size), style = Typography.BODY2)
                    }

                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                    ) {
                        Text("Type:", style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM))
                        Text(type, style = Typography.BODY2)
                    }
                }
            }
        }
    }
}
```

### File Validation

```kotlin
@Composable
fun FileValidationExample() {
    var uploadedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }
    val validationResults = remember(uploadedFiles) {
        uploadedFiles.map { file -> file to validateFile(file) }
    }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("File Upload with Validation", style = Typography.H6)

        FileUpload(
            onFilesSelected = { uploadedFiles = it },
            multiple = true,
            buttonLabel = "Upload Files"
        )

        if (validationResults.isNotEmpty()) {
            validationResults.forEach { (file, validation) ->
                FileValidationCard(file = file, validation = validation)
            }

            val validFiles = validationResults.filter { it.second.isValid }.map { it.first }
            val invalidFiles = validationResults.filter { !it.second.isValid }.map { it.first }

            if (validFiles.isNotEmpty()) {
                Text(
                    "${validFiles.size} valid file${if (validFiles.size != 1) "s" else ""} ready for upload",
                    style = Typography.BODY2.copy(color = Colors.Success.MAIN)
                )
            }

            if (invalidFiles.isNotEmpty()) {
                Text(
                    "${invalidFiles.size} file${if (invalidFiles.size != 1) "s" else ""} failed validation",
                    style = Typography.BODY2.copy(color = Colors.Error.MAIN)
                )
            }
        }
    }
}

@Composable
fun FileValidationCard(file: FileInfo, validation: FileValidationResult) {
    Card(
        modifier = Modifier()
            .width(500.px)
            .border(
                Border.solid(
                    1.px,
                    if (validation.isValid) Colors.Success.MAIN else Colors.Error.MAIN
                )
            )
            .borderRadius(BorderRadius.MD)
            .padding(Spacing.MD)
    ) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Column(modifier = Modifier().weight(1f)) {
                Text(file.name, style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM))
                Text(
                    "${formatFileSize(file.size)} • ${file.type}",
                    style = Typography.CAPTION
                )

                if (!validation.isValid) {
                    validation.errors.forEach { error ->
                        Text(
                            "• $error",
                            style = Typography.CAPTION.copy(color = Colors.Error.MAIN)
                        )
                    }
                }
            }

            Icon(
                name = if (validation.isValid) "check_circle" else "error",
                color = if (validation.isValid) Colors.Success.MAIN else Colors.Error.MAIN
            )
        }
    }
}

data class FileValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

private fun validateFile(file: FileInfo): FileValidationResult {
    val errors = mutableListOf<String>()

    // Size validation (10 MB limit)
    if (file.size > 10 * 1024 * 1024) {
        errors.add("File size exceeds 10 MB limit")
    }

    // Type validation
    val allowedTypes = listOf(
        "image/jpeg", "image/png", "image/gif",
        "application/pdf", "text/plain",
        "application/msword"
    )
    if (file.type !in allowedTypes) {
        errors.add("File type not supported")
    }

    // Name validation
    if (file.name.isBlank()) {
        errors.add("Invalid file name")
    }

    return FileValidationResult(
        isValid = errors.isEmpty(),
        errors = errors
    )
}
```

### File List with Actions

```kotlin
@Composable
fun FileListWithActionsExample() {
    var files by remember { mutableStateOf<List<FileInfo>>(emptyList()) }
    var selectedFiles by remember { mutableStateOf<Set<String>>(emptySet()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("File Manager", style = Typography.H6)

            FileUpload(
                onFilesSelected = { newFiles ->
                    files = files + newFiles
                },
                multiple = true,
                buttonLabel = "Add Files"
            )
        }

        if (files.isNotEmpty()) {
            // Bulk actions
            if (selectedFiles.isNotEmpty()) {
                Row(modifier = Modifier().gap(Spacing.SM)) {
                    Button(
                        text = "Delete Selected (${selectedFiles.size})",
                        type = ButtonType.OUTLINE,
                        onClick = {
                            files = files.filter { it.name !in selectedFiles }
                            selectedFiles = emptySet()
                        }
                    )

                    Button(
                        text = "Clear Selection",
                        type = ButtonType.SECONDARY,
                        onClick = { selectedFiles = emptySet() }
                    )
                }
            }

            // File list
            Column(modifier = Modifier().gap(Spacing.SM)) {
                files.forEach { file ->
                    FileItemWithActions(
                        file = file,
                        isSelected = file.name in selectedFiles,
                        onSelectionChange = { isSelected ->
                            selectedFiles = if (isSelected) {
                                selectedFiles + file.name
                            } else {
                                selectedFiles - file.name
                            }
                        },
                        onDelete = {
                            files = files.filter { it.name != file.name }
                            selectedFiles = selectedFiles - file.name
                        }
                    )
                }
            }

            // Summary
            Text(
                "Total: ${files.size} files (${formatFileSize(files.sumOf { it.size })})",
                style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
            )
        }
    }
}

@Composable
fun FileItemWithActions(
    file: FileInfo,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier()
            .width(Width.FULL)
            .border(
                Border.solid(
                    1.px,
                    if (isSelected) Colors.Primary.MAIN else Colors.Gray.LIGHT
                )
            )
            .borderRadius(BorderRadius.MD)
            .padding(Spacing.MD)
            .backgroundColor(
                if (isSelected) Colors.Primary.LIGHT else Colors.White
            )
    ) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Row(
                modifier = Modifier()
                    .weight(1f)
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.MD)
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChange
                )

                Column {
                    Text(file.name, style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM))
                    Text(
                        "${formatFileSize(file.size)} • ${file.type}",
                        style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
                    )
                }
            }

            Row(modifier = Modifier().gap(Spacing.SM)) {
                Button(
                    text = "View",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    onClick = { println("Viewing ${file.name}") }
                )

                Button(
                    text = "Delete",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    onClick = onDelete
                )
            }
        }
    }
}
```

### File Type Icons

```kotlin
@Composable
fun FileIconExample() {
    var uploadedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        FileUpload(
            onFilesSelected = { uploadedFiles = it },
            multiple = true,
            buttonLabel = "Upload Files"
        )

        if (uploadedFiles.isNotEmpty()) {
            LazyGrid(
                columns = 3,
                modifier = Modifier().gap(Spacing.MD)
            ) {
                items(uploadedFiles) { file ->
                    FileIconCard(file = file)
                }
            }
        }
    }
}

@Composable
fun FileIconCard(file: FileInfo) {
    Card(
        modifier = Modifier()
            .width(120.px)
            .height(140.px)
            .border(Border.solid(1.px, Colors.Gray.LIGHT))
            .borderRadius(BorderRadius.MD)
            .padding(Spacing.SM)
    ) {
        Column(
            modifier = Modifier()
                .width(Width.FULL)
                .height(Width.FULL)
                .alignItems(AlignItems.Center)
                .justifyContent(JustifyContent.Center)
                .gap(Spacing.SM)
        ) {
            Icon(
                name = getFileIcon(file),
                color = getFileIconColor(file),
                modifier = Modifier().fontSize(48.px)
            )

            Text(
                file.name,
                style = Typography.CAPTION,
                modifier = Modifier()
                    .textAlign("center")
                    .maxLines(2)
                    .overflow("hidden")
            )

            Text(
                formatFileSize(file.size),
                style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
            )
        }
    }
}

private fun getFileIcon(file: FileInfo): String {
    return when {
        file.type.startsWith("image/") -> "image"
        file.type.startsWith("video/") -> "videocam"
        file.type.startsWith("audio/") -> "audiotrack"
        file.type == "application/pdf" -> "picture_as_pdf"
        file.type.contains("word") -> "description"
        file.type.contains("excel") || file.type.contains("spreadsheet") -> "table_chart"
        file.type.contains("powerpoint") || file.type.contains("presentation") -> "slideshow"
        file.type.contains("zip") || file.type.contains("rar") -> "archive"
        file.type.startsWith("text/") -> "text_snippet"
        else -> "attach_file"
    }
}

private fun getFileIconColor(file: FileInfo): Color {
    return when {
        file.type.startsWith("image/") -> Colors.Success.MAIN
        file.type.startsWith("video/") -> Colors.Warning.MAIN
        file.type.startsWith("audio/") -> Colors.Primary.MAIN
        file.type == "application/pdf" -> Colors.Error.MAIN
        file.type.contains("word") -> Colors.Info.MAIN
        file.type.contains("excel") || file.type.contains("spreadsheet") -> Colors.Success.MAIN
        file.type.contains("powerpoint") || file.type.contains("presentation") -> Colors.Warning.MAIN
        else -> Colors.Gray.MAIN
    }
}
```

## Platform Differences

### Browser (JS)

```kotlin
// JS-specific implementation
actual class FileInfo(private val file: File) {
    actual val name: String get() = file.name
    actual val size: Long get() = file.size.toLong()
    actual val type: String get() = file.type

    actual operator fun component1(): String = name
    actual operator fun component2(): Long = size
    actual operator fun component3(): String = type
}
```

### JVM

```kotlin
// JVM-specific implementation
actual class FileInfo(
    actual val name: String,
    actual val size: Long,
    actual val type: String
) {
    actual operator fun component1(): String = name
    actual operator fun component2(): Long = size
    actual operator fun component3(): String = type
}
```

## Utility Functions

### File Size Formatting

```kotlin
object FileUtils {
    fun formatFileSize(bytes: Long): String {
        if (bytes < 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        return if (unitIndex == 0) {
            "${size.toInt()} ${units[unitIndex]}"
        } else {
            "${"%.1f".format(size)} ${units[unitIndex]}"
        }
    }

    fun isImageFile(file: FileInfo): Boolean {
        return file.type.startsWith("image/")
    }

    fun isDocumentFile(file: FileInfo): Boolean {
        return file.type in listOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "text/csv"
        )
    }

    fun getFileExtension(file: FileInfo): String {
        return file.name.substringAfterLast('.', "")
    }

    fun getFileNameWithoutExtension(file: FileInfo): String {
        return file.name.substringBeforeLast('.')
    }
}
```

### File Sorting

```kotlin
enum class FileSortCriteria {
    NAME_ASC,
    NAME_DESC,
    SIZE_ASC,
    SIZE_DESC,
    TYPE_ASC,
    TYPE_DESC
}

fun List<FileInfo>.sortedBy(criteria: FileSortCriteria): List<FileInfo> {
    return when (criteria) {
        FileSortCriteria.NAME_ASC -> sortedBy { it.name.lowercase() }
        FileSortCriteria.NAME_DESC -> sortedByDescending { it.name.lowercase() }
        FileSortCriteria.SIZE_ASC -> sortedBy { it.size }
        FileSortCriteria.SIZE_DESC -> sortedByDescending { it.size }
        FileSortCriteria.TYPE_ASC -> sortedBy { it.type }
        FileSortCriteria.TYPE_DESC -> sortedByDescending { it.type }
    }
}
```

## Best Practices

### Do

- Always validate file properties before processing
- Use destructuring for cleaner code when accessing multiple properties
- Format file sizes appropriately for user display
- Group files by type or category for better organization
- Implement proper error handling for file operations

### Don't

- Assume file properties are always valid
- Ignore file size limits and type restrictions
- Display raw MIME types to users without formatting
- Process files without proper validation
- Forget to handle edge cases (empty names, zero sizes)

## Related Components

- [FileUpload](FileUpload.md) - For file selection
- [Form](Form.md) - For form integration
- [Button](Button.md) - For file actions
- [ProgressBar](../feedback/ProgressBar.md) - For upload progress
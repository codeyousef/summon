# FileUpload

FileUpload components provide file selection functionality with support for multiple files, file type restrictions,
drag-and-drop, and upload progress tracking.

## Overview

The FileUpload component allows users to select and upload files from their device. It supports various file types,
multiple file selection, drag-and-drop interfaces, and provides comprehensive file management capabilities.

### Key Features

- **File Selection**: Single and multiple file selection
- **File Type Filtering**: Accept specific file types and formats
- **Drag & Drop**: Intuitive drag-and-drop interface
- **Progress Tracking**: Upload progress monitoring
- **File Validation**: Size, type, and content validation
- **Preview Support**: Image and document previews
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms

## API Reference

### FileUpload

```kotlin
@Composable
fun FileUpload(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    multiple: Boolean = false,
    accept: String? = null,
    capture: String? = null,
    buttonLabel: String = "Upload File",
    label: String? = null,
    buttonStyle: Modifier = Modifier()
)
```

**Parameters:**

- `onFilesSelected`: Callback invoked when files are selected
- `modifier`: Modifier for the overall layout
- `enabled`: Whether the file upload is interactive (default: `true`)
- `multiple`: Whether multiple file selection is allowed (default: `false`)
- `accept`: Comma-separated string of accepted file types (e.g., "image/*,.pdf")
- `capture`: Input source for captured media ("user", "environment")
- `buttonLabel`: Text displayed on the trigger button (default: "Upload File")
- `label`: Optional label displayed above the button
- `buttonStyle`: Modifier applied specifically to the trigger button

### FileUpload (Stateful)

```kotlin
@Composable
fun FileUpload(
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    multiple: Boolean = false,
    accept: String? = null,
    capture: String? = null,
    buttonLabel: String = "Upload File",
    label: String? = null,
    buttonStyle: Modifier = Modifier()
): State<List<FileInfo>>
```

**Returns a State object holding the list of currently selected FileInfo objects.**

### FileInfo

```kotlin
expect class FileInfo {
    val name: String    // The name of the file
    val size: Long      // The size of the file in bytes
    val type: String    // The MIME type of the file

    operator fun component1(): String  // For destructuring
    operator fun component2(): Long
    operator fun component3(): String
}
```

## Usage Examples

### Basic File Upload

```kotlin
@Composable
fun BasicFileUploadExample() {
    var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Upload Document", style = Typography.H6)

        FileUpload(
            onFilesSelected = { selectedFiles = it },
            label = "Select File",
            buttonLabel = "Choose File",
            modifier = Modifier().width(300.px)
        )

        if (selectedFiles.isNotEmpty()) {
            selectedFiles.forEach { file ->
                Card(
                    modifier = Modifier()
                        .width(300.px)
                        .backgroundColor(Colors.Info.LIGHT)
                        .padding(Spacing.MD)
                ) {
                    Column(modifier = Modifier().gap(Spacing.SM)) {
                        Text(file.name, style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text("Size: ${formatFileSize(file.size)}", style = Typography.BODY2)
                        Text("Type: ${file.type}", style = Typography.CAPTION)
                    }
                }
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

### Image Upload with Preview

```kotlin
@Composable
fun ImageUploadExample() {
    var selectedImages by remember { mutableStateOf<List<FileInfo>>(emptyList()) }
    var uploadProgress by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Upload Images", style = Typography.H6)

        FileUpload(
            onFilesSelected = { files ->
                selectedImages = files.filter { it.type.startsWith("image/") }
            },
            multiple = true,
            accept = "image/*",
            label = "Select Images",
            buttonLabel = "Choose Images",
            modifier = Modifier().width(400.px)
        )

        if (selectedImages.isNotEmpty()) {
            Text("Selected Images:", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))

            Column(modifier = Modifier().gap(Spacing.SM)) {
                selectedImages.forEach { image ->
                    ImageUploadCard(
                        file = image,
                        progress = uploadProgress[image.name] ?: 0f,
                        onRemove = {
                            selectedImages = selectedImages.filter { it.name != image.name }
                            uploadProgress = uploadProgress - image.name
                        }
                    )
                }
            }

            Row(modifier = Modifier().gap(Spacing.MD)) {
                Button(
                    text = "Upload All",
                    type = ButtonType.PRIMARY,
                    onClick = {
                        selectedImages.forEach { image ->
                            startImageUpload(image.name) { progress ->
                                uploadProgress = uploadProgress + (image.name to progress)
                            }
                        }
                    }
                )

                Button(
                    text = "Clear All",
                    type = ButtonType.SECONDARY,
                    onClick = {
                        selectedImages = emptyList()
                        uploadProgress = emptyMap()
                    }
                )
            }
        }
    }
}

@Composable
fun ImageUploadCard(
    file: FileInfo,
    progress: Float,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier()
            .width(400.px)
            .border(Border.solid(1.px, Colors.Gray.LIGHT))
            .borderRadius(BorderRadius.MD)
            .padding(Spacing.MD)
    ) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .gap(Spacing.MD)
                .alignItems(AlignItems.Center)
        ) {
            // Image preview placeholder
            Box(
                modifier = Modifier()
                    .width(60.px)
                    .height(60.px)
                    .backgroundColor(Colors.Gray.LIGHT)
                    .borderRadius(BorderRadius.SM)
                    .alignItems(AlignItems.Center)
                    .justifyContent(JustifyContent.Center)
            ) {
                Icon(name = "image", color = Colors.Gray.MAIN)
            }

            Column(
                modifier = Modifier()
                    .weight(1f)
                    .gap(Spacing.XS)
            ) {
                Text(file.name, style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM))
                Text(formatFileSize(file.size), style = Typography.CAPTION)

                if (progress > 0f) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .alignItems(AlignItems.Center)
                            .gap(Spacing.SM)
                    ) {
                        Box(
                            modifier = Modifier()
                                .weight(1f)
                                .height(4.px)
                                .backgroundColor(Colors.Gray.LIGHT)
                                .borderRadius(BorderRadius.SM)
                        ) {
                            Box(
                                modifier = Modifier()
                                    .width("${(progress * 100).toInt()}%")
                                    .height(Width.FULL)
                                    .backgroundColor(Colors.Success.MAIN)
                                    .borderRadius(BorderRadius.SM)
                            ) {}
                        }
                        Text("${(progress * 100).toInt()}%", style = Typography.CAPTION)
                    }
                }
            }

            Button(
                text = "×",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = onRemove,
                modifier = Modifier().minWidth(32.px)
            )
        }
    }
}

private fun startImageUpload(fileName: String, onProgress: (Float) -> Unit) {
    // Simulate upload progress
    // In real implementation, this would be actual upload logic
}
```

### Document Upload with Validation

```kotlin
@Composable
fun DocumentUploadExample() {
    var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }
    var validationErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val maxFileSize = 10 * 1024 * 1024 // 10 MB
    val allowedTypes = listOf("application/pdf", "application/msword", "text/plain")

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Upload Documents", style = Typography.H6)
        Text(
            "Accepted formats: PDF, DOC, TXT | Max size: 10 MB",
            style = Typography.BODY2.copy(color = Colors.Gray.MAIN)
        )

        FileUpload(
            onFilesSelected = { files ->
                selectedFiles = files
                validationErrors = validateFiles(files, maxFileSize, allowedTypes)
            },
            multiple = true,
            accept = ".pdf,.doc,.docx,.txt",
            label = "Select Documents",
            buttonLabel = "Choose Documents",
            modifier = Modifier().width(500.px)
        )

        if (selectedFiles.isNotEmpty()) {
            Column(modifier = Modifier().gap(Spacing.SM)) {
                selectedFiles.forEach { file ->
                    val error = validationErrors[file.name]

                    Card(
                        modifier = Modifier()
                            .width(500.px)
                            .border(
                                Border.solid(
                                    1.px,
                                    if (error != null) Colors.Error.MAIN else Colors.Gray.LIGHT
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
                                Text(
                                    file.name,
                                    style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM)
                                )
                                Text(
                                    "${formatFileSize(file.size)} • ${file.type}",
                                    style = Typography.CAPTION
                                )

                                if (error != null) {
                                    Text(
                                        error,
                                        style = Typography.CAPTION.copy(color = Colors.Error.MAIN)
                                    )
                                }
                            }

                            if (error == null) {
                                Icon(name = "check_circle", color = Colors.Success.MAIN)
                            } else {
                                Icon(name = "error", color = Colors.Error.MAIN)
                            }
                        }
                    }
                }
            }

            val validFiles = selectedFiles.filter { validationErrors[it.name] == null }

            if (validFiles.isNotEmpty()) {
                Button(
                    text = "Upload ${validFiles.size} file${if (validFiles.size != 1) "s" else ""}",
                    type = ButtonType.PRIMARY,
                    onClick = {
                        println("Uploading ${validFiles.size} valid files")
                    }
                )
            }
        }
    }
}

private fun validateFiles(
    files: List<FileInfo>,
    maxFileSize: Long,
    allowedTypes: List<String>
): Map<String, String> {
    return files.associate { file ->
        val error = when {
            file.size > maxFileSize -> "File size exceeds 10 MB limit"
            file.type !in allowedTypes -> "File type not supported"
            file.name.isBlank() -> "Invalid file name"
            else -> null
        }
        file.name to (error ?: "")
    }.filterValues { it.isNotEmpty() }
}
```

### Drag and Drop Upload

```kotlin
@Composable
fun DragDropUploadExample() {
    var isDragOver by remember { mutableStateOf(false) }
    var uploadedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Drag & Drop Upload", style = Typography.H6)

        // Drag and drop area
        Box(
            modifier = Modifier()
                .width(400.px)
                .height(200.px)
                .border(
                    Border.dashed(
                        2.px,
                        if (isDragOver) Colors.Primary.MAIN else Colors.Gray.MAIN
                    )
                )
                .borderRadius(BorderRadius.MD)
                .backgroundColor(
                    if (isDragOver) Colors.Primary.LIGHT else Colors.Gray.LIGHT
                )
                .alignItems(AlignItems.Center)
                .justifyContent(JustifyContent.Center)
                .onDragOver { isDragOver = true }
                .onDragLeave { isDragOver = false }
                .onDrop { files ->
                    isDragOver = false
                    uploadedFiles = uploadedFiles + files
                }
        ) {
            Column(
                modifier = Modifier()
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.MD)
            ) {
                Icon(
                    name = "cloud_upload",
                    color = if (isDragOver) Colors.Primary.MAIN else Colors.Gray.MAIN,
                    modifier = Modifier().fontSize(48.px)
                )

                Text(
                    if (isDragOver) "Drop files here" else "Drag files here or click to browse",
                    style = Typography.BODY1.copy(
                        color = if (isDragOver) Colors.Primary.MAIN else Colors.Gray.MAIN
                    )
                )

                FileUpload(
                    onFilesSelected = { files ->
                        uploadedFiles = uploadedFiles + files
                    },
                    multiple = true,
                    buttonLabel = "Browse Files",
                    buttonStyle = Modifier()
                        .backgroundColor(Colors.Primary.MAIN)
                        .color(Colors.White)
                        .padding(Spacing.SM)
                        .borderRadius(BorderRadius.SM)
                )
            }
        }

        // Uploaded files list
        if (uploadedFiles.isNotEmpty()) {
            Text("Uploaded Files:", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))

            Column(modifier = Modifier().gap(Spacing.SM)) {
                uploadedFiles.forEach { file ->
                    Row(
                        modifier = Modifier()
                            .width(400.px)
                            .padding(Spacing.SM)
                            .backgroundColor(Colors.Success.LIGHT)
                            .borderRadius(BorderRadius.SM)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text(file.name, style = Typography.BODY2)
                        Text(formatFileSize(file.size), style = Typography.CAPTION)
                    }
                }
            }

            Button(
                text = "Clear All",
                type = ButtonType.OUTLINE,
                onClick = { uploadedFiles = emptyList() }
            )
        }
    }
}
```

### Camera Capture

```kotlin
@Composable
fun CameraCaptureExample() {
    var capturedImages by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Camera Capture", style = Typography.H6)

        Row(modifier = Modifier().gap(Spacing.MD)) {
            FileUpload(
                onFilesSelected = { files ->
                    capturedImages = capturedImages + files.filter { it.type.startsWith("image/") }
                },
                accept = "image/*",
                capture = "environment", // Rear camera
                buttonLabel = "Take Photo",
                multiple = true
            )

            FileUpload(
                onFilesSelected = { files ->
                    capturedImages = capturedImages + files.filter { it.type.startsWith("image/") }
                },
                accept = "image/*",
                capture = "user", // Front camera
                buttonLabel = "Selfie",
                multiple = true
            )
        }

        if (capturedImages.isNotEmpty()) {
            Text("Captured Images:", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))

            LazyRow(modifier = Modifier().gap(Spacing.SM)) {
                items(capturedImages) { image ->
                    Card(
                        modifier = Modifier()
                            .width(120.px)
                            .height(120.px)
                            .border(Border.solid(1.px, Colors.Gray.LIGHT))
                            .borderRadius(BorderRadius.MD)
                    ) {
                        Box(
                            modifier = Modifier()
                                .width(Width.FULL)
                                .height(Width.FULL)
                                .backgroundColor(Colors.Gray.LIGHT)
                                .alignItems(AlignItems.Center)
                                .justifyContent(JustifyContent.Center)
                        ) {
                            Icon(name = "image", color = Colors.Gray.MAIN)
                        }
                    }
                }
            }
        }
    }
}
```

### Form Integration

```kotlin
@Composable
fun FileUploadFormExample() {
    var profileImage by remember { mutableStateOf<FileInfo?>(null) }
    var resume by remember { mutableStateOf<FileInfo?>(null) }
    var portfolio by remember { mutableStateOf<List<FileInfo>>(emptyList()) }
    var name by remember { mutableStateOf("") }

    Form(
        onSubmit = {
            println("Profile submitted:")
            println("Name: $name")
            println("Profile Image: ${profileImage?.name}")
            println("Resume: ${resume?.name}")
            println("Portfolio: ${portfolio.map { it.name }}")
        }
    ) {
        FormField(label = "Profile Information") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name *",
                    validators = listOf(
                        Validator { value ->
                            if (value.isBlank()) {
                                ValidationResult.invalid("Name is required")
                            } else {
                                ValidationResult.valid()
                            }
                        }
                    ),
                    modifier = Modifier().width(Width.FULL)
                )

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Profile Picture", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    FileUpload(
                        onFilesSelected = { files ->
                            profileImage = files.firstOrNull()
                        },
                        accept = "image/*",
                        buttonLabel = "Choose Image",
                        modifier = Modifier().width(250.px)
                    )

                    profileImage?.let { image ->
                        Text(
                            "Selected: ${image.name} (${formatFileSize(image.size)})",
                            style = Typography.CAPTION.copy(color = Colors.Success.MAIN)
                        )
                    }
                }

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Resume *", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    FileUpload(
                        onFilesSelected = { files ->
                            resume = files.firstOrNull()
                        },
                        accept = ".pdf,.doc,.docx",
                        buttonLabel = "Upload Resume",
                        modifier = Modifier().width(250.px)
                    )

                    resume?.let { resumeFile ->
                        Text(
                            "Selected: ${resumeFile.name} (${formatFileSize(resumeFile.size)})",
                            style = Typography.CAPTION.copy(color = Colors.Success.MAIN)
                        )
                    }
                }

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Portfolio (Optional)", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    FileUpload(
                        onFilesSelected = { files ->
                            portfolio = files
                        },
                        accept = "image/*,.pdf",
                        multiple = true,
                        buttonLabel = "Add Portfolio Items",
                        modifier = Modifier().width(250.px)
                    )

                    if (portfolio.isNotEmpty()) {
                        Text(
                            "${portfolio.size} file${if (portfolio.size != 1) "s" else ""} selected",
                            style = Typography.CAPTION.copy(color = Colors.Info.MAIN)
                        )
                    }
                }
            }
        }

        Button(
            text = "Submit Application",
            type = ButtonType.SUBMIT,
            enabled = name.isNotBlank() && resume != null
        )
    }
}
```

### Stateful File Upload

```kotlin
@Composable
fun StatefulFileUploadExample() {
    val filesState = FileUpload(
        multiple = true,
        accept = "image/*,application/pdf",
        buttonLabel = "Select Files",
        label = "Upload Documents"
    )

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Stateful File Upload", style = Typography.H6)

        Text("Selected files: ${filesState.value.size}")

        filesState.value.forEach { file ->
            Text("• ${file.name}", style = Typography.BODY2)
        }
    }
}
```

## Accessibility Features

### ARIA Support

The FileUpload component automatically includes:

- `role="button"` for the trigger button
- `aria-label` for screen reader description
- `aria-describedby` for file type and size restrictions
- `accept` attribute for file type filtering

### Keyboard Navigation

- **Tab**: Move focus to file upload button
- **Enter/Space**: Open file selection dialog
- **Escape**: Cancel file selection (in dialog)

### Screen Reader Support

```kotlin
@Composable
fun AccessibleFileUploadExample() {
    var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    FileUpload(
        onFilesSelected = { selectedFiles = it },
        label = "Upload Profile Picture",
        buttonLabel = "Choose Image File",
        accept = "image/*",
        modifier = Modifier()
            .accessibilityLabel("Upload profile picture")
            .accessibilityHint("Choose an image file from your device")
    )
}
```

## Validation Patterns

### File Size Validation

```kotlin
fun fileSizeValidator(maxSizeBytes: Long) = Validator { file: FileInfo ->
    if (file.size <= maxSizeBytes) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("File size must be less than ${formatFileSize(maxSizeBytes)}")
    }
}
```

### File Type Validation

```kotlin
fun fileTypeValidator(allowedTypes: List<String>) = Validator { file: FileInfo ->
    if (file.type in allowedTypes) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("File type ${file.type} is not supported")
    }
}
```

### Multiple Files Validation

```kotlin
fun fileCountValidator(maxFiles: Int) = Validator { files: List<FileInfo> ->
    if (files.size <= maxFiles) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("Maximum $maxFiles files allowed")
    }
}
```

## Platform Differences

### Browser (JS)

- Uses HTML `<input type="file">` element
- FileAPI for file information
- Drag and drop events support
- Camera capture on mobile devices

### JVM

- Server-side file handling
- Generates appropriate HTML for file uploads
- Multipart form data handling
- Server-side validation

## Performance Considerations

### Optimization Tips

1. **Lazy Loading**: Only process files when needed
2. **Progress Tracking**: Show upload progress for large files
3. **Chunked Uploads**: Break large files into chunks
4. **Validation Early**: Validate files before upload

```kotlin
@Composable
fun OptimizedFileUploadExample() {
    var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    // Memoize file validation
    val validatedFiles = remember(selectedFiles) {
        selectedFiles.filter { file ->
            file.size <= 10 * 1024 * 1024 && // 10 MB
            file.type.startsWith("image/")
        }
    }

    FileUpload(
        onFilesSelected = { files ->
            selectedFiles = files
        },
        multiple = true,
        accept = "image/*"
    )

    Text("Valid files: ${validatedFiles.size}")
}
```

## Testing Strategies

### Unit Testing

```kotlin
class FileUploadTest {
    @Test
    fun `file upload triggers callback with selected files`() {
        var selectedFiles: List<FileInfo> = emptyList()

        composeTestRule.setContent {
            FileUpload(
                onFilesSelected = { selectedFiles = it }
            )
        }

        // Simulate file selection
        // (Implementation depends on testing framework)

        assertTrue(selectedFiles.isNotEmpty())
    }

    @Test
    fun `file upload respects file type restrictions`() {
        composeTestRule.setContent {
            FileUpload(
                onFilesSelected = {},
                accept = "image/*"
            )
        }

        // Verify accept attribute is set correctly
        composeTestRule.onNode(hasContentDescription("file upload"))
            .assertExists()
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<input type="file" name="upload" accept="image/*" multiple>
```

```kotlin
// After: Summon
@Composable
fun ImageUpload() {
    var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    FileUpload(
        onFilesSelected = { selectedFiles = it },
        accept = "image/*",
        multiple = true
    )
}
```

## Best Practices

### Do

- Clearly indicate accepted file types and size limits
- Provide visual feedback during upload
- Validate files on both client and server
- Support drag and drop for better UX
- Show upload progress for large files

### Don't

- Accept unlimited file sizes without validation
- Upload files without user confirmation
- Forget to handle upload errors gracefully
- Skip accessibility considerations

## Related Components

- [FileInfo](FileInfo.md) - For file information display
- [Form](Form.md) - For form integration
- [Button](Button.md) - For trigger actions
- [ProgressBar](../feedback/ProgressBar.md) - For upload progress
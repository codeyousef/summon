package code.yousef.summon.cli.generators

import code.yousef.summon.cli.templates.TemplateEngine
import code.yousef.summon.cli.templates.TemplateHelpers
import java.io.File

/**
 * Generates individual Summon components
 */
class ComponentGenerator {

    private val templateEngine = TemplateEngine()

    data class Config(
        val name: String,
        val type: String,
        val packagePath: String? = null,
        val generateTest: Boolean = true,
        val generateDoc: Boolean = false
    )

    /**
     * Generate a component based on the configuration
     */
    fun generate(config: Config) {
        val currentDir = File(".")

        // Determine if we're in a Summon project
        val isSummonProject = File(currentDir, "src/commonMain/kotlin").exists() ||
                File(currentDir, "src/jsMain/kotlin").exists()

        if (!isSummonProject) {
            throw IllegalStateException("Not in a Summon project directory. Run this command from your project root.")
        }

        val variables = prepareComponentVariables(config)

        // Generate the component file
        generateComponentFile(config, variables)

        // Generate test file if requested
        if (config.generateTest) {
            generateTestFile(config, variables)
        }

        // Generate documentation if requested
        if (config.generateDoc) {
            generateDocFile(config, variables)
        }

        println("✅ Component '${config.name}' generated successfully!")
    }

    private fun prepareComponentVariables(config: Config): Map<String, String> {
        val componentName = TemplateHelpers.transformName(config.name, "pascalcase")
        val fileName = TemplateHelpers.transformName(config.name, "pascalcase")
        val packageSuffix = if (config.packagePath != null) ".${config.packagePath.replace("/", ".")}" else ""

        return mapOf(
            "COMPONENT_NAME" to componentName,
            "FILE_NAME" to fileName,
            "COMPONENT_TYPE" to config.type,
            "PACKAGE_SUFFIX" to packageSuffix,
            "PACKAGE_PATH" to (config.packagePath ?: ""),
            "CAMEL_CASE_NAME" to TemplateHelpers.transformName(config.name, "camelcase"),
            "KEBAB_CASE_NAME" to TemplateHelpers.transformName(config.name, "kebabcase"),
            "SNAKE_CASE_NAME" to TemplateHelpers.transformName(config.name, "snakecase")
        )
    }

    private fun generateComponentFile(config: Config, variables: Map<String, String>) {
        val sourceDir = determineSourceDirectory()
        val packageDir = if (config.packagePath != null) {
            File(sourceDir, "components/${config.packagePath}")
        } else {
            File(sourceDir, "components")
        }
        packageDir.mkdirs()

        val componentFile = File(packageDir, "${variables["FILE_NAME"]}.kt")
        val content = generateComponentContent(config.type, variables)

        componentFile.writeText(content)
    }

    private fun generateTestFile(config: Config, variables: Map<String, String>) {
        val testDir = File("src/commonTest/kotlin")
        if (!testDir.exists()) {
            testDir.mkdirs()
        }

        val packageDir = if (config.packagePath != null) {
            File(testDir, "components/${config.packagePath}")
        } else {
            File(testDir, "components")
        }
        packageDir.mkdirs()

        val testFile = File(packageDir, "${variables["FILE_NAME"]}Test.kt")
        val content = generateTestContent(variables)

        testFile.writeText(content)
    }

    private fun generateDocFile(config: Config, variables: Map<String, String>) {
        val docsDir = File("docs/components")
        if (config.packagePath != null) {
            docsDir.mkdirs()
        }

        val packageDir = if (config.packagePath != null) {
            File(docsDir, config.packagePath)
        } else {
            docsDir
        }
        packageDir.mkdirs()

        val docFile = File(packageDir, "${variables["FILE_NAME"]}.md")
        val content = generateDocContent(variables)

        docFile.writeText(content)
    }

    private fun determineSourceDirectory(): File {
        // Check for different source directory structures
        val commonMain = File("src/commonMain/kotlin")
        val jsMain = File("src/jsMain/kotlin")

        return when {
            commonMain.exists() -> commonMain
            jsMain.exists() -> jsMain
            else -> {
                // Create commonMain as default
                commonMain.mkdirs()
                commonMain
            }
        }
    }

    private fun generateComponentContent(type: String, variables: Map<String, String>): String {
        return when (type) {
            "basic" -> generateBasicComponent(variables)
            "stateful" -> generateStatefulComponent(variables)
            "form" -> generateFormComponent(variables)
            "layout" -> generateLayoutComponent(variables)
            "input" -> generateInputComponent(variables)
            else -> generateBasicComponent(variables)
        }
    }

    private fun generateBasicComponent(variables: Map<String, String>): String {
        return """
@file:Suppress("FunctionName")

package components${variables["PACKAGE_SUFFIX"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.modifier.Modifier

/**
 * ${variables["COMPONENT_NAME"]} component
 * 
 * A basic Summon component that displays text.
 * 
 * @param text The text to display
 * @param modifier Modifier for styling
 */
@Composable
fun ${variables["COMPONENT_NAME"]}(
    text: String,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = text,
        modifier = modifier
    )
}
        """.trimIndent()
    }

    private fun generateStatefulComponent(variables: Map<String, String>): String {
        return """
@file:Suppress("FunctionName")

package components${variables["PACKAGE_SUFFIX"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.state.remember

/**
 * ${variables["COMPONENT_NAME"]} component
 * 
 * A stateful Summon component with internal state management.
 * 
 * @param initialValue Initial value for the component state
 * @param onValueChanged Callback when the value changes
 * @param modifier Modifier for styling
 */
@Composable
fun ${variables["COMPONENT_NAME"]}(
    initialValue: String = "",
    onValueChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state = remember { mutableStateOf(initialValue) }
    
    Column(modifier = modifier) {
        BasicText(
            text = "Current value: ${'$'}{state.value}",
            modifier = Modifier.padding(bottom = 8)
        )
        
        Button(
            text = "Update Value",
            onClick = {
                val newValue = "Updated at ${'$'}{System.currentTimeMillis()}"
                state.value = newValue
                onValueChanged(newValue)
            }
        )
    }
}
        """.trimIndent()
    }

    private fun generateFormComponent(variables: Map<String, String>): String {
        return """
@file:Suppress("FunctionName")

package components${variables["PACKAGE_SUFFIX"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.state.remember
import code.yousef.summon.validation.Validator

/**
 * ${variables["COMPONENT_NAME"]} component
 * 
 * A form component with validation support.
 * 
 * @param onSubmit Callback when the form is submitted with valid data
 * @param modifier Modifier for styling
 */
@Composable
fun ${variables["COMPONENT_NAME"]}(
    onSubmit: (Map<String, String>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val formData = remember { mutableStateOf(mapOf<String, String>()) }
    val errors = remember { mutableStateOf(mapOf<String, String>()) }
    
    Column(modifier = modifier.padding(16)) {
        TextField(
            value = formData.value["field1"] ?: "",
            placeholder = "Enter field 1",
            onValueChange = { value ->
                formData.value = formData.value + ("field1" to value)
                // Clear error when user starts typing
                if (errors.value.containsKey("field1")) {
                    errors.value = errors.value - "field1"
                }
            },
            isError = errors.value.containsKey("field1"),
            errorMessage = errors.value["field1"],
            modifier = Modifier.padding(bottom = 8)
        )
        
        TextField(
            value = formData.value["field2"] ?: "",
            placeholder = "Enter field 2",
            onValueChange = { value ->
                formData.value = formData.value + ("field2" to value)
                if (errors.value.containsKey("field2")) {
                    errors.value = errors.value - "field2"
                }
            },
            isError = errors.value.containsKey("field2"),
            errorMessage = errors.value["field2"],
            modifier = Modifier.padding(bottom = 16)
        )
        
        Button(
            text = "Submit",
            onClick = {
                val validationErrors = validateForm(formData.value)
                if (validationErrors.isEmpty()) {
                    onSubmit(formData.value)
                } else {
                    errors.value = validationErrors
                }
            }
        )
    }
}

private fun validateForm(data: Map<String, String>): Map<String, String> {
    val errors = mutableMapOf<String, String>()
    
    if (data["field1"].isNullOrBlank()) {
        errors["field1"] = "Field 1 is required"
    }
    
    if (data["field2"].isNullOrBlank()) {
        errors["field2"] = "Field 2 is required"
    }
    
    return errors
}
        """.trimIndent()
    }

    private fun generateLayoutComponent(variables: Map<String, String>): String {
        return """
@file:Suppress("FunctionName")

package components${variables["PACKAGE_SUFFIX"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier

/**
 * ${variables["COMPONENT_NAME"]} component
 * 
 * A layout component that arranges child components.
 * 
 * @param modifier Modifier for styling
 * @param content The child components to layout
 */
@Composable
fun ${variables["COMPONENT_NAME"]}(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        content()
    }
}
        """.trimIndent()
    }

    private fun generateInputComponent(variables: Map<String, String>): String {
        return """
@file:Suppress("FunctionName")

package components${variables["PACKAGE_SUFFIX"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.TextField
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.state.remember

/**
 * ${variables["COMPONENT_NAME"]} component
 * 
 * A custom input component with specialized behavior.
 * 
 * @param value Current value
 * @param onValueChange Callback when value changes
 * @param placeholder Placeholder text
 * @param modifier Modifier for styling
 */
@Composable
fun ${variables["COMPONENT_NAME"]}(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    val internalValue = remember { mutableStateOf(value) }
    
    // Update internal value when external value changes
    if (internalValue.value != value) {
        internalValue.value = value
    }
    
    TextField(
        value = internalValue.value,
        placeholder = placeholder,
        onValueChange = { newValue ->
            val processedValue = processInput(newValue)
            internalValue.value = processedValue
            onValueChange(processedValue)
        },
        modifier = modifier
    )
}

private fun processInput(input: String): String {
    // Add custom input processing logic here
    // For example: formatting, validation, transformation
    return input.trim()
}
        """.trimIndent()
    }

    private fun generateTestContent(variables: Map<String, String>): String {
        return """
package components${variables["PACKAGE_SUFFIX"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.test.TestComposer
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for ${variables["COMPONENT_NAME"]} component
 */
class ${variables["COMPONENT_NAME"]}Test {
    
    @Test
    fun test${variables["COMPONENT_NAME"]}Creation() {
        // Test that component renders without errors
        val renderer = MockPlatformRenderer()
        val composer = TestComposer(renderer)
        
        composer.compose {
            ${variables["COMPONENT_NAME"]}()
        }
        
        assertTrue(renderer.hasRenderedElements())
    }
    
    @Test
    fun test${variables["COMPONENT_NAME"]}Behavior() {
        // Test component interaction and state management
        val renderer = MockPlatformRenderer()
        val composer = TestComposer(renderer)
        
        composer.compose {
            ${variables["COMPONENT_NAME"]}()
        }
        
        // Test initial render
        assertTrue(renderer.getRenderedText().isNotEmpty())
        
        // Test interactions if component has clickable elements
        // renderer.simulateClick("button")
        // composer.recompose()
        // Add assertions for expected behavior changes
    }
    
    @Test
    fun test${variables["COMPONENT_NAME"]}Props() {
        // Test component with different props
        val renderer = MockPlatformRenderer()
        val composer = TestComposer(renderer)
        
        // Test with various prop combinations
        composer.compose {
            ${variables["COMPONENT_NAME"]}(
                // Add prop tests here based on component parameters
            )
        }
        
        // Verify props are correctly applied
        assertTrue(renderer.hasRenderedElements())
    }
}
        """.trimIndent()
    }

    private fun generateDocContent(variables: Map<String, String>): String {
        return """
# ${variables["COMPONENT_NAME"]}

## Description

The ${variables["COMPONENT_NAME"]} component is a ${variables["COMPONENT_TYPE"]} component for Summon applications.

## Usage

```kotlin
@Composable
fun Example() {
    ${variables["COMPONENT_NAME"]}(
        // Add component props here
    )
}
```

## Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| modifier | Modifier | Modifier | Styling modifier |

## Examples

### Basic Usage

```kotlin
${variables["COMPONENT_NAME"]}(
    modifier = Modifier.padding(16)
)
```

### Advanced Usage

```kotlin
${variables["COMPONENT_NAME"]}(
    // Add advanced usage example here
    modifier = Modifier
        .padding(16)
        .fillMaxWidth()
)
```

## Accessibility

The ${variables["COMPONENT_NAME"]} component supports:

- [ ] Screen readers
- [ ] Keyboard navigation
- [ ] High contrast mode
- [ ] Touch accessibility

## Testing

Run the component tests with:

```bash
./gradlew test --tests "*${variables["COMPONENT_NAME"]}Test"
```

## See Also

- [Component Documentation](../README.md)
- [Styling Guide](../../styling.md)
- [Accessibility Guide](../../accessibility.md)
        """.trimIndent()
    }
}
package code.yousef.summon.cli.templates

import java.io.File

/**
 * Template engine for processing template files with variable substitution.
 */
class TemplateEngine {

    /**
     * Process a template string with variable substitution
     */
    fun processTemplate(template: String, variables: Map<String, String>): String {
        var result = template

        // Replace {{VARIABLE}} style placeholders
        for ((key, value) in variables) {
            result = result.replace("{{${key}}}", value)
            result = result.replace("{{${key.uppercase()}}}", value)
            result = result.replace("{{${key.lowercase()}}}", value)
        }

        // Replace ${VARIABLE} style placeholders (Gradle style)
        for ((key, value) in variables) {
            result = result.replace("\${${key}}", value)
            result = result.replace("\${${key.uppercase()}}", value)
            result = result.replace("\${${key.lowercase()}}", value)
        }

        // Replace __VARIABLE__ style placeholders
        for ((key, value) in variables) {
            result = result.replace("__${key}__", value)
            result = result.replace("__${key.uppercase()}__", value)
            result = result.replace("__${key.lowercase()}__", value)
        }

        return result
    }

    /**
     * Process a template file and write the result to target file
     */
    fun processTemplateFile(
        sourceFile: File,
        targetFile: File,
        variables: Map<String, String>
    ) {
        if (!sourceFile.exists()) {
            throw IllegalArgumentException("Source template file does not exist: ${sourceFile.absolutePath}")
        }

        // Ensure target directory exists
        targetFile.parentFile?.mkdirs()

        val templateContent = sourceFile.readText()
        val processedContent = processTemplate(templateContent, variables)

        targetFile.writeText(processedContent)

        // Preserve executable permissions
        if (sourceFile.canExecute()) {
            targetFile.setExecutable(true)
        }
    }

    /**
     * Process template content with advanced features like conditionals and loops
     */
    fun processAdvancedTemplate(template: String, context: TemplateContext): String {
        var result = template

        // Process basic variable substitution first
        result = processTemplate(result, context.variables)

        // Process conditional blocks: {{#if CONDITION}}...{{/if}}
        result = processConditionals(result, context)

        // Process loops: {{#each ARRAY}}...{{/each}}
        result = processLoops(result, context)

        // Process includes: {{>partial}}
        result = processIncludes(result, context)

        return result
    }

    private fun processConditionals(template: String, context: TemplateContext): String {
        val ifPattern = Regex("\\{\\{#if\\s+(\\w+)\\}\\}([\\s\\S]*?)\\{\\{/if\\}\\}")
        val unlessPattern = Regex("\\{\\{#unless\\s+(\\w+)\\}\\}([\\s\\S]*?)\\{\\{/unless\\}\\}")

        var result = template

        // Process {{#if CONDITION}}...{{/if}}
        result = ifPattern.replace(result) { match ->
            val condition = match.groupValues[1]
            val content = match.groupValues[2]

            val conditionValue = context.variables[condition] ?: context.flags[condition]
            if (isTruthy(conditionValue)) content else ""
        }

        // Process {{#unless CONDITION}}...{{/unless}}
        result = unlessPattern.replace(result) { match ->
            val condition = match.groupValues[1]
            val content = match.groupValues[2]

            val conditionValue = context.variables[condition] ?: context.flags[condition]
            if (!isTruthy(conditionValue)) content else ""
        }

        return result
    }

    private fun processLoops(template: String, context: TemplateContext): String {
        val eachPattern = Regex("\\{\\{#each\\s+(\\w+)\\}\\}([\\s\\S]*?)\\{\\{/each\\}\\}")

        return eachPattern.replace(template) { match ->
            val arrayName = match.groupValues[1]
            val content = match.groupValues[2]

            val array = context.arrays[arrayName] ?: emptyList()
            array.joinToString("") { item ->
                val itemContext = context.copy(variables = context.variables + ("this" to item))
                processTemplate(content, itemContext.variables)
            }
        }
    }

    private fun processIncludes(template: String, context: TemplateContext): String {
        val includePattern = Regex("\\{\\{>\\s*(\\w+)\\}\\}")

        return includePattern.replace(template) { match ->
            val partialName = match.groupValues[1]
            context.partials[partialName] ?: "<!-- Partial '$partialName' not found -->"
        }
    }

    private fun isTruthy(value: Any?): Boolean {
        return when (value) {
            null -> false
            is Boolean -> value
            is String -> value.isNotEmpty() && value != "false" && value != "0"
            is Number -> value != 0
            else -> true
        }
    }

    /**
     * Generate file name from template with variable substitution
     */
    fun processFileName(fileName: String, variables: Map<String, String>): String {
        return processTemplate(fileName, variables)
    }
}

/**
 * Context object for advanced template processing
 */
data class TemplateContext(
    val variables: Map<String, String> = emptyMap(),
    val flags: Map<String, Boolean> = emptyMap(),
    val arrays: Map<String, List<String>> = emptyMap(),
    val partials: Map<String, String> = emptyMap()
)

/**
 * Helper functions for common template operations
 */
object TemplateHelpers {

    /**
     * Convert a string to different naming conventions
     */
    fun transformName(name: String, convention: String): String {
        return when (convention.lowercase()) {
            "camelcase" -> toCamelCase(name)
            "pascalcase" -> toPascalCase(name)
            "kebabcase", "kebab-case" -> toKebabCase(name)
            "snakecase", "snake_case" -> toSnakeCase(name)
            "uppercase" -> name.uppercase()
            "lowercase" -> name.lowercase()
            else -> name
        }
    }

    private fun toCamelCase(input: String): String {
        return input.split("[-_\\s]".toRegex())
            .mapIndexed { index, word ->
                if (index == 0) word.lowercase()
                else word.replaceFirstChar { it.uppercaseChar() }
            }
            .joinToString("")
    }

    private fun toPascalCase(input: String): String {
        return input.split("[-_\\s]".toRegex())
            .joinToString("") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
    }

    private fun toKebabCase(input: String): String {
        return input.replace("([a-z])([A-Z])".toRegex(), "$1-$2")
            .replace("[_\\s]".toRegex(), "-")
            .lowercase()
    }

    private fun toSnakeCase(input: String): String {
        return input.replace("([a-z])([A-Z])".toRegex(), "$1_$2")
            .replace("[-\\s]".toRegex(), "_")
            .lowercase()
    }
}
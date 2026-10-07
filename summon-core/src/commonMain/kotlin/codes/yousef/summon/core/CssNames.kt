package codes.yousef.summon.core

/**
 * Converts camel-case identifiers to kebab case without constructing a regular expression.
 *
 * CSS and route identifiers are expected to use letters from the Kotlin identifier alphabet.
 * A separator is inserted only at a lower-case to upper-case boundary, matching the previous
 * `([a-z])([A-Z])` conversion for ASCII names while also handling non-ASCII letters safely.
 */
internal fun String.camelToKebabCase(): String {
    var requiresConversion = false
    var previousWasLowerCase = false
    for (character in this) {
        if (character.isUpperCase()) {
            requiresConversion = true
            if (previousWasLowerCase) break
        }
        previousWasLowerCase = character.isLowerCase()
    }
    if (!requiresConversion) return this

    return buildString(length + 4) {
        var previousWasLowerCase = false
        for (character in this@camelToKebabCase) {
            if (previousWasLowerCase && character.isUpperCase()) append('-')
            append(character.lowercaseChar())
            previousWasLowerCase = character.isLowerCase()
        }
    }
}

package code.yousef.summon.modifier

/**
 * Adds a single attribute to the Modifier by creating a new Modifier instance.
 * Leverages the existing `attribute` method on the Modifier class.
 * @return A new Modifier instance with the added attribute.
 */
fun Modifier.withAttribute(key: String, value: String): Modifier {
    return this.attribute(key, value) // Use the existing 'attribute' method from Modifier.kt
}

/**
 * Adds multiple attributes to the Modifier by creating a new Modifier instance.
 * @return A new Modifier instance with the added attributes.
 */
fun Modifier.withAttributes(attrs: Map<String, String>): Modifier {
    return this.copy(attributes = this.attributes + attrs) // Create a new map by combining and then copy
}

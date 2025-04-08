package code.yousef.summon.test

/**
 * These are test-only implementations of the component classes used in TestExtensions.kt
 * These are needed for unit testing without using the full composable functions.
 */

/**
 * Test version of Image that contains just the properties we want to test
 */
class Image(
    val src: String,
    val alt: String,
    val width: String? = null,
    val height: String? = null
)

/**
 * Test version of Button that contains just the properties we want to test
 */
class Button(
    val label: String,
    val onClick: (Any) -> Unit = {},
    val disabled: Boolean = false
)

/**
 * Test version of Text that contains just the properties we want to test
 */
class Text(
    val text: String
) 
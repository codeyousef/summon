package code.yousef.summon

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

/**
 * Test helpers for common code to be used in both JVM and JS tests.
 * These are simple property checks that don't require platform-specific rendering.
 */

/**
 * Verify that the Image component has the expected property values
 */
fun Image.verifyProperties(
    expectedSrc: String,
    expectedAlt: String? = null,
    expectedWidth: String? = null,
    expectedHeight: String? = null
): Boolean {
    return src == expectedSrc &&
           (expectedAlt == null || alt == expectedAlt) &&
           (expectedWidth == null || width == expectedWidth) &&
           (expectedHeight == null || height == expectedHeight)
}

/**
 * Verify that the Button component has the expected property values
 */
fun Button.verifyProperties(
    expectedLabel: String,
    expectedDisabled: Boolean? = null
): Boolean {
    return label == expectedLabel &&
           (expectedDisabled == null || disabled == expectedDisabled)
}

/**
 * Verify that the Text component has the expected property values
 */
fun Text.verifyProperties(
    expectedText: String
): Boolean {
    return text == expectedText
} 

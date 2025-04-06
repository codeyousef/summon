package code.yousef.summon

import code.yousef.summon.components.display.Image
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button


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
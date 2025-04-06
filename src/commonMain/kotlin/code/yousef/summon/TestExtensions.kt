package code.yousef.summon

import code.yousef.summon.components.display.ImageData
import code.yousef.summon.components.display.TextData
import code.yousef.summon.components.input.ButtonData

/**
 * Test helpers for common code to be used in both JVM and JS tests.
 * These check properties of the Data objects passed to composables.
 */

/**
 * Verify that the ImageData has the expected property values
 */
fun ImageData.verifyProperties(
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
 * Verify that the ButtonData has the expected property values.
 * Note: Cannot easily verify the rendered label/content from here.
 */
fun ButtonData.verifyProperties(
    expectedEnabled: Boolean? = null
): Boolean {
    return (expectedEnabled == null || enabled == expectedEnabled)
}

/**
 * Verify that the TextData has the expected property values
 */
fun TextData.verifyProperties(
    expectedText: String
): Boolean {
    return text == expectedText
} 
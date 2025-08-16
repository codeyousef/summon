package code.yousef.summon

import code.yousef.summon.test.Button
import code.yousef.summon.test.Image
import code.yousef.summon.test.Text

/**
 * Test helpers for common code to be used in both JVM and JS tests.
 * These are simple property checks that don't require platform-specific rendering.
 */

/**
 * Simple assert function for testing.
 * This is a common implementation that works across platforms.
 */
internal inline fun assert(value: Boolean, lazyMessage: () -> Any = { "" }) {
    if (!value) {
        val message = lazyMessage()
        throw Error("Assertion failed: $message")
    }
}

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

/**
 * Verify that an Image component was rendered with the given properties
 */
fun verifyImage(image: Image, src: String, alt: String, width: String? = null, height: String? = null) {
    assert(image.src == src) { "Expected src to be $src, but was ${image.src}" }
    assert(image.alt == alt) { "Expected alt to be $alt, but was ${image.alt}" }
    if (width != null) {
        assert(image.width == width) { "Expected width to be $width, but was ${image.width}" }
    }
    if (height != null) {
        assert(image.height == height) { "Expected height to be $height, but was ${image.height}" }
    }
}

/**
 * Verify that a Button component was rendered with the given properties
 */
fun verifyButton(button: Button, label: String, disabled: Boolean = false) {
    assert(button.label == label) { "Expected label to be $label, but was ${button.label}" }
    assert(button.disabled == disabled) { "Expected disabled to be $disabled, but was ${button.disabled}" }
}

/**
 * Verify that a Text component was rendered with the given text
 */
fun verifyText(text: Text, expectedText: String) {
    assert(text.text == expectedText) { "Expected text to be $expectedText, but was ${text.text}" }
} 

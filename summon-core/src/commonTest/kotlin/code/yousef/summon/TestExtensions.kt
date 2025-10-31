package code.yousef.summon

import code.yousef.summon.test.Button
import code.yousef.summon.test.Image
import code.yousef.summon.test.Text

internal inline fun assert(value: Boolean, lazyMessage: () -> Any = { "" }) {
    if (!value) {
        val message = lazyMessage()
        throw AssertionError("Assertion failed: $message")
    }
}

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

fun Button.verifyProperties(
    expectedLabel: String,
    expectedDisabled: Boolean? = null
): Boolean {
    return label == expectedLabel &&
            (expectedDisabled == null || disabled == expectedDisabled)
}

fun Text.verifyProperties(expectedText: String): Boolean = text == expectedText

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

fun verifyButton(button: Button, label: String, disabled: Boolean = false) {
    assert(button.label == label) { "Expected label to be $label, but was ${button.label}" }
    assert(button.disabled == disabled) { "Expected disabled to be $disabled, but was ${button.disabled}" }
}

fun verifyText(text: Text, expectedText: String) {
    assert(text.text == expectedText) { "Expected text to be $expectedText, but was ${text.text}" }
}

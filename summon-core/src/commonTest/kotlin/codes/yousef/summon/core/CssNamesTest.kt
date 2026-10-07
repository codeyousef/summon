package codes.yousef.summon.core

import kotlin.test.Test
import kotlin.test.assertEquals

class CssNamesTest {
    @Test
    fun camelCaseConversionDoesNotRequireRegexSupport() {
        assertEquals("background-color", "backgroundColor".camelToKebabCase())
        assertEquals("webkit-transform", "WebkitTransform".camelToKebabCase())
        assertEquals("xmlhttp-request", "XMLHttpRequest".camelToKebabCase())
        assertEquals("font-size", "font-size".camelToKebabCase())
        assertEquals("über-größe", "überGröße".camelToKebabCase())
    }
}

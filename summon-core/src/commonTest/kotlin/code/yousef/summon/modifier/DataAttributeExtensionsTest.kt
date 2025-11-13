package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DataAttributeExtensionsTest {

    @Test
    fun dataAttributesAppliesPrefixedAttribute() {
        val modifier = Modifier()
            .dataAttributes(
                mapOf(
                    "copy" to "token",
                    "href" to "#cta",
                    "" to "ignored"
                )
            )

        assertEquals("token", modifier.attributes["data-copy"])
        assertEquals("#cta", modifier.attributes["data-href"])
        assertFalse(modifier.attributes.containsKey("data-"))
    }
}

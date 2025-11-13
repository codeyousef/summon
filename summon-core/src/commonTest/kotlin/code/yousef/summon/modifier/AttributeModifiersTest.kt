package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals

class AttributeModifiersTest {

    @Test
    fun buttonTypeSetsAttribute() {
        val modifier = Attributes.run { Modifier().buttonType(ButtonType.Submit) }
        assertEquals("submit", modifier.attributes["type"])
    }
}

package codes.yousef.summon.hydration

import kotlinx.html.stream.appendHTML
import kotlinx.html.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SummonTagConsumerTest {

    @Test
    fun `generates deterministic path-based IDs`() {
        val sb = StringBuilder()
        val downstream = sb.appendHTML()
        val consumer = SummonTagConsumer(downstream)

        consumer.div {
            div {
                span { +"text" }
            }
        }

        val result = sb.toString()
        // Verify that IDs are generated. 
        // SummonTagConsumer is expected to add 'id' attributes to elements.
        // Example: <div id="div-1"><div id="div-1-div-1"><span id="div-1-div-1-span-1">text</span></div></div>
        // We just check for presence of id attribute.
        assertTrue(result.contains("id=\""), "Output should contain generated IDs: $result")
    }
    
    @Test
    fun `preserves explicit IDs`() {
        val sb = StringBuilder()
        val downstream = sb.appendHTML()
        val consumer = SummonTagConsumer(downstream)

        consumer.div {
            id = "explicit-id"
            span { +"text" }
        }

        val result = sb.toString()
        assertTrue(result.contains("id=\"explicit-id\""), "Should preserve explicit ID: $result")
    }
}
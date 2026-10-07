package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.integration.quarkus.htmx.*
import codes.yousef.summon.integration.quarkus.renderer.EnhancedJvmPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains

class HtmxComponentsContractTest {
    @Test
    fun componentsRenderEveryMethodAndOptionalAttribute() {
        val html = EnhancedJvmPlatformRenderer().renderComposableRoot {
            HtmxButton("get", "/get", "get")
            HtmxButton("post", "/post", "POST", "#target", "outerHTML", "click", "#busy", "Sure?")
            HtmxButton("put", "/put", "put")
            HtmxButton("delete", "/delete", "delete")
            HtmxContainer("container", "/load", loadingText = "waiting")
            HtmxForm("/form-get", "get") { Text("form-get") }
            HtmxForm("/form-post", "post") { Text("form-post") }
            HtmxForm("/form-put", "put") { Text("form-put") }
            HtmxForm("/form-delete", "delete") { Text("form-delete") }
            HtmxIndicator("busy", "busy")
            HtmxTrigger("revealed", "/trigger-get", "get") { Text("trigger-get") }
            HtmxTrigger("load", "/trigger-post", "post") { Text("trigger-post") }
            HtmxTrigger("click", "/trigger-put", "put") { Text("trigger-put") }
            HtmxTrigger("click", "/trigger-delete", "delete") { Text("trigger-delete") }
            HtmxPolling("/poll", 750) { Text("polling") }
        }
        listOf("hx-get", "hx-post", "hx-put", "hx-delete", "hx-target", "hx-swap", "hx-trigger", "hx-indicator", "hx-confirm").forEach {
            assertContains(html, "$it=")
        }
        assertContains(html, "hx-indicator=\"#busy\"")
        assertContains(html, "hx-confirm=\"Sure?\"")
        assertContains(html, "every 750ms")
        assertContains(html, "htmx-indicator")
        assertContains(html, "waiting")
    }
}

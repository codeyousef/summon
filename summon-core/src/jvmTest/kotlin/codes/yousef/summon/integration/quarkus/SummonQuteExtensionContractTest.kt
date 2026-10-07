package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.components.display.Text
import io.quarkus.qute.Engine
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SummonQuteExtensionContractTest {
    @Test
    fun registeredAndPropFactoriesRenderActualSummonMarkup() {
        QuteComponentRegistry.register("contract-greeting") { Text("Hello from Qute") }
        val registered = QuteComponentRegistry.renderComponent("contract-greeting")
        assertContains(registered, "Hello from Qute")
        assertContains(registered, "<span")
        assertEquals("", QuteComponentRegistry.renderComponent("contract-missing"))

        SummonQuteExtensions.registerComponent("contract-props") { props ->
            { Text("Hello ${props["name"]}") }
        }
        val props = SummonQuteExtensions.renderSummon("contract-props", mapOf("name" to "Ada"))
        assertContains(props, "Hello Ada")
        assertFailsWith<IllegalArgumentException> {
            SummonQuteExtensions.renderSummon("contract-missing")
        }
    }

    @Test
    fun namespaceResolverEvaluatesArgumentsReturnsRawHtmlAndEscapesContainerAttributes() {
        QuteComponentRegistry.register("contract-namespace") { Text("Namespace content") }
        val engine = Engine.builder().also { SummonQuteExtension.create { includeComments(true) }.accept(it) }.build()

        val html = engine.parse(
            "{summon:component('contract-namespace')}|" +
                "{summon:isComponent('contract-namespace')}|" +
                "{summon:isComponent('missing')}|" +
                "{summon:withContainer('contract-namespace','a\"<','x&y')}",
        ).render()

        assertContains(html, "id=\"a&quot;&lt;\"")
        assertContains(html, "Namespace content")
        assertContains(html, "|true|false|")
        assertContains(html, "class=\"x&amp;y\"")
    }

    @Test
    fun companionHelpersRenderComposableFunctionsAndRejectArbitraryObjects() {
        val component = { Text("Direct component") }
        assertContains(SummonQuteExtension.renderComponent(component), "Direct component")
        assertContains(SummonQuteExtension.templateValue(component).get(), "Direct component")
        assertFailsWith<IllegalArgumentException> { SummonQuteExtension.renderComponent(42) }

        val config = SummonQuteExtension.Config()
        assertEquals(config, config.usePrettyPrinting(false))
        assertEquals(config, config.includeComments(true))
        assertEquals(false, config.usePrettyPrinting)
        assertEquals(true, config.includeComments)
    }
}

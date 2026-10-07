package codes.yousef.summon.integration.springboot

import codes.yousef.summon.components.display.Text
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebFluxStreamingContractTest {
    @Test
    fun flowFluxAndPublisherRenderComposableContentInBoundedEscapedChunks() = runBlocking {
        val chunks = WebFluxSupport.renderToFlow("unsafe <&>\"'", chunkSize = 7) {
            Text("streamed <content>")
        }.toList()
        val html = chunks.joinToString("")
        assertTrue(chunks.size > 3)
        assertContains(html, "<title>unsafe &lt;&amp;&gt;&quot;&#39;</title>")
        assertContains(html, "streamed &lt;content&gt;")
        assertFalse(html.contains("<title>unsafe <"))

        val flux = WebFluxSupport.renderToFlux(chunkSize = 64) { Text("flux") }
        assertContains(flux.collectList().block()!!.joinToString(""), "flux")
        val publisher: Publisher<String> = WebFluxSupport.renderToPublisher(chunkSize = 64) { Text("publisher") }
        assertContains(Flux.from(publisher).collectList().block()!!.joinToString(""), "publisher")
    }
}

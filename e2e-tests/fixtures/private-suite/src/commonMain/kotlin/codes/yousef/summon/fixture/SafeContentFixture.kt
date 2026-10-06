package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.CidReference
import codes.yousef.summon.components.display.CidResolver
import codes.yousef.summon.components.display.LocalObjectUrl
import codes.yousef.summon.components.display.SafeContainerKind
import codes.yousef.summon.components.display.SafeDocument
import codes.yousef.summon.components.display.SafeDocumentContent
import codes.yousef.summon.components.display.SafeDocumentNode
import codes.yousef.summon.components.display.SafeOutboundLink
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.state.mutableStateOf

class SafeContentFixture {
    private val showCid = mutableStateOf(true)
    private val releases = mutableStateOf(0)
    private val link = requireNotNull(
        SafeOutboundLink.parse("HTTPS://%65xample.com/final", setOf("example.com"))
    )
    private val cid = requireNotNull(CidReference.parse("cid:safe-image@example.test"))
    private val resolver = CidResolver {
        LocalObjectUrl.create("blob:https://fixture.invalid/safe-image") { releases.value++ }
    }

    @Composable
    fun Content() {
        val attackText = """
            <img src=https://tracker.invalid/pixel oNeRrOr
            =globalThis.__summonXss=1><a href=&#x6a;avascript:globalThis.__summonXss=2>bad</a>
            <img src=data:image/svg+xml,boom><a href=blob:https://evil.invalid/id>blob</a>
            <svg onload=globalThis.__summonXss=3><math><mtext>namespace</mtext></math></svg>
            <iframe srcdoc='<script>globalThis.__summonXss=4</script>'></iframe><form action=https://tracker.invalid></form>
            <input id=attributes name=children><b><i>malformed</b></i>
            <div style='background:url(https://tracker.invalid/css)'>@import https://tracker.invalid/import;</div>
            RTL שלום
        """.trimIndent()
        val nodes = mutableListOf<SafeDocumentNode>(
            SafeDocumentNode.Container(
                SafeContainerKind.PARAGRAPH,
                listOf(SafeDocumentNode.Text(attackText))
            ),
            SafeDocumentNode.Link(
                link,
                listOf(SafeDocumentNode.Text("<img src=x onerror=globalThis.__summonXss=5> deceptive label"))
            )
        )
        if (showCid.value) nodes += SafeDocumentNode.CidImage(cid, "Authorized local CID image")
        val document = SafeDocument.create(nodes, "Plaintext fallback: RTL שלום")

        Column {
            SafeDocumentContent(document, Modifier().attribute("data-testid", "safe-document"), resolver)
            Text("CID releases: ${releases.value}", Modifier().attribute("data-testid", "cid-releases"))
            Button(onClick = { showCid.value = false }, label = "Remove CID image")
        }
    }
}

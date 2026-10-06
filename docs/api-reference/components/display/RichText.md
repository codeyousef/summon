# RichText and safe documents

Summon 0.8 separates untrusted content from trusted author markup. Untrusted strings are always rendered as text; they are never parsed with `innerHTML` and never passed through a regex sanitizer.

## Plain untrusted text

```kotlin
RichText(message.plaintextBody)
```

`RichText(String)` preserves Unicode, including RTL text, and treats `<script>`, entity text, and malformed markup literally.

## Formatted untrusted content

Parse mail or imported content outside the renderer into the closed `SafeDocumentNode` model, then render it:

```kotlin
val destination = SafeOutboundLink.parse(
    raw = "https://docs.example.com/final",
    allowedHosts = setOf("docs.example.com")
) ?: error("Link is not approved")

val document = SafeDocument.create(
    nodes = listOf(
        SafeDocumentNode.Container(
            kind = SafeContainerKind.PARAGRAPH,
            children = listOf(SafeDocumentNode.Text("Hello שלום"))
        ),
        SafeDocumentNode.Link(
            target = destination,
            label = listOf(SafeDocumentNode.Text("Read documentation"))
        )
    ),
    plaintextFallback = "Hello שלום\nRead documentation (docs.example.com)"
)

RichText(document)
```

The policy identifier is `SafeDocument.POLICY_VERSION`. The tree is bounded to 10,000 nodes, depth 32, and 1 MiB of text. It can represent text, reviewed structural containers, HTTPS links with an explicitly approved normalized host, line breaks, and authorized CID images. It cannot represent scripts, forms, frames, embedded objects, event handlers, arbitrary attributes, remote images, stylesheets, SVG, MathML, or CSS URLs.

Outbound links always display the normalized final hostname and render with `noopener noreferrer`. Only `https` is accepted; `javascript:`, `data:`, `blob:`, user-info authorities, ports, controls, backslashes, malformed percent escapes, unapproved hosts, and non-ASCII host syntax are rejected.

## CID images

`CidResolver` is an object-capability boundary. It receives a validated `CidReference` and may return only a `LocalObjectUrl` whose URL starts with `blob:`. The URL is released through `DisposableEffect` when the image leaves composition.

```kotlin
val resolver = CidResolver { reference ->
    encryptedObjectStore.openLocalObjectUrl(reference.contentId)
}
RichText(document, cidResolver = resolver)
```

Do not resolve network URLs. Remote images remain off unless the application records explicit per-message consent and imports the result into an authorized local object. If no CID resolver is available, Summon renders the image's alt text. Original MIME bytes are intentionally absent from `SafeDocument`; retain them only in encrypted application storage.

## Trusted author markup

Generic raw HTML and inline SVG are application-author APIs, not sanitizers:

```kotlin
Html(TrustedHtml.fromAuthorCode("<canvas id=\"chart\"></canvas>"))
SvgIcon(TrustedSvg.fromAuthorCode(APP_ICON_SVG), ariaLabel = "Application")
```

`RawHtml { ... }` is also an explicit author-code boundary. Values from users, messages, storage, network responses, or URL parameters must never enter `TrustedHtml`, `TrustedSvg`, `TrustedCss`, `RawHtml`, or renderer raw-markup/style APIs. Summon does not provide string/boolean `sanitize` switches: the former regex filtering was not an HTML or CSS parser security boundary.

## Markdown

`Markdown(String)` renders plaintext in 0.8. Applications that need formatting must use a maintained, reviewed, bounded parser that outputs `SafeDocument`; converting Markdown to an HTML string is not supported for untrusted content.

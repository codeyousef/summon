package codes.yousef.summon.runtime

import kotlinx.browser.document
import org.w3c.dom.Element

/** Applies the server-issued CSP nonce to a runtime-created style element. */
internal fun applySummonStyleNonce(element: Element) {
    val nonce = document.querySelector("meta[name=\"summon-style-nonce\"]")
        ?.getAttribute("content")
        ?.takeIf { it.length == 43 && it.all { character -> character.isLetterOrDigit() || character == '-' || character == '_' } }
        ?: return
    element.setAttribute("nonce", nonce)
}

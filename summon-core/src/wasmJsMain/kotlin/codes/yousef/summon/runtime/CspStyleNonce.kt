package codes.yousef.summon.runtime

import kotlinx.browser.document
import org.w3c.dom.Element

internal fun applySummonStyleNonce(element: DOMElement) {
    val nonce = DOMProvider.document.querySelector("meta[name=\"summon-style-nonce\"]")
        ?.getAttribute("content")
        ?.takeIf(::isValidSummonStyleNonce)
        ?: return
    element.setAttribute("nonce", nonce)
}

internal fun applySummonNativeStyleNonce(element: Element) {
    val nonce = document.querySelector("meta[name=\"summon-style-nonce\"]")
        ?.getAttribute("content")
        ?.takeIf(::isValidSummonStyleNonce)
        ?: return
    element.setAttribute("nonce", nonce)
}

private fun isValidSummonStyleNonce(value: String): Boolean =
    value.length == 43 && value.all { it.isLetterOrDigit() || it == '-' || it == '_' }

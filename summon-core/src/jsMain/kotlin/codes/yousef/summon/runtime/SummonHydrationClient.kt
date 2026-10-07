package codes.yousef.summon.runtime

import codes.yousef.summon.hydration.Bootloader
import codes.yousef.summon.hydration.GlobalEventListener
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.JSON

/**
 * Client-side hydration for Summon components.
 * This script runs in the browser and activates server-rendered Summon components.
 */
object SummonHydrationClient {

    /**
     * Initializes Summon hydration when the page loads.
     * This function is automatically called when the JS bundle loads.
     */
    fun initialize() {
        // Check for performance metrics opt-in and initialize if enabled
        PerformanceMetrics.checkAndInitialize()

        perfMarkStart("initialize", HydrationPhase.INITIALIZATION)

        if (js("document.readyState === 'loading'") as Boolean) {
            perfMarkStart("dom-wait", HydrationPhase.DOM_READY)
            document.addEventListener("DOMContentLoaded", {
                perfMarkEnd("dom-wait")
                startHydration()
            })
        } else {
            startHydration()
        }

        perfMarkEnd("initialize")
    }

    private fun startHydration() {
        perfMarkStart("startHydration", HydrationPhase.INITIALIZATION)
        val dataElement = document.getElementById("summon-hydration-data")
        if (dataElement == null && pendingRecoveryMatchesLocation()) {
            perfMarkEnd("startHydration")
            recoverPublicShell("hydration metadata unavailable after recovery")
            return
        }
        val hydrationData = if (dataElement != null) {
            try {
                parseHydrationData((dataElement.textContent ?: "").trim())
            } catch (error: Exception) {
                perfMarkEnd("startHydration")
                recoverPublicShell(error.message ?: "invalid hydration metadata")
                return
            }
        } else {
            null
        }
        val publicState = try {
            loadPublicState()
        } catch (error: Exception) {
            perfMarkEnd("startHydration")
            recoverPublicShell(error.message ?: "invalid public hydration state")
            return
        }


        try {
            window.sessionStorage.removeItem(HYDRATION_RECOVERY_KEY)
            if (hydrationData != null) {
                window.asDynamic().__SUMMON_CALLBACK_CONTEXT__ = hydrationData.callbackContext
            }
            if (publicState != null) {
                window.asDynamic().__SUMMON_STATE__ = publicState
            }

            withPerfMetrics("global-event-listener-init", HydrationPhase.EVENT_SYSTEM) {
                GlobalEventListener.init()
            }
            withPerfMetrics("bootloader-process-queue", HydrationPhase.EVENT_REPLAY) {
                Bootloader.processQueue()
            }

            val rootElement: Element? =
                document.getElementById(SummonConstants.DEFAULT_ROOT_ELEMENT_ID)
                    ?: document.querySelector("[data-summon-hydration=\"root\"]")
            rootElement?.setAttribute("data-hydration-ready", "true")

            perfMarkEnd("startHydration")
            PerformanceMetrics.markHydrationComplete()
        } catch (error: Exception) {
            perfMarkEnd("startHydration")
            recoverPublicShell(error.message ?: "hydration initialization failed")
        }
    }

    private fun loadPublicState(): dynamic {
        val stateElement = document.getElementById("summon-state") ?: return null
        val encoded = (stateElement.textContent ?: "").trim()
        require(encoded.length <= MAX_PUBLIC_STATE_BASE64_CHARS) { "Public hydration state exceeds the size limit" }
        require(encoded.all { it.isLetterOrDigit() || it == '+' || it == '/' || it == '=' }) {
            "Public hydration state is not valid base64"
        }
        val json = window.atob(encoded)
        require(json.length <= MAX_PUBLIC_STATE_JSON_CHARS && json.all { it.code <= 0x7f }) {
            "Public hydration state is not bounded ASCII JSON"
        }
        return JSON.parse<dynamic>(json)
    }

    private fun parseHydrationData(jsonText: String): HydrationData {
        require(jsonText.isNotEmpty()) { "Hydration metadata is empty" }
        require(jsonText.length <= MAX_HYDRATION_JSON_CHARS) { "Hydration metadata exceeds the size limit" }
        require(jsonText.all { it.code <= 0x7f }) { "Hydration metadata must be ASCII" }

        val data = strictJson.decodeFromString<HydrationData>(jsonText)
        require(data.version == SUPPORTED_HYDRATION_VERSION) { "Unsupported hydration metadata version" }
        require(data.renderer == "jvm" || data.renderer == "js") { "Unsupported hydration renderer" }
        require(data.callbacks.size <= MAX_CALLBACKS) { "Hydration callback limit exceeded" }
        require(data.callbacks.distinct().size == data.callbacks.size) { "Duplicate hydration callbacks" }
        require(data.callbacks.all(::isValidCallbackId)) { "Invalid hydration callback ID" }
        require(
            data.callbacks.isEmpty() && data.callbackContext.isEmpty() ||
                isValidCapability(data.callbackContext)
        ) { "Invalid hydration callback capability" }
        require(data.timestamp >= 0) { "Invalid hydration timestamp" }
        return data
    }

    private fun isValidCallbackId(value: String): Boolean =
        value.length in 4..64 &&
            value.startsWith("cb-") &&
            value.drop(3).all { it in '0'..'9' || it in 'a'..'f' }

    private fun isValidCapability(value: String): Boolean =
        value.length in 43..64 &&
            value.all { it.isLetterOrDigit() || it == '-' || it == '_' }

    private fun recoverPublicShell(reason: String) {
        val root = document.getElementById(SummonConstants.DEFAULT_ROOT_ELEMENT_ID)
        root?.setAttribute("data-hydration-failed", "true")
        root?.setAttribute("data-hydration-ready", "false")
        window.asDynamic().__SUMMON_STATE__ = null
        window.asDynamic().__SUMMON_CALLBACK_CONTEXT__ = null
        SummonLogger.error("Hydration rejected; retaining the public shell: $reason")

        try {
            if (window.sessionStorage.getItem(HYDRATION_RECOVERY_KEY) == null) {
                window.sessionStorage.setItem(HYDRATION_RECOVERY_KEY, recoveryLocation())
                window.location.reload()
            }
        } catch (_: Exception) {
            // Storage can be unavailable in privacy modes. The inert public shell remains safe.
        }
    }

    private fun pendingRecoveryMatchesLocation(): Boolean = try {
        window.sessionStorage.getItem(HYDRATION_RECOVERY_KEY) == recoveryLocation()
    } catch (_: Exception) {
        false
    }

    private fun recoveryLocation(): String = window.location.pathname + window.location.search

    private val strictJson = Json {
        ignoreUnknownKeys = false
        isLenient = false
        explicitNulls = false
    }

    private const val MAX_HYDRATION_JSON_CHARS = 65_536
    private const val MAX_CALLBACKS = 512
    private const val SUPPORTED_HYDRATION_VERSION = 1
    private const val HYDRATION_RECOVERY_KEY = "summon-hydration-recovery"
    private const val MAX_PUBLIC_STATE_JSON_CHARS = 65_536
    private const val MAX_PUBLIC_STATE_BASE64_CHARS = 87_384

}

/**
 * Bounded, versioned hydration metadata. Unknown fields are rejected.

 * @property version The version value.
 * @property callbacks The callbacks value.
 * @property callbackContext The callback context value.
 * @property timestamp The timestamp value.
 * @property renderer The renderer value.
 * @property hydrationMarkers The hydration markers value.
 * @property seoCompatible The seo compatible value.
 */
@Serializable
data class HydrationData(
    val version: Int,
    val callbacks: List<String>,
    val callbackContext: String,
    val timestamp: Long,
    val renderer: String,
    val hydrationMarkers: Boolean,
    val seoCompatible: Boolean
)

/**
 * Entry point for the Summon hydration client.
 * This function is called when the script loads.
 */
@JsExport
fun main() {
    SummonHydrationClient.initialize()
}

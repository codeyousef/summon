package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier

/**
 * JavaScript-friendly facade for PlatformRenderer.
 * This class is exported to JavaScript and only exposes JavaScript-friendly methods.
 * It delegates to PlatformRenderer for the actual implementation.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class PlatformRendererFacade() {
    // The actual implementation that handles all the non-exportable types
    private val impl = PlatformRenderer()

    /**
     * Renders text with the given style string.
     */
    fun renderText(text: String, styleString: String = "") {
        val modifier = createModifierFromStyleString(styleString)
        impl.renderText(text, modifier)
    }

    /**
     * Renders a button with the given click handler, style string, and content.
     */
    fun renderButton(onClick: () -> Unit, styleString: String = "", content: @Composable () -> Unit) {
        val modifier = createModifierFromStyleString(styleString)
        impl.renderButton(onClick, modifier) {
            content()
        }
    }

    /**
     * Renders a text field with the given value, change handler, style string, and type.
     */
    fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        styleString: String = "",
        type: String = "text"
    ) {
        val modifier = createModifierFromStyleString(styleString)
        impl.renderTextField(value, onValueChange, modifier, type)
    }

    /**
     * Renders a div with the given style string and content.
     */
    fun renderDiv(styleString: String = "", content: @Composable () -> Unit) {
        val modifier = createModifierFromStyleString(styleString)
        impl.renderDiv(modifier) {
            content()
        }
    }

    /**
     * Renders a row with the given style string and content.
     */
    fun renderRow(styleString: String = "", content: @Composable () -> Unit) {
        val modifier = createModifierFromStyleString(styleString)
        impl.renderRow(modifier) {
            content()
        }
    }

    /**
     * Renders a column with the given style string and content.
     */
    fun renderColumn(styleString: String = "", content: @Composable () -> Unit) {
        val modifier = createModifierFromStyleString(styleString)
        impl.renderColumn(modifier) {
            content()
        }
    }

    /**
     * Renders a block with the given style string and content.
     */
    fun renderBlock(styleString: String = "", content: @Composable () -> Unit) {
        val modifier = createModifierFromStyleString(styleString)
        impl.renderBlock(modifier) {
            content()
        }
    }

    /**
     * Renders a composable function.
     */
    fun renderComposable(composable: @Composable () -> Unit) {
        impl.renderComposable(composable)
    }

    /**
     * JavaScript-friendly version of renderText with mangled method name.
     */
    @JsName("renderText_wa0k6q")
    fun renderTextMangled(text: String, styleString: String = "") {
        renderText(text, styleString)
    }

    /**
     * JavaScript-friendly version of renderRow with mangled method name.
     */
    @JsName("renderRow_e96qap")
    fun renderRowMangled(styleString: String = "", content: @Composable () -> Unit) {
        renderRow(styleString, content)
    }

    /**
     * JavaScript-friendly version of renderColumn with mangled method name.
     */
    @JsName("renderColumn_aiouiv")
    fun renderColumnMangled(styleString: String = "", content: @Composable () -> Unit) {
        renderColumn(styleString, content)
    }

    /**
     * JavaScript-friendly version of renderBlock with mangled method name.
     */
    @JsName("renderBlock_bncpua")
    fun renderBlockMangled(styleString: String = "", content: @Composable () -> Unit) {
        renderBlock(styleString, content)
    }

    /**
     * JavaScript-friendly version of renderButton with mangled method name.
     */
    @JsName("renderButton_ahh62j")
    fun renderButtonMangled(onClick: () -> Unit, styleString: String = "", content: @Composable () -> Unit) {
        renderButton(onClick, styleString, content)
    }

    /**
     * JavaScript-friendly version of renderTextField with mangled method name.
     */
    @JsName("renderTextField_w72mnb")
    fun renderTextFieldMangled(
        value: String,
        onValueChange: (String) -> Unit,
        styleString: String = "",
        type: String = "text"
    ) {
        renderTextField(value, onValueChange, styleString, type)
    }

    /**
     * Helper method to create a Modifier from a style string.
     */
    private fun createModifierFromStyleString(styleString: String): Modifier {
        // Parse the style string and create a Modifier
        val styles = mutableMapOf<String, String>()
        if (js("styleString.length > 0") as Boolean) {
            styleString.split(";").forEach { style ->
                val parts = style.split(":")
                if (parts.size == 2) {
                    val property = parts[0].trim()
                    val value = parts[1].trim()
                    styles[property] = value
                }
            }
        }
        return Modifier(styles)
    }
}

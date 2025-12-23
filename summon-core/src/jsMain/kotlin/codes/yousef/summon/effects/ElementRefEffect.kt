package codes.yousef.summon.effects

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.remember
import org.w3c.dom.Element

/**
 * Creates an ElementRef that can be used to access DOM elements.
 *
 * Usage:
 * ```
 * @Composable
 * fun MyComponent() {
 *     val elementRef = useElementRef()
 *
 *     // Use the ref with a modifier
 *     Div(modifier = Modifier().ref(elementRef)) {
 *         Text("Hello")
 *     }
 *
 *     // Access the DOM element
 *     LaunchedEffect(Unit) {
 *         val element = elementRef.getElement()
 *         // Do something with the element
 *     }
 * }
 * ```
 */
@Composable
fun useElementRef(): ElementRef {
    return remember { ElementRef() }
}

/**
 * Extension function to attach an ElementRef to a modifier.
 * This allows components to capture their DOM element reference.
 *
 * @param ref The ElementRef to attach
 * @return The modified Modifier
 */
fun Modifier.ref(ref: ElementRef): Modifier {
    return this.attribute("data-element-ref", ref.getId())
        .also {
            // Store the ref in a way that the renderer can access it
            // This would typically be done through a composition local or similar mechanism
            // For now, we'll use a simple attribute that the renderer can look for
        }
}

/**
 * Extension function to set a callback when the element is attached.
 *
 * @param callback The callback to invoke with the DOM element
 * @return The modified Modifier
 */
fun Modifier.onElementAttached(callback: (Element) -> Unit): Modifier {
    return this.attribute("data-on-attached", "true")
        .also {
            // Store the callback in a way that the renderer can access it
            // This would typically be done through a composition local or similar mechanism
        }
}
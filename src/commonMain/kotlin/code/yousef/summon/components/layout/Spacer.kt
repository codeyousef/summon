package code.yousef.summon.components.layout

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * A layout element that takes up space but does not have any content.
 *
 * Spacer can be used to add space between other composables or to fill
 * remaining space in layouts. The size of the spacer is determined by
 * the provided modifier.
 *
 * @param modifier The modifier to be applied to the spacer
 */
@Composable
fun Spacer(modifier: Modifier = Modifier()) {
    val renderer = getPlatformRenderer()
    renderer.renderSpacer(modifier)
} 
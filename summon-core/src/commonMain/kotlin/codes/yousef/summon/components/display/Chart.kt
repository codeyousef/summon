package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Renders a chart using a charting library.
 *
 * @param type The type of chart (e.g., "bar", "line", "pie")
 * @param dataJson The chart data in JSON format
 * @param optionsJson Optional chart configuration options in JSON format
 * @param modifier The modifier to apply to this component
 */
@Composable
fun Chart(
    type: String,
    dataJson: String,
    optionsJson: String? = null,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderChart(type, dataJson, optionsJson, modifier)
}

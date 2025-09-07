package code.yousef.summon.modifier

import code.yousef.summon.components.layout.Alignment

/**
 * Sets the vertical alignment of items in a Row.
 *
 * @param alignment The vertical alignment value as an Alignment.Vertical enum
 * @return A new Modifier with the vertical alignment style added
 */
fun Modifier.verticalAlignment(alignment: Alignment.Vertical): Modifier =
    when (alignment) {
        Alignment.Vertical.Top -> alignItems(AlignItems.FlexStart)
        Alignment.Vertical.CenterVertically -> alignItems(AlignItems.Center)
        Alignment.Vertical.Bottom -> alignItems(AlignItems.FlexEnd)
    }

/**
 * Sets the horizontal alignment of items in a Column.
 *
 * @param alignment The horizontal alignment value as an Alignment.Horizontal enum
 * @return A new Modifier with the horizontal alignment style added
 */
fun Modifier.horizontalAlignment(alignment: Alignment.Horizontal): Modifier =
    when (alignment) {
        Alignment.Horizontal.Start -> alignItems(AlignItems.FlexStart)
        Alignment.Horizontal.CenterHorizontally -> alignItems(AlignItems.Center)
        Alignment.Horizontal.End -> alignItems(AlignItems.FlexEnd)
    }
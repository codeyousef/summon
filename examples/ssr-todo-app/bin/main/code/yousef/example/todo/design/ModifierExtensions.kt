package code.yousef.example.todo.design

import codes.yousef.summon.modifier.*

// Design system specific modifiers that use our type-safe enums

// Type aliases for convenience at package level
typealias TypographyTextSize = TextSize
typealias TypographyFontWeight = FontWeight

object ModifierExtensions {
    // Spacing modifier extensions using core library functions
    fun Modifier.spacing(value: Spacing): Modifier = this
        .padding(value.value)

    fun Modifier.paddingHorizontal(value: Spacing): Modifier = this
        .paddingLeft(value.value)
        .paddingRight(value.value)

    fun Modifier.paddingVertical(value: Spacing): Modifier = this
        .paddingTop(value.value)
        .paddingBottom(value.value)

    fun Modifier.marginVertical(value: Spacing): Modifier = this
        .marginTop(value.value)
        .marginBottom(value.value)

    // MaxWidth modifier extension (using our enum)
    fun Modifier.containerWidth(value: MaxWidth): Modifier = this
        .style("width", "100%")
        .style("max-width", value.value)
        .style("margin-left", "auto")
        .style("margin-right", "auto")

    // Shadow and radius modifier extensions using core library functions
    fun Modifier.shadow(value: Shadow): Modifier = this
        .boxShadow(value.value)

    fun Modifier.radius(value: BorderRadius): Modifier = this
        .borderRadius(value.value)

    // Typography modifiers using core library functions
    fun Modifier.typography(size: TextSize, weight: FontWeight = FontWeight.NORMAL): Modifier = this
        .fontSize(size.value)
        .fontWeight(weight.value)

    // Button size modifier using core library functions
    fun Modifier.buttonSize(size: ButtonSize): Modifier = this
        .padding(size.padding)
        .fontSize(size.fontSize)
        .minWidth(size.minWidth)
}
package codes.yousef.summon.modifier

@Deprecated(
    message = "LayoutModifierExtras is kept for backwards compatibility only. Import LayoutModifiers.* instead."
)
object LayoutModifierExtras {
    @Deprecated(
        message = "Use LayoutModifiers.position instead.",
        replaceWith = ReplaceWith(
            expression = "this.position(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.position"]
        )
    )
    fun Modifier.position(value: String): Modifier =
        LayoutModifiers.run { this@position.position(value) }

    @Deprecated(
        message = "Use LayoutModifiers.top instead.",
        replaceWith = ReplaceWith(
            expression = "this.top(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.top"]
        )
    )
    fun Modifier.top(value: String): Modifier =
        LayoutModifiers.run { this@top.top(value) }

    @Deprecated(
        message = "Use LayoutModifiers.right instead.",
        replaceWith = ReplaceWith(
            expression = "this.right(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.right"]
        )
    )
    fun Modifier.right(value: String): Modifier =
        LayoutModifiers.run { this@right.right(value) }

    @Deprecated(
        message = "Use LayoutModifiers.bottom instead.",
        replaceWith = ReplaceWith(
            expression = "this.bottom(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.bottom"]
        )
    )
    fun Modifier.bottom(value: String): Modifier =
        LayoutModifiers.run { this@bottom.bottom(value) }

    @Deprecated(
        message = "Use LayoutModifiers.left instead.",
        replaceWith = ReplaceWith(
            expression = "this.left(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.left"]
        )
    )
    fun Modifier.left(value: String): Modifier =
        LayoutModifiers.run { this@left.left(value) }

    @Deprecated(
        message = "Use LayoutModifiers.flexDirection instead.",
        replaceWith = ReplaceWith(
            expression = "this.flexDirection(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.flexDirection"]
        )
    )
    fun Modifier.flexDirection(value: String): Modifier =
        LayoutModifiers.run { this@flexDirection.flexDirection(value) }

    @Deprecated(
        message = "Use LayoutModifiers.display instead.",
        replaceWith = ReplaceWith(
            expression = "this.display(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.display"]
        )
    )
    fun Modifier.display(value: String): Modifier =
        LayoutModifiers.run { this@display.display(value) }

    @Deprecated(
        message = "Use LayoutModifiers.gridTemplateColumns instead.",
        replaceWith = ReplaceWith(
            expression = "this.gridTemplateColumns(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.gridTemplateColumns"]
        )
    )
    fun Modifier.gridTemplateColumns(value: String): Modifier =
        LayoutModifiers.run { this@gridTemplateColumns.gridTemplateColumns(value) }

    @Deprecated(
        message = "Use LayoutModifiers.gridColumnGap instead.",
        replaceWith = ReplaceWith(
            expression = "this.gridColumnGap(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.gridColumnGap"]
        )
    )
    fun Modifier.gridColumnGap(value: String): Modifier =
        LayoutModifiers.run { this@gridColumnGap.gridColumnGap(value) }

    @Deprecated(
        message = "Use LayoutModifiers.gridRowGap instead.",
        replaceWith = ReplaceWith(
            expression = "this.gridRowGap(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.gridRowGap"]
        )
    )
    fun Modifier.gridRowGap(value: String): Modifier =
        LayoutModifiers.run { this@gridRowGap.gridRowGap(value) }

    @Deprecated(
        message = "Use LayoutModifiers.overflow instead.",
        replaceWith = ReplaceWith(
            expression = "this.overflow(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.overflow"]
        )
    )
    fun Modifier.overflow(value: String): Modifier =
        LayoutModifiers.run { this@overflow.overflow(value) }

    @Deprecated(
        message = "Use LayoutModifiers.fontWeight instead.",
        replaceWith = ReplaceWith(
            expression = "this.fontWeight(value)",
            imports = ["code.yousef.summon.modifier.LayoutModifiers.fontWeight"]
        )
    )
    fun Modifier.fontWeight(value: String): Modifier =
        LayoutModifiers.run { this@fontWeight.fontWeight(value) }
}

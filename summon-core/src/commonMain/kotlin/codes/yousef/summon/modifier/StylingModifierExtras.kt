package codes.yousef.summon.modifier

@Deprecated(
    message = "StylingModifierExtras is kept for backwards compatibility only. Import StylingModifiers.* instead."
)
object StylingModifierExtras {
    @Deprecated(
        message = "Use StylingModifiers.fontStyle instead.",
        replaceWith = ReplaceWith(
            expression = "this.fontStyle(value)",
            imports = ["codes.yousef.summon.modifier.StylingModifiers.fontStyle"]
        )
    )
    fun Modifier.fontStyle(value: String): Modifier =
        StylingModifiers.run { this@fontStyle.fontStyle(value) }

    @Deprecated(
        message = "Use StylingModifiers.textDecoration instead.",
        replaceWith = ReplaceWith(
            expression = "this.textDecoration(value)",
            imports = ["codes.yousef.summon.modifier.StylingModifiers.textDecoration"]
        )
    )
    fun Modifier.textDecoration(value: String): Modifier =
        StylingModifiers.run { this@textDecoration.textDecoration(value) }

    @Deprecated(
        message = "Use StylingModifiers.transform instead.",
        replaceWith = ReplaceWith(
            expression = "this.transform(value)",
            imports = ["codes.yousef.summon.modifier.StylingModifiers.transform"]
        )
    )
    fun Modifier.transform(value: String): Modifier =
        StylingModifiers.run { this@transform.transform(value) }
}

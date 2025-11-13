package codes.yousef.summon.test

/**
 * Lightweight component representations used by multiplatform tests.
 */
class Image(
    val src: String,
    val alt: String,
    val width: String? = null,
    val height: String? = null
)

class Button(
    val label: String,
    val onClick: (Any) -> Unit = {},
    val disabled: Boolean = false
)

class Text(
    val text: String
)

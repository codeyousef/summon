package fixture

fun classify(value: Int): String = when {
    value < 0 -> "negative"
    value == 0 -> "zero"
    else -> "positive"
}

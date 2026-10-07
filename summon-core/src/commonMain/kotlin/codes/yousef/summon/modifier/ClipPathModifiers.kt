package codes.yousef.summon.modifier

/**
 * Clip-path utilities extracted from `StylingModifiers`. They remain simple extension wrappers so
 * existing call sites keep working while the styling codebase becomes modular.
 */

fun Modifier.clipPath(value: String): Modifier =
    style("clip-path", value)

/**
 * Executes the clip circle operation.
 *
 * @param radius The radius value.
 * @return The resulting value.
 */
fun Modifier.clipCircle(radius: String = "50%"):
        Modifier = clipPath("circle($radius)")

/**
 * Executes the clip ellipse operation.
 *
 * @param radiusX The radius x value.
 * @param radiusY The radius y value.
 * @return The resulting value.
 */
fun Modifier.clipEllipse(radiusX: String = "50%", radiusY: String = "50%"): Modifier =
    clipPath("ellipse($radiusX $radiusY)")

/**
 * Executes the clip circle operation.
 *
 * @param radius The radius value.
 * @param centerX The center x value.
 * @param centerY The center y value.
 * @return The resulting value.
 */
fun Modifier.clipCircle(radius: String, centerX: String, centerY: String): Modifier =
    clipPath("circle($radius at $centerX $centerY)")

/**
 * Executes the clip ellipse operation.
 *
 * @param radiusX The radius x value.
 * @param radiusY The radius y value.
 * @param centerX The center x value.
 * @param centerY The center y value.
 * @return The resulting value.
 */
fun Modifier.clipEllipse(radiusX: String, radiusY: String, centerX: String, centerY: String): Modifier =
    clipPath("ellipse($radiusX $radiusY at $centerX $centerY)")

/**
 * Executes the clip polygon operation.
 *
 * @param points The points value.
 * @return The resulting value.
 */
fun Modifier.clipPolygon(points: List<String>): Modifier =
    clipPath("polygon(${points.joinToString(", ")})")

/**
 * Executes the clip polygon operation.
 *
 * @param points The points value.
 * @return The resulting value.
 */
fun Modifier.clipPolygon(vararg points: String): Modifier =
    clipPolygon(points.toList())

/**
 * Executes the clip inset operation.
 *
 * @param top The top value.
 * @param right The right value.
 * @param bottom The bottom value.
 * @param left The left value.
 * @param round The round value.
 * @return The resulting value.
 */
fun Modifier.clipInset(
    top: String,
    right: String = top,
    bottom: String = top,
    left: String = right,
    round: String? = null
): Modifier {
    val roundPart = if (round != null) " round $round" else ""
    return clipPath("inset($top $right $bottom $left$roundPart)")
}

/**
 * Executes the clip triangle up operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipTriangleUp(): Modifier =
    clipPolygon("50% 0%", "0% 100%", "100% 100%")

/**
 * Executes the clip triangle down operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipTriangleDown(): Modifier =
    clipPolygon("50% 100%", "0% 0%", "100% 0%")

/**
 * Executes the clip triangle left operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipTriangleLeft(): Modifier =
    clipPolygon("0% 50%", "100% 0%", "100% 100%")

/**
 * Executes the clip triangle right operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipTriangleRight(): Modifier =
    clipPolygon("100% 50%", "0% 0%", "0% 100%")

/**
 * Executes the clip diamond operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipDiamond(): Modifier =
    clipPolygon("50% 0%", "100% 50%", "50% 100%", "0% 50%")

/**
 * Executes the clip hexagon operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipHexagon(): Modifier =
    clipPolygon("30% 0%", "70% 0%", "100% 50%", "70% 100%", "30% 100%", "0% 50%")

/**
 * Executes the clip star operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipStar(): Modifier =
    clipPolygon(
        "50% 0%", "61% 35%", "98% 35%", "68% 57%", "79% 91%",
        "50% 70%", "21% 91%", "32% 57%", "2% 35%", "39% 35%"
    )

/**
 * Executes the clip arrow right operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipArrowRight(): Modifier =
    clipPolygon("0% 20%", "60% 20%", "60% 0%", "100% 50%", "60% 100%", "60% 80%", "0% 80%")

/**
 * Executes the clip arrow left operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipArrowLeft(): Modifier =
    clipPolygon("40% 0%", "40% 20%", "100% 20%", "100% 80%", "40% 80%", "40% 100%", "0% 50%")

/**
 * Executes the clip chevron right operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipChevronRight(): Modifier =
    clipPolygon("75% 0%", "100% 50%", "75% 100%", "25% 100%", "50% 50%", "25% 0%")

/**
 * Executes the clip chevron left operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipChevronLeft(): Modifier =
    clipPolygon("25% 0%", "0% 50%", "25% 100%", "75% 100%", "50% 50%", "75% 0%")

/**
 * Executes the clip message bubble operation.
 *
 * @return The resulting value.
 */
fun Modifier.clipMessageBubble(): Modifier =
    clipPolygon("0% 0%", "100% 0%", "100% 75%", "75% 75%", "75% 100%", "50% 75%", "0% 75%")

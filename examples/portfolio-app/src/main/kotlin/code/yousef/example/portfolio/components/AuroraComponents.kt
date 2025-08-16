package code.yousef.example.portfolio.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.display.*
import code.yousef.summon.components.input.*
import code.yousef.summon.modifier.*

/**
 * Simplified Aurora-themed components for basic Summon functionality.
 * Uses only string-based CSS styling that's currently available.
 */

/**
 * Simple Aurora Portal Beam
 */
@Composable
fun AuroraPortalBeam() {
    Box(
        modifier = Modifier()
            .position(Position.Fixed)
            .style("left", "50%")
            .style("top", "0")
            .width("8px")
            .height("100vh")
            .background("linear-gradient(180deg, rgba(240, 249, 255, 0.9) 0%, rgba(56, 178, 172, 0.7) 25%, rgba(255, 255, 255, 1) 50%, rgba(44, 82, 130, 0.7) 75%, rgba(155, 44, 44, 0.6) 100%)")
            .transform("translateX(-50%)")
            .zIndex(10)
            .style("pointer-events", "none")
    ) {}
}

/**
 * Simple Floating Content Shard with basic glass effects
 */
@Composable
fun FloatingContentShard(
    modifier: Modifier = Modifier(),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .background("rgba(255, 255, 255, 0.05)")
            .style("backdrop-filter", "blur(8px)")
            .border("1px", "solid", "rgba(255, 255, 255, 0.2)")
            .borderRadius("16px")
            .padding("24px")
            .boxShadow("0 8px 32px rgba(56, 178, 172, 0.3)")
            .style("transition", "all 0.3s ease")
            .hover(mapOf(
                "transform" to "translateY(-4px)",
                "background" to "rgba(255, 255, 255, 0.08)",
                "box-shadow" to "0 12px 40px rgba(56, 178, 172, 0.4)"
            )),
        onClick = onClick
    ) {
        content()
    }
}

/**
 * Simple Navigation Bar
 */
@Composable
fun SimpleNavigationBar(
    currentPath: String
) {
    Box(
        modifier = Modifier()
            .position(Position.Fixed)
            .style("top", "40px")
            .style("left", "50%")
            .transform("translateX(-50%)")
            .zIndex(100)
    ) {
        Row(
            modifier = Modifier()
                .background("rgba(255, 255, 255, 0.05)")
                .style("backdrop-filter", "blur(8px)")
                .border("1px", "solid", "rgba(255, 255, 255, 0.2)")
                .borderRadius("20px")
                .padding("10px", "20px")
        ) {
            SimpleNavigationLink("Home", "/", currentPath)
            Spacer(modifier = Modifier().width("20px"))
            SimpleNavigationLink("Portfolio", "/portfolio", currentPath)
            Spacer(modifier = Modifier().width("20px"))
            SimpleNavigationLink("Services", "/services", currentPath)
            Spacer(modifier = Modifier().width("20px"))
            SimpleNavigationLink("Journal", "/journal", currentPath)
            Spacer(modifier = Modifier().width("20px"))
            SimpleNavigationLink("Contact", "/contact", currentPath)
        }
    }
}

@Composable
private fun SimpleNavigationLink(
    text: String,
    path: String,
    currentPath: String
) {
    val isActive = currentPath == path
    val linkColor = if (isActive) "#ffffff" else "rgba(56, 178, 172, 0.9)"
    
    Text(
        text = text,
        modifier = Modifier()
            .color(linkColor)
            .style("text-decoration", "none")
            .fontWeight("500")
            .cursor("pointer")
            .style("transition", "all 0.3s ease")
            .hover(mapOf(
                "color" to "#ffffff",
                "text-shadow" to "0 0 10px rgba(255, 255, 255, 0.5)"
            ))
            .attribute("href", path)
            .attribute("style", "display: inline-block;")
    )
}

/**
 * Simple Project Card
 */
@Composable
fun SimpleProjectCard(
    title: String,
    description: String,
    technologies: List<String>,
    modifier: Modifier = Modifier()
) {
    FloatingContentShard(
        modifier = modifier
    ) {
        Column {
            // Title
            Text(
                text = title,
                modifier = Modifier()
                    .fontSize("1.4rem")
                    .fontWeight("600")
                    .marginBottom("12px")
                    .color("#ffffff")
            )
            
            // Description
            Text(
                text = description,
                modifier = Modifier()
                    .color("#B0B0B0")
                    .lineHeight("1.6")
                    .marginBottom("16px")
            )
            
            // Technology badges
            if (technologies.isNotEmpty()) {
                Row(
                    modifier = Modifier()
                        .style("gap", "8px")
                        .style("flex-wrap", "wrap")
                        .marginBottom("16px")
                ) {
                    technologies.forEach { tech ->
                        SimpleTechBadge(tech)
                    }
                }
            }
        }
    }
}

/**
 * Simple Aurora Layout
 */
@Composable
fun SimpleAuroraLayout(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier()
            .background("radial-gradient(ellipse at center, #0f0f23 0%, #000000 100%)")
            .minHeight("100vh")
            .margin("0")
            .style("overflow-x", "hidden")
            .style("font-family", "'Inter', system-ui, sans-serif")
            .color("#ffffff")
    ) {
        // Aurora Portal Beam
        AuroraPortalBeam()
        
        // Content Container
        Box(
            modifier = Modifier()
                .position(Position.Relative)
                .minHeight("100vh")
                .padding("140px", "20px", "40px", "20px")
                .zIndex(10)
                .maxWidth("1200px")
                .margin("0", "auto")
        ) {
            content()
        }
    }
}

/**
 * Simple Orbital Layout
 */
@Composable
fun SimpleOrbitalLayout(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier()
            .position(Position.Relative)
            .width("100%")
            .minHeight("60vh")
            .display(Display.Flex)
            .style("flex-direction", "column")
            .justifyContent(JustifyContent.Center)
            .style("align-items", "center")
    ) {
        content()
    }
}

/**
 * Simple Aurora Button
 */
@Composable
fun SimpleAuroraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier()
) {
    Button(
        label = text,
        onClick = onClick,
        modifier = modifier
            .padding("12px", "24px")
            .background("rgba(56, 178, 172, 0.2)")
            .border("1px", "solid", "rgba(56, 178, 172, 0.5)")
            .borderRadius("6px")
            .color("rgba(56, 178, 172, 0.95)")
            .fontSize("1rem")
            .fontWeight("600")
            .cursor("pointer")
            .style("transition", "all 0.3s ease")
            .hover(mapOf(
                "background" to "rgba(56, 178, 172, 0.3)",
                "transform" to "translateY(-2px)",
                "box-shadow" to "0 4px 20px rgba(56, 178, 172, 0.4)"
            ))
    )
}

/**
 * Simple Aurora TextField (kept as reference, used in SummonPortfolioApp.kt instead)
 */
@Composable
fun SimpleAuroraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier()
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier
            .padding("12px", "16px")
            .background("rgba(255, 255, 255, 0.05)")
            .border("1px", "solid", "rgba(56, 178, 172, 0.3)")
            .borderRadius("6px")
            .color("rgba(255, 255, 255, 0.9)")
            .fontSize("1rem")
            .style("::placeholder", "color: rgba(255, 255, 255, 0.5)")
    )
}

/**
 * Simple Aurora TextArea (kept as reference, used in SummonPortfolioApp.kt instead)
 */
@Composable
fun SimpleAuroraTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    rows: Int = 4,
    modifier: Modifier = Modifier()
) {
    TextArea(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        rows = rows,
        modifier = modifier
            .padding("12px", "16px")
            .background("rgba(255, 255, 255, 0.05)")
            .border("1px", "solid", "rgba(56, 178, 172, 0.3)")
            .borderRadius("6px")
            .color("rgba(255, 255, 255, 0.9)")
            .fontSize("1rem")
            .style("resize", "vertical")
            .style("::placeholder", "color: rgba(255, 255, 255, 0.5)")
    )
}

/**
 * Simple Aurora Heading
 */
@Composable
fun SimpleAuroraHeading(
    text: String,
    level: Int = 1,
    modifier: Modifier = Modifier()
) {
    val fontSize = when (level) {
        1 -> "2.5rem"
        2 -> "2rem"
        3 -> "1.5rem"
        else -> "1.2rem"
    }
    
    Text(
        text = text,
        modifier = modifier
            .fontSize(fontSize)
            .fontWeight("700")
            .background("linear-gradient(135deg, #38B2AC 0%, #2C5282 100%)")
            .style("-webkit-background-clip", "text")
            .style("-webkit-text-fill-color", "transparent")
            .style("background-clip", "text")
    )
}

/**
 * Simple Tech Badge
 */
@Composable
fun SimpleTechBadge(text: String) {
    Text(
        text = text,
        modifier = Modifier()
            .padding("4px", "8px")
            .background("rgba(56, 178, 172, 0.2)")
            .border("1px", "solid", "rgba(56, 178, 172, 0.4)")
            .borderRadius("12px")
            .fontSize("0.8rem")
            .color("rgba(56, 178, 172, 0.9)")
    )
}

/**
 * Simple Project Link
 */
@Composable
fun SimpleProjectLink(
    text: String,
    href: String,
    colorStr: String = "#38B2AC"
) {
    Text(
        text = text,
        modifier = Modifier()
            .padding("6px", "12px")
            .background("rgba(56, 178, 172, 0.1)")
            .border("1px", "solid", colorStr)
            .borderRadius("6px")
            .color(colorStr)
            .style("text-decoration", "none")
            .fontSize("0.85rem")
            .cursor("pointer")
            .attribute("href", href)
            .attribute("target", "_blank")
            .style("transition", "all 0.3s ease")
            .hover(mapOf(
                "background" to "rgba(56, 178, 172, 0.2)",
                "transform" to "translateY(-1px)"
            ))
    )
}



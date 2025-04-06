package code.yousef.summon.examples

import code.yousef.summon.annotation.Composable
import androidx.compose.runtime.* // Use androidx imports for remember/state
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.* // Import modifier extensions
// import code.yousef.summon.theme.border // Assuming border is a Modifier extension or needs theme integration
import code.yousef.summon.ui.Alignment // Check if Alignment enum/object is still valid
import code.yousef.summon.ui.unit.dp

/**
 * Example demonstrating the Card component using the new @Composable API.
 */
@Composable
fun CardExample() {
    var clickCount by remember { mutableStateOf(0) }

    // Example 1: Basic Card with Text
    Card(modifier = Modifier.padding(16.dp)) {
        Text("This is a simple card.")
    }

    // Example 2: Card with multiple children (Column)
    Card(
        modifier = Modifier.padding(16.dp), // .border(1.dp, "#cccccc") // TODO: Add border modifier if available
        elevation = "4px"
    ) {
        Column(/* horizontalAlignment = Alignment.CenterHorizontally */) { // TODO: Check alignment modifier
            Text("Card Title", modifier = Modifier.padding(bottom = 8.dp))
            Text("This card has multiple children arranged in a column.")
            Button(onClick = { /* Do nothing */ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Click Me")
            }
        }
    }

    // Example 3: Clickable Card
    Card(
        modifier = Modifier.padding(16.dp).clickable { clickCount++ }, // Use clickable modifier
        // onClick = { clickCount++ } // onClick might be part of CardData or handled via modifier
    ) {
        Column(/* horizontalAlignment = Alignment.CenterHorizontally */) { // TODO: Check alignment modifier
            Text("Clickable Card")
            Text("Clicked: $clickCount times")
        }
    }

    // Example 4: Card with custom border radius and elevation
    Card(
        modifier = Modifier.padding(16.dp),
        borderRadius = "12px",
        elevation = "8px"
    ) {
        Text("Card with custom styling.")
    }
}

@Composable
fun AdvancedCardLayouts() {
    // Example 5: Nested Cards
    Card(modifier = Modifier.padding(16.dp)) {
        Column {
            Text("Outer Card")
            Card(modifier = Modifier.padding(8.dp)/*.border(1.dp, "#eeeeee")*/) { // TODO: Add border
                Text("Inner Card 1")
            }
            Card(modifier = Modifier.padding(8.dp)/*.border(1.dp, "#eeeeee")*/) { // TODO: Add border
                Text("Inner Card 2")
            }
        }
    }

    // Example 6: Cards in a Column
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Cards in a Column:", modifier = Modifier.padding(bottom = 8.dp))
        (1..3).forEach { i ->
            Card(modifier = Modifier.padding(vertical = 4.dp)) {
                Text("Card $i in Column")
            }
        }
    }
}

@Composable
fun CardShowcase() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Card Component Showcase"/*, style = MaterialTheme.typography.h6 */) // TODO: Add typography

        CardExample()
        AdvancedCardLayouts()
    }
}

/**
 * Creates a basic card with a title and description.
 */
@Composable
fun BasicCard() { // Changed return type, made @Composable
    Card(
        modifier = Modifier
            .width("300px")
            .margin("16px")
    ) {
        // Content is now a lambda
        Text(
            text = "Basic Card Title",
            modifier = Modifier
                .fontSize("20px")
                .fontWeight("bold")
                .marginBottom("8px")
        )
        Text(
            text = "This is a basic card with some content. The card has default elevation and styling.",
            modifier = Modifier
                .color("#555555")
        )
    }
}

/**
 * Creates a card with custom styling.
 */
@Composable
fun CustomStyledCard() { // Changed return type, made @Composable
    Card(
        modifier = Modifier
            .width("300px")
            .margin("16px")
            .backgroundColor("#1976D2"),
        elevation = "4px",
        borderRadius = "8px"
    ) {
        // Content is now a lambda
        Text(
            text = "Custom Styled Card",
            modifier = Modifier
                .fontSize("18px")
                .fontWeight("bold")
                .color("#FFFFFF")
                .marginBottom("12px")
        )
        Text(
            text = "This card has custom background color, elevation, and border radius.",
            modifier = Modifier
                .color("#EEEEEE")
        )
    }
}

/**
 * Creates a clickable card.
 */
@Composable
fun ClickableCard() { // Changed return type, made @Composable
    var clickMessage by remember { mutableStateOf("Card has not been clicked.") }
    Card(
        modifier = Modifier
            .width("300px")
            .margin("16px")
            .backgroundColor("#F5F5F5")
            .cursor("pointer")
            .transitions(mapOf("background-color" to "0.2s ease")) // Use map for transitions
            .hover(Modifier.backgroundColor("#E0E0E0")) // Use Modifier for hover state
            .clickable { // Use clickable modifier
                clickMessage = "Card was clicked!"
                println(clickMessage)
            },
        elevation = "2px",
        borderRadius = "4px"
        // onClick handled by modifier now
    ) {
        // Content is now a lambda
        Text(
            text = "Clickable Card",
            modifier = Modifier
                .fontSize("18px")
                .fontWeight("bold")
                .marginBottom("8px")
        )
        Text(
            text = "Click this card to trigger an action. Try me! ($clickMessage)",
            modifier = Modifier
                .color("#555555")
        )
    }
}

/**
 * Creates a layout with multiple cards.
 */
@Composable
fun CardLayout() { // Changed return type, made @Composable
    Column {
        // Content is now a lambda
        Text(
            text = "Card Component Examples",
            modifier = Modifier
                .fontSize("24px")
                .fontWeight("bold")
                .margin("16px")
        )
        Row(
            modifier = Modifier
                .flexWrap("wrap")
        ) {
            // Call the @Composable helpers
            BasicCard()
            CustomStyledCard()
            ClickableCard()
        }
    }
} 
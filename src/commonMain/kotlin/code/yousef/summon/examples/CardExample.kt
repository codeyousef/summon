package code.yousef.summon.examples

import code.yousef.summon.*

/**
 * Example demonstrating the Card component.
 */
object CardExample {
    /**
     * Creates a basic card with a title and description.
     */
    fun basicCard(): Card {
        return Card(
            content = listOf(
                Text(
                    text = "Basic Card Title",
                    modifier = Modifier()
                        .fontSize("20px")
                        .fontWeight("bold")
                        .marginBottom("8px")
                ),
                Text(
                    text = "This is a basic card with some content. The card has default elevation and styling.",
                    modifier = Modifier()
                        .color("#555555")
                )
            ),
            modifier = Modifier()
                .width("300px")
                .margin("16px")
        )
    }
    
    /**
     * Creates a card with custom styling.
     */
    fun customStyledCard(): Card {
        return Card(
            content = listOf(
                Text(
                    text = "Custom Styled Card",
                    modifier = Modifier()
                        .fontSize("18px")
                        .fontWeight("bold")
                        .color("#FFFFFF")
                        .marginBottom("12px")
                ),
                Text(
                    text = "This card has custom background color, elevation, and border radius.",
                    modifier = Modifier()
                        .color("#EEEEEE")
                )
            ),
            modifier = Modifier()
                .width("300px")
                .margin("16px")
                .backgroundColor("#1976D2"),
            elevation = "4px",
            borderRadius = "8px"
        )
    }
    
    /**
     * Creates a clickable card.
     */
    fun clickableCard(): Card {
        return Card(
            content = listOf(
                Text(
                    text = "Clickable Card",
                    modifier = Modifier()
                        .fontSize("18px")
                        .fontWeight("bold")
                        .marginBottom("8px")
                ),
                Text(
                    text = "Click this card to trigger an action. Try me!",
                    modifier = Modifier()
                        .color("#555555")
                )
            ),
            modifier = Modifier()
                .width("300px")
                .margin("16px")
                .backgroundColor("#F5F5F5")
                .cursor("pointer")
                .transitions("background-color 0.2s ease")
                .hover(
                    Modifier()
                        .backgroundColor("#E0E0E0")
                ),
            elevation = "2px",
            borderRadius = "4px",
            onClick = {
                // In a real app, you would handle the click here
                println("Card was clicked!")
            }
        )
    }
    
    /**
     * Creates a layout with multiple cards.
     */
    fun cardLayout(): Column {
        return Column(
            content = listOf(
                Text(
                    text = "Card Component Examples",
                    modifier = Modifier()
                        .fontSize("24px")
                        .fontWeight("bold")
                        .margin("16px")
                ),
                Row(
                    content = listOf(
                        basicCard(),
                        customStyledCard(),
                        clickableCard()
                    ),
                    modifier = Modifier()
                        .flexWrap("wrap")
                )
            )
        )
    }
} 
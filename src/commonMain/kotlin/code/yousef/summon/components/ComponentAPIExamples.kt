package code.yousef.summon.components

import code.yousef.summon.components.display.Icon
import code.yousef.summon.components.display.Image
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.feedback.Alert
import code.yousef.summon.components.feedback.Progress
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.input.Select
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Grid
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.components.layout.Alignment
import code.yousef.summon.components.layout.Arrangement
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * This file demonstrates the consistent API usage for all core components.
 * Use this as a reference for the correct parameters and usage patterns.
 */
object ComponentAPIExamples {

    /**
     * Examples of display components
     */
    @Composable
    fun displayComponentExamples() {
        // Text component
        Text(
            text = "Hello World",
            modifier = Modifier().fontSize("16px").color("#333333")
        )
        
        // Icon component
        Icon(
            name = "settings", 
            size = "24px",
            modifier = Modifier().color("#555555")
        )
        
        // Image component
        Image(
            src = "/images/photo.jpg",
            alt = "A descriptive alt text",
            modifier = Modifier().width("200px").height("150px")
        )
    }
    
    /**
     * Examples of input components
     */
    @Composable
    fun inputComponentExamples() {
        // TextField component
        TextField(
            value = "Current text",
            onValueChange = { newValue -> /* Handle text change */ },
            label = "Username",
            placeholder = "Enter your username",
            modifier = Modifier().width("100%"),
            enabled = true,
            readOnly = false,
            isError = false,
            type = TextFieldType.Text
        )
        
        // Checkbox component - Use explicitly typed parameter to avoid ambiguity
        val checkHandler: (Boolean) -> Unit = { isChecked -> /* Handle state change */ }
        Checkbox(
            checked = true,
            onCheckedChange = checkHandler,
            modifier = Modifier(),
            enabled = true
        )
        
        // Select component
        Select(
            value = "option1",
            onValueChange = { newValue -> /* Handle selection change */ },
            options = listOf(
                SelectOption("option1", "Option 1"),
                SelectOption("option2", "Option 2")
            ),
            modifier = Modifier().width("200px"),
            enabled = true
        )
    }
    
    /**
     * Examples of layout components
     */
    @Composable
    fun layoutComponentExamples() {
        // Box component
        Box(
            modifier = Modifier().width("300px").height("200px").background("#f5f5f5")
        ) {
            Text(text = "Content inside box")
        }
        
        // Row component
        Row(
            modifier = Modifier().width("100%"),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalArrangement = Arrangement.Horizontal.SpaceBetween
        ) {
            Text(text = "Left item")
            Text(text = "Right item")
        }
        
        // Grid component
        Grid(
            columns = 2,
            modifier = Modifier().width("100%")
        ) {
            Text(text = "Grid item 1")
            Text(text = "Grid item 2")
            Text(text = "Grid item 3")
            Text(text = "Grid item 4")
        }
        
        // Card component
        Card(
            modifier = Modifier().width("320px").padding("16px")
        ) {
            Text(text = "Card title")
            Text(text = "Card content goes here")
        }
        
        // Spacer component
        Spacer(modifier = Modifier().height("16px"))
    }
    
    /**
     * Examples of feedback components
     */
    @Composable
    fun feedbackComponentExamples() {
        // Alert component
        Alert(
            title = "Success",
            message = "Your changes have been saved.",
            modifier = Modifier().margin("16px 0")
        )
        
        // Progress component
        Progress(
            progress = 0.75f, // 75%
            modifier = Modifier().width("100%").height("8px"),
            type = ProgressType.LINEAR,
            color = "#4285f4"
        )
    }
    
    /**
     * Examples of navigation components
     */
    @Composable
    fun navigationComponentExamples() {
        // Link component
        Link(
            href = "/dashboard",
            modifier = Modifier().color("#0066cc")
        ) {
            Text(text = "Go to Dashboard")
        }
    }
} 
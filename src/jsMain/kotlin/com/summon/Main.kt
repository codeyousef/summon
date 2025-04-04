package com.summon

import com.summon.examples.TextExample
import com.summon.examples.ImageExample

// External JS interfaces
external class Document {
    val body: HTMLElement?
    fun getElementById(id: String): HTMLElement?
    fun createElement(tagName: String): HTMLElement
    fun querySelectorAll(selectors: String): NodeList
}

external class HTMLElement {
    var id: String
    var className: String
    var innerHTML: String
    var textContent: String?
    var style: CSSStyleDeclaration
    fun appendChild(node: HTMLElement): HTMLElement
    fun addEventListener(type: String, listener: (Event) -> Unit)
}

external class CSSStyleDeclaration {
    var display: String
    var marginBottom: String
    var borderBottom: String
    var padding: String
    var cursor: String
    var color: String
    var fontWeight: String
    var margin: String
    var maxWidth: String
}

external class NodeList {
    val length: Int
    fun item(index: Int): HTMLElement?
}

external class Event {
    val currentTarget: HTMLElement?
    fun preventDefault()
}

external class Console {
    fun log(message: String)
}

external class Window {
    val document: Document
    val console: Console
}

// Global JS objects
external val window: Window
external val document: Document

/**
 * JS example demonstrating the Summon library.
 */
fun main() {
    // Set up the app when the DOM is loaded
    document.addEventListener("DOMContentLoaded") {
        setupApp()
    }
}

/**
 * Provides event listener functionality for Document
 */
private fun Document.addEventListener(type: String, listener: (Event) -> Unit) {
    // This is just a stub for the external function
}

/**
 * Sets up the application once the DOM is loaded.
 */
private fun setupApp() {
    // Get the app container element or create one if it doesn't exist
    val appContainer = document.getElementById("app") ?: createAppContainer()

    // Load and render our examples
    renderDemoPage(appContainer)
}

/**
 * Create the examples and renders them in tabs.
 */
private fun renderDemoPage(container: HTMLElement) {
    // Create a tab system for our examples
    val tabBar = document.createElement("div").apply {
        className = "tab-bar"
        style.display = "flex"
        style.marginBottom = "20px"
        style.borderBottom = "1px solid #ddd"
    }

    val contentContainer = document.createElement("div").apply {
        id = "tab-content"
    }

    // Add the tabs
    createTab("Basic UI", tabBar, "basic-ui", true)
    createTab("Form Example", tabBar, "form-example", false)
    createTab("Text Components", tabBar, "text-example", false)
    createTab("Image Examples", tabBar, "image-example", false)

    // Add tabs and content to the container
    container.appendChild(tabBar)
    container.appendChild(contentContainer)

    // Render the initial active tab
    renderBasicUI(contentContainer)

    // Set up tab click handlers
    setupTabHandlers(contentContainer)
}

/**
 * Sets up click handlers for the tabs.
 */
private fun setupTabHandlers(contentContainer: HTMLElement) {
    val tabItems = document.querySelectorAll(".tab-item")
    // Iterate through NodeList
    for (i in 0 until tabItems.length) {
        val tabItem = tabItems.item(i)
        tabItem?.addEventListener("click") { event ->
            event.preventDefault()

            // Remove active class from all tabs
            val allTabItems = document.querySelectorAll(".tab-item")
            for (j in 0 until allTabItems.length) {
                val item = allTabItems.item(j)
                if (item != null) {
                    val currentClass = item.className
                    item.className = currentClass.replace(" active", "")
                }
            }

            // Add active class to the clicked tab
            val target = event.currentTarget
            if (target != null) {
                target.className = target.className + " active"

                // Clear the content container
                contentContainer.innerHTML = ""

                // Render the appropriate content based on tab ID
                when (target.id) {
                    "basic-ui" -> renderBasicUI(contentContainer)
                    "form-example" -> renderFormExample(contentContainer)
                    "text-example" -> renderTextExample(contentContainer)
                    "image-example" -> renderImageExample(contentContainer)
                }
            }
        }
    }
}

/**
 * Renders the Text component example tab.
 */
private fun renderTextExample(container: HTMLElement) {
    // Empty the container
    container.innerHTML = ""

    // Create the text example using our TextExample class
    val textExample = TextExample().createTextDemo()

    // Render to string using our custom extension function
    val htmlOutput = textExample.renderToString()

    container.innerHTML = htmlOutput
}

/**
 * Creates a container div for the application if it doesn't exist.
 * @return The created container element
 */
private fun createAppContainer(): HTMLElement {
    val container = document.createElement("div")
    container.id = "app"
    container.style.maxWidth = "1200px"
    container.style.margin = "0 auto"
    container.style.padding = "20px"
    document.body?.appendChild(container)
    return container
}

/**
 * Creates a tab button in the tab bar.
 * @param label The label for the tab
 * @param container The container to add the tab to
 * @param id The ID for the tab
 * @param isActive Whether this tab should be active by default
 */
private fun createTab(label: String, container: HTMLElement, id: String, isActive: Boolean): HTMLElement {
    val tab = document.createElement("div")
    tab.id = id
    tab.className = "tab-item${if (isActive) " active" else ""}"
    tab.textContent = label
    tab.style.padding = "10px 15px"
    tab.style.cursor = "pointer"
    tab.style.borderBottom = if (isActive) "2px solid #2196F3" else "2px solid transparent"
    tab.style.color = if (isActive) "#2196F3" else "#555"
    tab.style.fontWeight = if (isActive) "500" else "normal"
    container.appendChild(tab)
    return tab
}

/**
 * Renders the basic UI example tab.
 */
private fun renderBasicUI(container: HTMLElement) {
    // Empty the container
    container.innerHTML = ""

    // Create a simple UI
    val example = Column(
        modifier = Modifier()
            .background("linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)")
            .padding("20px")
            .border("1px", "solid", "#ddd")
            .borderRadius("8px")
            .shadow(),
        content = listOf(
            Text(
                "Hello from Summon UI!",
                Modifier()
                    .padding("10px")
                    .color("#333")
                    .fontSize("24px")
                    .fontWeight("bold")
            ),
            Spacer("20px", true),
            Text(
                "This is a demo of the basic UI components.",
                Modifier()
                    .padding("10px")
                    .color("#666")
                    .fontSize("16px")
            )
        )
    )

    // Render the component to string
    val htmlOutput = example.renderToString()

    container.innerHTML = htmlOutput
}

/**
 * Renders the form example tab.
 */
private fun renderFormExample(container: HTMLElement) {
    // Empty the container
    container.innerHTML = ""

    // Create a form example
    val formExample = Form(
        onSubmit = { formData ->
            // In a real app, you would process the form data here
            window.console.log("Form submitted with data: $formData")
        },
        modifier = Modifier()
            .padding("20px")
            .background("#fff")
            .borderRadius("8px")
            .shadow(),
        content = listOf(
            Text(
                "Contact Form Example",
                Modifier()
                    .fontSize("24px")
                    .fontWeight("bold")
                    .margin("0 0 20px 0")
            ),
            TextField(
                state = mutableStateOf(""),
                label = "Name",
                placeholder = "Enter your name",
                validators = listOf()
            ),
            TextField(
                state = mutableStateOf(""),
                label = "Email",
                placeholder = "Enter your email",
                type = TextFieldType.Email,
                validators = listOf()
            ),
            TextField(
                state = mutableStateOf(""),
                label = "Message",
                placeholder = "Enter your message",
                validators = listOf(),
                modifier = Modifier().height("100px")
            ),
            Button(
                "Submit",
                onClick = { /* Form will handle submission */ },
                modifier = Modifier()
                    .background("#4CAF50")
                    .color("white")
                    .padding("10px 20px")
                    .borderRadius("4px")
                    .margin("20px 0 0 0")
            )
        )
    )

    // Render the component to string
    val htmlOutput = formExample.renderToString()

    container.innerHTML = htmlOutput
}

/**
 * Renders the Image component examples tab.
 */
private fun renderImageExample(container: HTMLElement) {
    // Empty the container
    container.innerHTML = ""
    
    // Create a container for multiple examples
    val examplesContainer = document.createElement("div")
    
    // Render the basic image example
    val basicImageExample = ImageExample.basicImage()
    val basicImageHtml = basicImageExample.renderToString()
    
    // Create a container for the basic image example
    val basicImageContainer = document.createElement("div")
    basicImageContainer.style.marginBottom = "40px"
    basicImageContainer.innerHTML = basicImageHtml
    
    // Render the multiple images example
    val multipleImagesExample = ImageExample.multipleImages()
    val multipleImagesHtml = multipleImagesExample.renderToString()
    
    // Create a container for the multiple images example
    val multipleImagesContainer = document.createElement("div")
    multipleImagesContainer.innerHTML = multipleImagesHtml
    
    // Add both examples to the container
    examplesContainer.appendChild(basicImageContainer)
    examplesContainer.appendChild(multipleImagesContainer)
    
    // Add the examples container to the main container
    container.appendChild(examplesContainer)
}

/**
 * Extension function to render a Composable to HTML string
 */
private fun Composable.renderToString(): String {
    // This is a simplified version - in reality, we'd need to implement this 
    // based on the kotlinx.html API or another HTML generation method
    val result = StringBuilder()
    result.append("<div>")

    // In a real implementation, this would use the compose method properly
    // For now, we'll use a stub that just returns some minimal HTML
    result.append("<!-- Placeholder for rendered component -->")

    result.append("</div>")
    return result.toString()
} 
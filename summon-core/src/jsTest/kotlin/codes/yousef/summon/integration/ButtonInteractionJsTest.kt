package codes.yousef.summon.integration

import codes.yousef.summon.js.testutil.ensureJsDom
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
import codes.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import kotlin.test.*

class ButtonInteractionJsTest {

    private lateinit var renderer: PlatformRenderer
    private lateinit var container: HTMLElement

    @BeforeTest
    fun setUp() {
        renderer = PlatformRenderer()
        ensureJsDom()
        container = document.createElement("div") as HTMLElement
        container.id = "js-button-test-root"
        document.body?.appendChild(container)
    }

    @AfterTest
    fun tearDown() {
        container.parentElement?.removeChild(container)
    }

    @Test
    fun todoButtonsMutateDomConsistently() {
        data class TodoItem(val id: Int, val label: String, var completed: Boolean)

        val todos = mutableListOf<TodoItem>()
        var nextId = 1

        lateinit var render: () -> Unit
        render = {
            renderer.startRecomposition()
            renderer.renderInto(container) {
                this@ButtonInteractionJsTest.renderer.renderHtmlTag(
                    "div",
                    Modifier()
                        .withAttribute("data-test-id", "todo-root")
                        .withAttribute("data-total", todos.size.toString())
                ) {
                    this@ButtonInteractionJsTest.renderer.renderButton(
                        onClick = {
                            val id = nextId++
                            todos.add(TodoItem(id = id, label = "Task $id", completed = false))
                            render()
                        },
                        modifier = Modifier().withAttribute("data-test-id", "todo-add")
                    ) {
                        this@ButtonInteractionJsTest.renderer.renderText("Add", Modifier())
                    }

                    this@ButtonInteractionJsTest.renderer.renderHtmlTag(
                        "div",
                        Modifier().withAttribute("data-test-id", "todo-list")
                    ) {
                        todos.forEach { todo ->
                            this@ButtonInteractionJsTest.renderer.renderHtmlTag(
                                "div",
                                Modifier()
                                    .withAttribute("data-test-id", "todo-row-${todo.id}")
                                    .withAttribute("data-completed", todo.completed.toString())
                            ) {
                                this@ButtonInteractionJsTest.renderer.renderText(
                                    todo.label,
                                    Modifier().withAttribute("data-test-id", "todo-label-${todo.id}")
                                )

                                this@ButtonInteractionJsTest.renderer.renderButton(
                                    onClick = {
                                        todo.completed = !todo.completed
                                        render()
                                    },
                                    modifier = Modifier().withAttribute("data-test-id", "todo-done-${todo.id}")
                                ) {
                                    val label = if (todo.completed) "Undo" else "Done"
                                    this@ButtonInteractionJsTest.renderer.renderText(label, Modifier())
                                }
                            }
                        }
                    }

                    this@ButtonInteractionJsTest.renderer.renderButton(
                        onClick = {
                            val iterator = todos.iterator()
                            while (iterator.hasNext()) {
                                if (iterator.next().completed) iterator.remove()
                            }
                            render()
                        },
                        modifier = Modifier().withAttribute("data-test-id", "todo-clear")
                    ) {
                        this@ButtonInteractionJsTest.renderer.renderText("Clear Completed", Modifier())
                    }
                }
            }
            renderer.endRecomposition()
        }

        render()

        fun rows(): List<HTMLElement> {
            val nodeList = container.querySelectorAll("[data-test-id^='todo-row-']")
            val elements = mutableListOf<HTMLElement>()
            for (index in 0 until nodeList.length) {
                val node = nodeList.item(index) as? HTMLElement ?: continue
                elements.add(node)
            }
            return elements
        }

        fun getElementByTestId(id: String): HTMLElement {
            val element = container.querySelector("[data-test-id='$id']") as? HTMLElement
            return element ?: error("Element with data-test-id '$id' not found")
        }

        assertEquals(0, rows().size, "Initial render should have no todo rows")

        getElementByTestId("todo-add").click()
        assertEquals(1, rows().size, "Clicking add should append a row")

        getElementByTestId("todo-add").click()
        assertEquals(2, rows().size, "Second add click should append another row")

        getElementByTestId("todo-done-1").click()
        assertEquals("true", getElementByTestId("todo-row-1").getAttribute("data-completed"))

        getElementByTestId("todo-clear").click()
        assertEquals(1, rows().size, "Clearing should remove completed rows")

        getElementByTestId("todo-done-2").click()
        assertEquals("true", getElementByTestId("todo-row-2").getAttribute("data-completed"))

        getElementByTestId("todo-clear").click()
        assertEquals(0, rows().size, "Clearing again should remove remaining rows")

        val root = container.querySelector("[data-test-id='todo-root']")
        assertNotNull(root)
        assertEquals("0", root.getAttribute("data-total"))
    }
}

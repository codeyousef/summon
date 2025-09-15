package code.yousef.example.todo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Simple standalone test to verify SSR works
 */
@Composable
fun SimplePage() {
    Column {
        Text("🎉 Summon SSR Works!")
        Text("This is server-side rendered content.")
        Text("No client-side JavaScript required.")
    }
}

fun main() {
    println("Testing Summon SSR...")
    
    val renderer = PlatformRenderer()
    val html = renderer.renderComposableRoot { SimplePage() }
    
    println("Generated HTML:")
    println("================")
    println(html)
    println("================")
    println("✅ SSR test successful!")
}
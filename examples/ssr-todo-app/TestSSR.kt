package codes.yousef.example.todo

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.runtime.PlatformRenderer

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
    // Using renderComposableRootWithHydration to test the new hydration approach
    val html = renderer.renderComposableRootWithHydration { SimplePage() }
    
    println("Generated HTML:")
    println("================")
    println(html)
    println("================")
    println("✅ SSR test successful!")
}
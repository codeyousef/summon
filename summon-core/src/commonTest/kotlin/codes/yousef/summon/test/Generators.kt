package codes.yousef.summon.test

import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

/**
 * Abstract representation of a UI component for property-based testing.
 */
sealed interface TestComponent

data class TestContainer(val children: List<TestComponent>) : TestComponent
data class TestText(val content: String) : TestComponent
data class TestButton(val label: String) : TestComponent

/**
 * Generators for Summon framework testing.
 */
object Generators {
    
    /**
     * Generates a random tree of components.
     * 
     * @param maxDepth Maximum depth of the tree
     */
    fun componentTree(maxDepth: Int = 3): Arb<TestComponent> {
        // Use alphanumeric strings to avoid HTML escaping issues in the test environment (Happy DOM)
        val safeStringArb = Arb.string(minSize = 1, maxSize = 20).filter { str -> 
            str.all { it.isLetterOrDigit() || it.isWhitespace() } && str.isNotBlank()
        }
        val textArb = safeStringArb.map { TestText(it) }
        val buttonArb = safeStringArb.map { TestButton(it) }
        val leafArb = Arb.choice(textArb, buttonArb)
        
        if (maxDepth <= 0) return leafArb
        
        // Recursive step: Create a container that holds children generated with maxDepth - 1
        // We use Arb.choice to decide whether to generate a leaf or a container at this level
        return Arb.choice(
            leafArb,
            Arb.list(componentTree(maxDepth - 1), 0..3).map { TestContainer(it) }
        )
    }
}

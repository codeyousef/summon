package codes.yousef.summon.test

import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
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
        val textArb = Arb.string().map { TestText(it) }
        val buttonArb = Arb.string().map { TestButton(it) }
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

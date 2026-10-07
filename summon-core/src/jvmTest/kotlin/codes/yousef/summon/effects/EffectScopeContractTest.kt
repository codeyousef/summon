package codes.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertEquals

class EffectScopeContractTest {
    @Test
    fun effectsExecuteInPriorityAndInsertionOrderAndCanBeCleared() {
        val calls = mutableListOf<String>()
        val scope = EffectScope()
        scope.addEffect({ calls += "normal-1" })
        scope.addEffect({ calls += "low" }, EffectPriority.LOW)
        scope.addEffect({ calls += "high" }, EffectPriority.HIGH)
        scope.addEffect({ calls += "normal-2" }, EffectPriority.NORMAL)
        scope.executeEffects()
        assertEquals(listOf("high", "normal-1", "normal-2", "low"), calls)
        scope.clearEffects()
        scope.executeEffects()
        assertEquals(4, calls.size)
    }
}

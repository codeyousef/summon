package codes.yousef.summon.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

fun ownedFlowBindingSample() {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val source = MutableStateFlow("initial")
    val binding = bindMutableStateFlow(source, scope)
    val events = MutableSharedFlow<String>()
    val eventBinding = bindSharedFlow(events, initialValue = "none", scope = scope)

    try {
        binding.state.value = "edited"
        check(source.value == "edited")
        check(eventBinding.state.value == "none")
    } finally {
        binding.dispose()
        eventBinding.dispose()
        scope.cancel()
    }
}

class DocumentationSamplesTest {
    @Test
    fun ownedFlowBinding() {
        ownedFlowBindingSample()
    }
}

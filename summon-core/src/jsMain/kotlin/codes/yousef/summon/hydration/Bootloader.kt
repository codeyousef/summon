package codes.yousef.summon.hydration

import kotlinx.browser.window

object Bootloader {
    fun processQueue() {
        val queue = window.asDynamic().__SUMMON_QUEUE__ as? Array<dynamic> ?: return
        
        queue.forEach { event ->
            val type = event.type as String
            val targetId = event.targetId as String
            val originalEvent = event.originalEvent
            
            GlobalEventListener.handleEvent(type, targetId, originalEvent)
        }
        
        window.asDynamic().Summon = true
    }
}

package code.yousef.example.todo.design

import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px

enum class MaxWidth(val value: String) {
    SM(640.px),      // Small screens
    MD(768.px),      // Medium screens
    LG(1024.px),     // Large screens (default container)
    XL(1280.px),     // Extra large
    FULL(100.percent)      // Full width
}
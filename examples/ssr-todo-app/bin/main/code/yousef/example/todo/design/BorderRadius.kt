package code.yousef.example.todo.design

import code.yousef.summon.extensions.px

enum class BorderRadius(val value: String) {
    NONE(0.px),
    SM(4.px),
    MD(8.px),
    LG(12.px),
    XL(16.px),
    FULL(9999.px)  // Pill shape
}
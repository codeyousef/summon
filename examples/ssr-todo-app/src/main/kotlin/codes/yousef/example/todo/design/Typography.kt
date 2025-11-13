package codes.yousef.example.todo.design

import codes.yousef.summon.extensions.px

enum class TextSize(val value: String) {
    XS(12.px),       // 0.75rem
    SM(14.px),       // 0.875rem
    BASE(16.px),     // 1rem (base)
    MD(18.px),       // 1.125rem
    LG(20.px),       // 1.25rem
    XL(24.px),       // 1.5rem
    XXL(30.px),      // 1.875rem
    XXXL(36.px),     // 2.25rem
    DISPLAY(48.px)   // 3rem
}

enum class FontWeight(val value: String) {
    NORMAL("400"),
    MEDIUM("500"),
    SEMIBOLD("600"),
    BOLD("700")
}
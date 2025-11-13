package code.yousef.example.todo.design

import codes.yousef.summon.extensions.px

enum class ButtonSize(
    val padding: String,
    val fontSize: String,
    val minWidth: String
) {
    SMALL("${6.px} ${12.px}", 14.px, 64.px),
    MEDIUM("${8.px} ${16.px}", 16.px, 80.px),
    LARGE("${12.px} ${24.px}", 18.px, 96.px)
}
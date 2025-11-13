package codes.yousef.example.todo.design

enum class SemanticColor(val lightValue: String, val darkValue: String) {
    BACKGROUND("#ffffff", "#1a1a1a"),
    SURFACE("#f5f5f5", "#2d2d2d"),
    PRIMARY("#2196f3", "#64b5f6"),
    SECONDARY("#757575", "#9e9e9e"),
    DANGER("#f44336", "#ef5350"),
    SUCCESS("#4caf50", "#66bb6a"),
    WARNING("#ff9800", "#ffa726"),
    TEXT_PRIMARY("rgba(0,0,0,0.87)", "rgba(255,255,255,0.87)"),
    TEXT_SECONDARY("rgba(0,0,0,0.6)", "rgba(255,255,255,0.6)"),
    TEXT_DISABLED("rgba(0,0,0,0.38)", "rgba(255,255,255,0.38)"),
    BORDER("#e0e0e0", "#424242")
}
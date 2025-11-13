package codes.yousef.summon.css

/**
 * Lightweight alias representing a CSS value string.
 *
 * Keeping this as a typealias preserves backwards compatibility with existing
 * APIs that accept raw strings while allowing future upgrades to wrap it in a
 * dedicated value class without churn.
 */
typealias CssValue = String

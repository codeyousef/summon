package codes.yousef.summon.runtime

/**
 * This file provides a typealias from runtime.Composable to annotation.Composable.
 *
 * The codebase is being consolidated to use a single Composable annotation,
 * and this typealias ensures backward compatibility with existing code that
 * imports from the runtime package.
 */

/**
 * Typealias to annotation.Composable for backward compatibility.
 * This allows existing code that imports from runtime to continue working.
 */
typealias Composable = code.yousef.summon.annotation.Composable 
package code.yousef.summon.annotation

/**
 * This file provides type aliases for standardized usage of the Composable annotation.
 *
 * The codebase previously had two separate annotation declarations:
 * 1. code.yousef.summon.annotation.Composable
 * 2. code.yousef.summon.runtime.Composable (which is now a typealias to annotation.Composable)
 *
 * These aliases ensure backward compatibility while consolidating to a single annotation.
 */

/**
 * The preferred way to use the Composable annotation in new code.
 *
 * Use this import pattern for new code:
 * import code.yousef.summon.annotation.StandardComposable as Composable
 */
typealias StandardComposable = Composable 
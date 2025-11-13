package codes.yousef.summon.runtime

/**
 * Console functions for WASM - single definition to avoid duplicates
 * All WASM files should import from this module instead of declaring their own externals
 */

// These are defined in WasmNativeInterfaces.kt
// Just re-export them here for convenience
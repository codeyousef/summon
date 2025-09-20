package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

/**
 * Console functions for WASM - single definition to avoid duplicates
 * All WASM files should import from this module instead of declaring their own externals
 */

// These are defined in WasmNativeInterfaces.kt
// Just re-export them here for convenience
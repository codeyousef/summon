package code.yousef.summon.security

import code.yousef.summon.runtime.wasmConsoleLog
import security.Authentication

actual object SecurityContextHolder {
    private var _authentication: Authentication? = null

    actual fun get(): Authentication? {
        wasmConsoleLog("SecurityContextHolder.get - WASM stub")
        return _authentication
    }

    actual fun set(value: Authentication?) {
        wasmConsoleLog("SecurityContextHolder.set - WASM stub")
        _authentication = value
    }

    actual fun remove() {
        wasmConsoleLog("SecurityContextHolder.remove - WASM stub")
        _authentication = null
    }
}
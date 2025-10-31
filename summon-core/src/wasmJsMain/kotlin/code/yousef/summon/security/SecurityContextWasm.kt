package code.yousef.summon.security

actual object SecurityContextHolder {
    private var _authentication: Authentication? = null

    actual fun get(): Authentication? {
        return _authentication
    }

    actual fun set(value: Authentication?) {
        _authentication = value
    }

    actual fun remove() {
        _authentication = null
    }
}

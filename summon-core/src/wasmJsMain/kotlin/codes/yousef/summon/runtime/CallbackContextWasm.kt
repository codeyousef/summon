package codes.yousef.summon.runtime

internal actual fun callbackContextKey(): Long = 0L

internal actual fun isCallbackDebugEnabled(): Boolean = false

internal actual fun generateCallbackCapability(): String = js(
    "(function(){var a=crypto.getRandomValues(new Uint8Array(32));var s='';for(var i=0;i<a.length;i++){s+=('0'+a[i].toString(16)).slice(-2);}return s;})()"
)

package codes.yousef.summon.runtime

import kotlin.js.Date

internal actual fun currentTimeMillis(): Long = Date.now().toLong()

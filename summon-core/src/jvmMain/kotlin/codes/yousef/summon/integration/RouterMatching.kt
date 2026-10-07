package codes.yousef.summon.integration

import codes.yousef.summon.routing.PageRegistry

/** Shared path normalization and matching for JVM server integrations. */
internal fun String.normalizeRouterBasePath(): String {
    if (isBlank() || this == "/") return "/"
    val withLeadingSlash = if (startsWith('/')) this else "/$this"
    return withLeadingSlash.trimEnd('/').ifBlank { "/" }
}

internal fun String.ensureRouterLeadingSlash(): String = when {
    isBlank() -> "/"
    startsWith('/') -> this
    else -> "/$this"
}

internal fun PageRegistry.hasRouterRouteFor(path: String): Boolean {
    val normalizedPath = path.ensureRouterLeadingSlash()
    val routes = getPages()
    if (routes.isEmpty()) return false
    return routes.keys.any { pattern -> routerPatternMatches(pattern, normalizedPath) }
}

internal fun routerPatternMatches(pattern: String, path: String): Boolean {
    val normalizedPattern = pattern.ensureRouterLeadingSlash()
    if (normalizedPattern == path) return true

    val patternSegments = normalizedPattern.trim('/').takeIf(String::isNotEmpty)?.split('/') ?: emptyList()
    val pathSegments = path.trim('/').takeIf(String::isNotEmpty)?.split('/') ?: emptyList()
    if (patternSegments.isEmpty()) return pathSegments.isEmpty()

    val catchAll = patternSegments.lastOrNull() == "*"
    if (!catchAll && patternSegments.size != pathSegments.size) return false
    if (catchAll && pathSegments.size < patternSegments.size - 1) return false

    patternSegments.forEachIndexed { index, segment ->
        if (segment == "*") return true
        val candidate = pathSegments.getOrNull(index) ?: return false
        if (!segment.startsWith(':') && segment != candidate) return false
    }
    return catchAll || patternSegments.size == pathSegments.size
}

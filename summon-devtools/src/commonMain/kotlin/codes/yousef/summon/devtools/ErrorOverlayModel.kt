package codes.yousef.summon.devtools

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Category exposed by the development overlay without retaining an arbitrary rejected value. */
enum class DevelopmentErrorCategory { /** The script error development error category option. */
                                      SCRIPT_ERROR,
                                      /** The unhandled rejection development error category option. */
                                      UNHANDLED_REJECTION,
                                      /** The error boundary development error category option. */
                                      ERROR_BOUNDARY }

/** Controls whether explicitly synthetic public text may be shown instead of a generic category. */
enum class DevelopmentErrorTextPolicy { /** The generic only development error text policy option. */
                                        GENERIC_ONLY,
                                        /** The synthetic public development error text policy option. */
                                        SYNTHETIC_PUBLIC }

/**
 * One validated generated-code frame from a browser stack. Positions are one-based.
 *
 * @property assetUrl The asset url value.
 * @property line The line value.
 * @property column The column value.
 */
data class GeneratedSourceLocation(val assetUrl: String, val line: Int, val column: Int)

/**
 * One verified original-source position resolved from the exact configured build map.
 *
 * @property source The source value.
 * @property line The line value.
 * @property column The column value.
 * @property buildId The build id value.
 */
data class MappedSourceLocation(
    val source: String,
    val line: Int,
    val column: Int,
    val buildId: String
)

/**
 * Generated and optional verified original positions for one error frame.
 *
 * @property generated The generated value.
 * @property mapped The mapped value.
 */
data class DevelopmentErrorFrame(
    val generated: GeneratedSourceLocation,
    val mapped: MappedSourceLocation?
)

/**
 * Bounded, display-safe error retained by the development overlay.
 *
 * @property category The category value.
 * @property text The text value.
 * @property frames The frames value.
 */
data class DevelopmentError(
    val category: DevelopmentErrorCategory,
    val text: String,
    val frames: List<DevelopmentErrorFrame>
)

/**
 * A source map supplied by trusted debug-build configuration for one exact generated asset/build.
 *
 * @property generatedAssetUrl The generated asset url value.
 * @property buildId The build id value.
 * @property json The json value.
 */
data class VerifiedSourceMapAsset(
    val generatedAssetUrl: String,
    val buildId: String,
    val json: String
)

/**
 * Validated source-link policy. No link is produced when a path or identity is not allowed.
 *
 * @property editorProtocol The editor protocol value.
 * @property workspaceRoot The workspace root value.
 * @property viewerOrigin The viewer origin value.
 * @property sourceRevision The source revision value.
 */
data class DevelopmentSourceLinkPolicy(
    val editorProtocol: String?,
    val workspaceRoot: String?,
    val viewerOrigin: String?,
    val sourceRevision: String?
)

/** Pure parser for bounded browser stack strings. Invalid frames are ignored, never fetched. */
object DevelopmentStackParser {
    /** The property declaration value. */
    const val MAX_FRAMES: Int = 100
    /** The property declaration value. */
    const val MAX_TEXT_UTF8_BYTES: Int = 16_384
    private val location = Regex("(https?://[^\\s)]+):(\\d{1,8}):(\\d{1,8})")

    /** Extracts at most [MAX_FRAMES] HTTP(S) locations from a bounded stack string. */
    fun parse(stack: String?): List<GeneratedSourceLocation> {
        if (stack == null) return emptyList()
        return stack.takeUtf8(MAX_TEXT_UTF8_BYTES)
            .lineSequence()
            .mapNotNull { line ->
                val match = location.find(line) ?: return@mapNotNull null
                val sourceLine = match.groupValues[2].toIntOrNull() ?: return@mapNotNull null
                val sourceColumn = match.groupValues[3].toIntOrNull() ?: return@mapNotNull null
                if (sourceLine !in 1..10_000_000 || sourceColumn !in 1..10_000_000) return@mapNotNull null
                GeneratedSourceLocation(match.groupValues[1], sourceLine, sourceColumn)
            }
            .take(MAX_FRAMES)
            .toList()
    }

    /** Returns generic text unless the caller explicitly declared bounded synthetic text public. */
    fun displayText(category: DevelopmentErrorCategory, syntheticText: String?, policy: DevelopmentErrorTextPolicy): String {
        val generic = when (category) {
            DevelopmentErrorCategory.SCRIPT_ERROR -> "Unhandled script error"
            DevelopmentErrorCategory.UNHANDLED_REJECTION -> "Unhandled promise rejection"
            DevelopmentErrorCategory.ERROR_BOUNDARY -> "Component error boundary"
        }
        if (policy != DevelopmentErrorTextPolicy.SYNTHETIC_PUBLIC || syntheticText == null) return generic
        return syntheticText.takeUtf8(MAX_TEXT_UTF8_BYTES)
    }
}

/**
 * Bounded Source Map v3 catalog. It never performs I/O; only explicitly supplied maps are used.
 *
 * @property buildId The build id value.
 * @property maps The maps value.
 */
class VerifiedSourceMapCatalog private constructor(
    private val buildId: String,
    private val maps: Map<String, ParsedSourceMap>
) {
    /** Provides verified source map catalog factory and constant members. */
    companion object {
        /** The property declaration value. */
        const val MAX_SOURCE_MAP_UTF8_BYTES: Int = 4 * 1_048_576
        /** The property declaration value. */
        const val MAX_SOURCE_COUNT: Int = 10_000

        /**
         * Parses the operation.
         *
         * @param buildId The build id value.
         * @param assets The assets value.
         * @return The resulting value.
         */
        fun parse(buildId: String, assets: List<VerifiedSourceMapAsset>): VerifiedSourceMapCatalog {
            requireIdentity(buildId, "buildId")
            require(assets.size <= 128) { "Too many configured source maps" }
            val parsed = linkedMapOf<String, ParsedSourceMap>()
            assets.forEach { asset ->
                require(asset.buildId == buildId) { "Source map build identity does not match" }
                require(asset.generatedAssetUrl.startsWith("http://") || asset.generatedAssetUrl.startsWith("https://")) {
                    "Generated source-map asset must use HTTP(S)"
                }
                require(asset.json.encodeToByteArray().size <= MAX_SOURCE_MAP_UTF8_BYTES) { "Source map exceeds size limit" }
                val sourceMap = try {
                    ParsedSourceMap.parse(asset.json)
                } catch (exception: Exception) {
                    throw IllegalArgumentException("Malformed source map: ${exception.message ?: exception::class.simpleName}", exception)
                }
                require(parsed.put(asset.generatedAssetUrl, sourceMap) == null) {
                    "Duplicate generated source-map asset"
                }
            }
            return VerifiedSourceMapCatalog(buildId, parsed)
        }
    }

    /**
     * Executes the map operation.
     *
     * @param location The location value.
     * @return The resulting value.
     */
    fun map(location: GeneratedSourceLocation): MappedSourceLocation? {
        val mapped = maps[location.assetUrl]?.map(location.line, location.column) ?: return null
        return MappedSourceLocation(mapped.source, mapped.line, mapped.column, buildId)
    }
}

/**
 * Executes the editor link operation.
 *
 * @param location The location value.
 * @return The resulting value.
 */
fun DevelopmentSourceLinkPolicy.editorLink(location: MappedSourceLocation): String? {
    if (location.buildId != sourceRevision) return null
    val protocol = editorProtocol ?: return null
    if (!protocol.matches(Regex("[A-Za-z][A-Za-z0-9+.-]{0,31}"))) return null
    val normalizedProtocol = protocol.lowercase()
    if (
        normalizedProtocol == "http" ||
        normalizedProtocol == "https" ||
        normalizedProtocol == "javascript" ||
        normalizedProtocol == "data" ||
        normalizedProtocol == "file"
    ) return null
    val root = normalizeWorkspaceRoot(workspaceRoot) ?: return null
    val source = normalizeSourcePath(location.source) ?: return null
    if (!validPosition(location.line, location.column)) return null
    return "$protocol://file${percentEncodePath("$root/$source")}:${location.line}:${location.column}"
}

/**
 * Executes the viewer link operation.
 *
 * @param location The location value.
 * @return The resulting value.
 */
fun DevelopmentSourceLinkPolicy.viewerLink(location: MappedSourceLocation): String? {
    if (location.buildId != sourceRevision) return null
    val origin = normalizeViewerOrigin(viewerOrigin) ?: return null
    val source = normalizeSourcePath(location.source) ?: return null
    if (!validPosition(location.line, location.column)) return null
    return "$origin/source/${percentEncodeSegment(location.buildId)}/${source.split('/').joinToString("/") { percentEncodeSegment(it) }}#L${location.line}:C${location.column}"
}

private data class OriginalPosition(val source: String, val line: Int, val column: Int)
private data class MappingSegment(val generatedColumn: Int, val sourceIndex: Int, val originalLine: Int, val originalColumn: Int)
private data class MappingLine(
    val start: Int,
    val end: Int,
    val sourceIndex: Int,
    val originalLine: Int,
    val originalColumn: Int
)

private class MappingState(
    var sourceIndex: Int = 0,
    var originalLine: Int = 0,
    var originalColumn: Int = 0
)

private class ParsedSourceMap(
    private val sources: List<String>,
    private val mappings: String,
    private val lines: List<MappingLine>
) {
    companion object {
        fun parse(source: String): ParsedSourceMap {
            val root = Json.parseToJsonElement(source).jsonObject
            require(root.getValue("version").jsonPrimitive.content == "3") { "Only Source Map v3 is supported" }
            val sourceRoot = root["sourceRoot"]?.jsonPrimitive?.content.orEmpty().trimEnd('/')
            val sources = root.getValue("sources").jsonArray.map { entry ->
                val source = entry.jsonPrimitive.content
                normalizeSourcePath(if (sourceRoot.isEmpty()) source else "$sourceRoot/$source")
                    ?: throw IllegalArgumentException("Source map contains an unsafe source path")
            }
            require(sources.size <= VerifiedSourceMapCatalog.MAX_SOURCE_COUNT) { "Source map has too many sources" }
            val mappings = root.getValue("mappings").jsonPrimitive.content
            require(mappings.length <= VerifiedSourceMapCatalog.MAX_SOURCE_MAP_UTF8_BYTES) { "Source-map mappings exceed size limit" }
            return ParsedSourceMap(sources, mappings, indexMappings(mappings, sources.size))
        }
    }

    fun map(line: Int, column: Int): OriginalPosition? {
        val indexed = lines.getOrNull(line - 1) ?: return null
        val state = MappingState(indexed.sourceIndex, indexed.originalLine, indexed.originalColumn)
        val segment = scanMappingLine(
            mappings,
            indexed.start,
            indexed.end,
            sources.size,
            state,
            column - 1
        ) ?: return null
        val source = sources.getOrNull(segment.sourceIndex) ?: return null
        return OriginalPosition(source, segment.originalLine + 1, segment.originalColumn + 1)
    }
}

private fun indexMappings(mappings: String, sourceCount: Int): List<MappingLine> {
    val lines = ArrayList<MappingLine>()
    val state = MappingState()
    var start = 0
    while (start <= mappings.length) {
        val delimiter = mappings.indexOf(';', start)
        state.originalColumn = 0
        val end = if (delimiter < 0) mappings.length else delimiter
        lines += MappingLine(start, end, state.sourceIndex, state.originalLine, state.originalColumn)
        scanMappingLine(mappings, start, end, sourceCount, state, targetColumn = null)
        if (delimiter < 0) break
        start = delimiter + 1
    }
    return lines
}

private fun scanMappingLine(
    mappings: String,
    start: Int,
    end: Int,
    sourceCount: Int,
    state: MappingState,
    targetColumn: Int?
): MappingSegment? {
    var generatedColumn = 0
    var selected: MappingSegment? = null
    var segmentStart = start
    while (segmentStart < end) {
        var segmentEnd = mappings.indexOf(',', segmentStart)
        if (segmentEnd < 0 || segmentEnd > end) segmentEnd = end
        if (segmentEnd > segmentStart) {
            var cursor = segmentStart
            var fieldCount = 0
            var generatedDelta = 0
            var sourceDelta = 0
            var lineDelta = 0
            var columnDelta = 0
            while (cursor < segmentEnd) {
                var value = 0
                var shift = 0
                while (true) {
                    require(cursor < segmentEnd) { "Truncated source-map VLQ value" }
                    val digit = BASE64.indexOf(mappings[cursor++])
                    require(digit >= 0) { "Invalid source-map VLQ digit" }
                    value += (digit and 31) shl shift
                    if ((digit and 32) == 0) break
                    shift += 5
                    require(shift <= 30) { "Source-map VLQ value is too large" }
                }
                val decoded = if ((value and 1) == 1) -(value shr 1) else value shr 1
                when (fieldCount) {
                    0 -> generatedDelta = decoded
                    1 -> sourceDelta = decoded
                    2 -> lineDelta = decoded
                    3 -> columnDelta = decoded
                    4 -> Unit // Optional name index is not needed for source locations.
                    else -> throw IllegalArgumentException("Invalid source-map segment")
                }
                fieldCount++
            }
            require(fieldCount == 1 || fieldCount == 4 || fieldCount == 5) { "Invalid source-map segment" }
            generatedColumn += generatedDelta
            if (fieldCount == 1 && targetColumn != null && generatedColumn <= targetColumn) {
                selected = null
            }
            require(generatedColumn >= 0) { "Invalid generated source-map column" }
            if (fieldCount >= 4) {
                state.sourceIndex += sourceDelta
                state.originalLine += lineDelta
                state.originalColumn += columnDelta
                require(state.sourceIndex in 0 until sourceCount) {
                    "Invalid source-map source index"
                }
                if (
                    targetColumn != null &&
                    generatedColumn <= targetColumn &&
                    state.originalLine >= 0 &&
                    state.originalColumn >= 0
                ) {
                    selected = MappingSegment(
                        generatedColumn,
                        state.sourceIndex,
                        state.originalLine,
                        state.originalColumn
                    )
                }
            }
        }
        segmentStart = segmentEnd + 1
    }
    return selected
}

private fun normalizeViewerOrigin(raw: String?): String? {
    val match = raw?.let { VIEWER_ORIGIN.matchEntire(it) } ?: return null
    val host = match.groupValues[1]
    if (host.split('.').any { label ->
            label.isEmpty() ||
                label.startsWith('-') ||
                label.endsWith('-')
        }
    ) return null
    val port = match.groupValues[2]
    if (port.isNotEmpty() && port.toIntOrNull() !in 1..65_535) return null
    return raw
}

private fun normalizeSourcePath(raw: String): String? {
    var value = when {
        raw.startsWith("webpack:///") -> raw.removePrefix("webpack:///")
        raw.startsWith("webpack://") -> raw.removePrefix("webpack://").substringAfter('/', "")
        else -> raw
    }
    if (raw.startsWith("webpack://")) {
        while (value.startsWith("./") || value.startsWith("../")) {
            value = value.substringAfter('/')
        }
    } else {
        value = value.removePrefix("./")
    }
    while (value.startsWith('/')) value = value.removePrefix("/")
    if (value.isEmpty() || value.contains('\\') || value.contains(':') || value.contains('?') || value.contains('#')) return null
    val segments = value.split('/').filter { it != "." }
    if (segments.any { it.isEmpty() || it == ".." }) return null
    return segments.joinToString("/")
}

private fun normalizeWorkspaceRoot(raw: String?): String? {
    val root = raw?.takeIf { it.startsWith('/') && !it.contains('\\') && !it.contains(':') }?.trimEnd('/')
        ?: return null
    if (root.split('/').drop(1).any { it.isEmpty() || it == "." || it == ".." }) return null
    return root
}

private fun validPosition(line: Int, column: Int): Boolean = line in 1..10_000_000 && column in 1..10_000_000

private fun requireIdentity(value: String, label: String) {
    require(value.matches(Regex("[A-Za-z0-9._-]{1,128}"))) { "$label is invalid" }
}

private fun percentEncodePath(value: String): String = value.encodeToByteArray().joinToString("") { byte ->
    val unsigned = byte.toInt() and 0xff
    val character = unsigned.toChar()
    if (isUnreservedAscii(unsigned) || character == '/') character.toString() else "%${unsigned.toString(16).uppercase().padStart(2, '0')}"
}

private fun percentEncodeSegment(value: String): String = value.encodeToByteArray().joinToString("") { byte ->
    val unsigned = byte.toInt() and 0xff
    val character = unsigned.toChar()
    if (isUnreservedAscii(unsigned)) character.toString() else "%${unsigned.toString(16).uppercase().padStart(2, '0')}"
}

private fun isUnreservedAscii(value: Int): Boolean =
    value in 'A'.code..'Z'.code ||
        value in 'a'.code..'z'.code ||
        value in '0'.code..'9'.code ||
        value == '-'.code ||
        value == '.'.code ||
        value == '_'.code ||
        value == '~'.code

private fun String.takeUtf8(maxBytes: Int): String {
    if (encodeToByteArray().size <= maxBytes) return this
    var index = 0
    var bytes = 0
    while (index < length) {
        val character = this[index]
        val characterBytes: Int
        val width: Int
        when {
            character.code <= 0x7f -> {
                characterBytes = 1
                width = 1
            }
            character.code <= 0x7ff -> {
                characterBytes = 2
                width = 1
            }
            character.isHighSurrogate() && index + 1 < length && this[index + 1].isLowSurrogate() -> {
                characterBytes = 4
                width = 2
            }
            else -> {
                characterBytes = 3
                width = 1
            }
        }
        if (bytes + characterBytes > maxBytes) break
        bytes += characterBytes
        index += width
    }
    return substring(0, index)
}

private val VIEWER_ORIGIN = Regex("https://([A-Za-z0-9.-]+)(?::([0-9]{1,5}))?")

private const val BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

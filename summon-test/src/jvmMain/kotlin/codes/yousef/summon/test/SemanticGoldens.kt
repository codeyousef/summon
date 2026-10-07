package codes.yousef.summon.test

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val MAX_GOLDEN_BYTES = 1_048_576L

/**
 * Verifies this harness against an existing UTF-8 semantic golden. A missing or changed golden is
 * a failure; this function never creates or modifies files.
 */
fun ComponentHarness.verifySemanticGolden(goldenFile: File): ComponentHarness {
    if (!goldenFile.isFile) throw AssertionError("Semantic golden is missing: ${goldenFile.path}")
    if (goldenFile.length() > MAX_GOLDEN_BYTES) {
        throw AssertionError("Semantic golden exceeds $MAX_GOLDEN_BYTES bytes: ${goldenFile.path}")
    }
    return assertSemanticSnapshot(goldenFile.readText(StandardCharsets.UTF_8))
}

/**
 * Explicitly replaces only [goldenFile] with this harness's deterministic UTF-8 snapshot.
 * Normal verification must use [verifySemanticGolden] so failures cannot rewrite baselines.
 */
fun ComponentHarness.updateSemanticGolden(goldenFile: File): ComponentHarness {
    val snapshot = semanticSnapshot()
    val bytes = snapshot.toByteArray(StandardCharsets.UTF_8)
    require(bytes.size <= MAX_GOLDEN_BYTES) { "Semantic snapshot exceeds $MAX_GOLDEN_BYTES bytes" }
    val parent = goldenFile.absoluteFile.parentFile
    check(parent.exists() || parent.mkdirs()) { "Cannot create semantic golden directory: ${parent.path}" }
    val temporary = Files.createTempFile(parent.toPath(), ".${goldenFile.name}.", ".tmp")
    try {
        Files.write(temporary, bytes)
        try {
            Files.move(
                temporary,
                goldenFile.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
            )
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(temporary, goldenFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    } finally {
        Files.deleteIfExists(temporary)
    }
    return this
}

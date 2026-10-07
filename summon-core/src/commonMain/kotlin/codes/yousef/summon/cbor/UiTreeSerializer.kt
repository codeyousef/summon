package codes.yousef.summon.cbor

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

/**
 * Encodes UI trees and patches as deterministic CBOR payloads.
 */
object UiTreeSerializer {
    private val cbor = Cbor {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Serializes the UI tree to a byte array.
     */
    fun serialize(tree: UiTree): ByteArray =
        cbor.encodeToByteArray(UiTree.serializer(), tree)

    /**
     * Deserializes the UI tree from a byte array.
     */
    fun deserialize(bytes: ByteArray): UiTree =
        cbor.decodeFromByteArray(UiTree.serializer(), bytes)

    /**
     * Serializes a list of patches.
     */
    fun serializePatches(patches: List<UiPatch>): ByteArray =
        cbor.encodeToByteArray(ListSerializer(UiPatch.serializer()), patches)

    /**
     * Deserializes an ordered patch batch.
     */
    fun deserializePatches(bytes: ByteArray): List<UiPatch> =
        cbor.decodeFromByteArray(ListSerializer(UiPatch.serializer()), bytes)
}

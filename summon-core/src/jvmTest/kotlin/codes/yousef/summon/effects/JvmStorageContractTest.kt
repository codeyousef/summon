package codes.yousef.summon.effects

import kotlinx.serialization.Serializable
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JvmStorageContractTest {
    @Test
    fun memoryAndTypedStoragePreserveValuesPrefixesAndSerializableTypes() {
        val storage = createMemoryStorage()
        assertEquals(0, storage.length())
        assertNull(storage.getItem("missing"))
        storage.setItem("plain", "value")
        assertEquals("value", storage.getItem("plain"))
        assertTrue(storage.contains("plain"))
        assertEquals(listOf("plain"), storage.keys())
        storage.removeItem("plain")
        assertFalse(storage.contains("plain"))

        val first = TypedStorage(storage, "first")
        val second = TypedStorage(storage, "second")
        first.setString("text", "hello")
        first.setInt("int", 42)
        first.setLong("long", Long.MAX_VALUE)
        first.setBoolean("boolean", true)
        first.setFloat("float", 1.5f)
        first.setDouble("double", 2.5)
        first.setTypedItem("record", StoredRecord("summon", 8))
        second.setString("text", "other")

        assertEquals("hello", first.getString("text"))
        assertEquals(42, first.getInt("int"))
        assertEquals(Long.MAX_VALUE, first.getLong("long"))
        assertEquals(true, first.getBoolean("boolean"))
        assertEquals(1.5f, first.getFloat("float"))
        assertEquals(2.5, first.getDouble("double"))
        assertEquals(StoredRecord("summon", 8), first.getTypedItem<StoredRecord>("record"))
        assertEquals("other", second.getString("text"))
        assertEquals(setOf("text", "int", "long", "boolean", "float", "double", "record"), first.keys().toSet())

        first.setString("bad-int", "NaN")
        first.setString("bad-long", "NaN")
        first.setString("bad-boolean", "yes")
        first.setString("bad-float", "not-a-float")
        first.setString("bad-double", "not-a-double")
        first.setString("bad-json", "not-json")
        assertNull(first.getInt("bad-int"))
        assertNull(first.getLong("bad-long"))
        assertNull(first.getBoolean("bad-boolean"))
        assertNull(first.getFloat("bad-float"))
        assertNull(first.getDouble("bad-double"))
        assertNull(first.getTypedItem<StoredRecord>("bad-json"))
        assertNull(first.getTypedItem<StoredRecord>("missing"))

        first.removeItem("text")
        assertNull(first.getString("text"))
        first.clear()
        assertTrue(first.keys().isEmpty())
        assertEquals("other", second.getString("text"))
        storage.clear()
        assertEquals(0, storage.length())
    }

    @Test
    fun filePreferencesAndFactoriesImplementTheSameStorageContract() {
        val directory = Files.createTempDirectory("summon-storage-test")
        try {
            val fileStorage = Storage.createFileStorage(directory.toString())
            fileStorage.setItem("nested/key:*", "file-value")
            assertEquals("file-value", fileStorage.getItem("nested/key:*"))
            assertEquals(listOf("nested_key__"), fileStorage.keys())
            assertEquals(1, fileStorage.length())
            fileStorage.removeItem("nested/key:*")
            assertNull(fileStorage.getItem("nested/key:*"))
            fileStorage.setItem("one", "1")
            fileStorage.setItem("two", "22")
            fileStorage.clear()
            assertTrue(fileStorage.keys().isEmpty())
        } finally {
            directory.toFile().deleteRecursively()
        }

        val session = createSessionStorage()
        session.setItem("session", "value")
        assertEquals("value", session.getItem("session"))

        val local = createLocalStorage()
        val key = "summon-test-${System.nanoTime()}"
        try {
            local.setItem(key, "local")
            assertEquals("local", local.getItem(key))
            assertTrue(local.keys().contains(key))
        } finally {
            local.removeItem(key)
        }

        assertTrue(getStorage(StorageType.MEMORY).keys().isEmpty())
        assertTrue(getStorage(StorageType.SESSION).keys().isEmpty())
        val scoped = StorageUtils.createScopedStorage(StorageType.MEMORY, "feature")
        scoped.setString("key", "value")
        assertEquals("value", scoped.getString("key"))
        val typed = StorageUtils.createTypedStorage(StorageType.MEMORY, "prefix")
        typed.setString("key", "value")
        assertEquals("value", typed.getString("key"))
        val info = StorageUtils.getStorageInfo(StorageType.MEMORY)
        assertEquals(StorageType.MEMORY, info.type)
        assertEquals(0, info.itemCount)
        assertEquals(0, info.approximateSize)
    }

    @Test
    fun migrationAndUsageInspectionHonorPrefixesAndMissingValues() {
        val prefix = "summon-migrate-${System.nanoTime()}-"
        val local = createLocalStorage()
        val matching = "${prefix}matching"
        val unrelated = "${prefix}other".replace(prefix, "unrelated-")
        try {
            local.setItem(matching, "value")
            local.setItem(unrelated, "ignored")

            StorageUtils.migrateStorage(StorageType.LOCAL, StorageType.LOCAL, prefix)
            val info = StorageUtils.getStorageInfo(StorageType.LOCAL)
            assertTrue(info.keys.contains(matching))
            assertTrue(info.approximateSize >= matching.length + "value".length)

            StorageUtils.migrateStorage(StorageType.LOCAL, StorageType.LOCAL)
            assertEquals("value", createLocalStorage().getItem(matching))
        } finally {
            local.removeItem(matching)
            local.removeItem(unrelated)
        }
    }

    @Serializable
    private data class StoredRecord(val name: String, val version: Int)
}

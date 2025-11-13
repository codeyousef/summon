package codes.yousef.summon.cli

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test that validates Gradle wrapper resources are available in the CLI JAR
 * This ensures that scaffolded projects can successfully run `./gradlew`
 */
class WrapperResourceValidationTest {

    @Test
    fun `gradle-wrapper jar is available as resource`() {
        val resource = this::class.java.classLoader.getResourceAsStream("gradle-wrapper/gradle-wrapper.jar")
        assertNotNull(resource, "gradle-wrapper.jar not found in resources")

        // Verify it's a non-empty ZIP file (JAR files are ZIP archives)
        val bytes = resource.use { it.readBytes() }
        assertTrue(bytes.isNotEmpty(), "gradle-wrapper.jar is empty")
        assertTrue(bytes.size > 1000, "gradle-wrapper.jar is too small (${bytes.size} bytes)")

        // Check ZIP magic number (PK\x03\x04)
        assertTrue(
            bytes[0] == 0x50.toByte() && bytes[1] == 0x4B.toByte(),
            "gradle-wrapper.jar does not have valid ZIP header"
        )
    }

    @Test
    fun `gradle-wrapper properties is available as resource`() {
        val resource = this::class.java.classLoader.getResourceAsStream("gradle-wrapper/gradle-wrapper.properties")
        assertNotNull(resource, "gradle-wrapper.properties not found in resources")

        val content = resource.use { it.bufferedReader().readText() }
        assertTrue(content.isNotEmpty(), "gradle-wrapper.properties is empty")
        assertTrue(
            content.contains("distributionUrl"),
            "gradle-wrapper.properties missing distributionUrl"
        )
    }

    @Test
    fun `gradlew script is available as resource`() {
        val resource = this::class.java.classLoader.getResourceAsStream("gradle-wrapper/gradlew")
        assertNotNull(resource, "gradlew not found in resources")

        val content = resource.use { it.bufferedReader().readText() }
        assertTrue(content.isNotEmpty(), "gradlew is empty")
        assertTrue(
            content.contains("#!/bin/sh") || content.contains("#!/usr/bin/env sh"),
            "gradlew missing shebang"
        )
    }

    @Test
    fun `gradlew bat script is available as resource`() {
        val resource = this::class.java.classLoader.getResourceAsStream("gradle-wrapper/gradlew.bat")
        assertNotNull(resource, "gradlew.bat not found in resources")

        val content = resource.use { it.bufferedReader().readText() }
        assertTrue(content.isNotEmpty(), "gradlew.bat is empty")
        assertTrue(
            content.contains("@rem") || content.contains("@echo off"),
            "gradlew.bat missing Windows batch header"
        )
    }

    @Test
    fun `all four wrapper files are present`() {
        val classLoader = this::class.java.classLoader
        val files = listOf(
            "gradle-wrapper/gradle-wrapper.jar",
            "gradle-wrapper/gradle-wrapper.properties",
            "gradle-wrapper/gradlew",
            "gradle-wrapper/gradlew.bat"
        )

        val missingFiles = files.filter { classLoader.getResourceAsStream(it) == null }
        assertTrue(
            missingFiles.isEmpty(),
            "Missing wrapper resources: ${missingFiles.joinToString(", ")}"
        )
    }
}

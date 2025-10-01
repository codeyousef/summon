package code.yousef.summon.cli

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test that validates Gradle wrapper files exist in the build resources directory.
 * The actual validation of the shadow JAR is done by the validateWrapperInJar task.
 *
 * These tests verify that the source wrapper files are present before they get injected.
 */
class WrapperResourceValidationTest {

    private val wrapperDir = File("build/resources/jvmMain/gradle-wrapper")

    @Test
    fun `gradle-wrapper jar exists in build resources`() {
        val jarFile = File(wrapperDir, "gradle-wrapper.jar")
        assertTrue(jarFile.exists(), "gradle-wrapper.jar not found at ${jarFile.absolutePath}")
        assertTrue(jarFile.length() > 1000, "gradle-wrapper.jar is too small (${jarFile.length()} bytes)")

        // Check ZIP magic number (PK\x03\x04)
        val bytes = jarFile.readBytes()
        assertTrue(
            bytes[0] == 0x50.toByte() && bytes[1] == 0x4B.toByte(),
            "gradle-wrapper.jar does not have valid ZIP header"
        )
    }

    @Test
    fun `gradle-wrapper properties exists in build resources`() {
        val propsFile = File(wrapperDir, "gradle-wrapper.properties")
        assertTrue(propsFile.exists(), "gradle-wrapper.properties not found at ${propsFile.absolutePath}")

        val content = propsFile.readText()
        assertTrue(content.isNotEmpty(), "gradle-wrapper.properties is empty")
        assertTrue(
            content.contains("distributionUrl"),
            "gradle-wrapper.properties missing distributionUrl"
        )
    }

    @Test
    fun `gradlew script exists in build resources`() {
        val gradlewFile = File(wrapperDir, "gradlew")
        assertTrue(gradlewFile.exists(), "gradlew not found at ${gradlewFile.absolutePath}")

        val content = gradlewFile.readText()
        assertTrue(content.isNotEmpty(), "gradlew is empty")
        assertTrue(
            content.contains("#!/bin/sh") || content.contains("#!/usr/bin/env sh"),
            "gradlew missing shebang"
        )
    }

    @Test
    fun `gradlew bat script exists in build resources`() {
        val gradlewBatFile = File(wrapperDir, "gradlew.bat")
        assertTrue(gradlewBatFile.exists(), "gradlew.bat not found at ${gradlewBatFile.absolutePath}")

        val content = gradlewBatFile.readText()
        assertTrue(content.isNotEmpty(), "gradlew.bat is empty")
        assertTrue(
            content.contains("@rem") || content.contains("@echo off"),
            "gradlew.bat missing Windows batch header"
        )
    }

    @Test
    fun `all four wrapper files are present in build resources`() {
        val files = listOf(
            "gradle-wrapper.jar",
            "gradle-wrapper.properties",
            "gradlew",
            "gradlew.bat"
        )

        val missingFiles = files.filter { !File(wrapperDir, it).exists() }
        assertTrue(
            missingFiles.isEmpty(),
            "Missing wrapper files in ${wrapperDir.absolutePath}: ${missingFiles.joinToString(", ")}"
        )
    }
}

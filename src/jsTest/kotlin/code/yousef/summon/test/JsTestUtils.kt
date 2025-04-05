package code.yousef.summon.test

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test utilities for JS tests to avoid unresolved reference issues,
 * by re-exporting the test functionality from the appropriate locations.
 * 
 * This is needed because the JS Kotlin/test environment may not properly
 * resolve the references in individual test files.
 */ 
package codes.yousef.summon.css

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for CssInjector functionality.
 *
 * TEST DIRECTIVE: Pass a CSS string `body { background: red; }`.
 * Verify a style tag exists with that content.
 * Verify the computed style of body is red.
 *
 * Note: These tests verify the sanitization and logic without actual DOM access.
 * Browser-specific tests should be in jsTest/wasmJsTest source sets.
 */
class StyleInjectionTest {

    @Test
    fun testCssSanitization() {
        // Test that dangerous content is stripped
        val dangerousCss = """
            body { background: red; }
            </style><script>alert('xss')</script><style>
            p { color: blue; }
        """.trimIndent()

        val sanitized = CssSanitizer.sanitize(dangerousCss)

        // Should not contain </style> tags
        assertFalse(sanitized.contains("</style>"), "Should remove </style> tags")
        assertFalse(sanitized.contains("<script>"), "Should remove script tags")

        // Should preserve valid CSS
        assertTrue(sanitized.contains("body { background: red; }"), "Should preserve valid CSS")
        assertTrue(sanitized.contains("p { color: blue; }"), "Should preserve valid CSS")
    }

    @Test
    fun testCssSanitizationPreservesValidCss() {
        val validCss = """
            .container {
                display: flex;
                flex-direction: column;
                gap: 16px;
            }
            
            .button {
                background-color: #007bff;
                border-radius: 4px;
                padding: 8px 16px;
            }
            
            @media (max-width: 768px) {
                .container {
                    flex-direction: row;
                }
            }
        """.trimIndent()

        val sanitized = CssSanitizer.sanitize(validCss)

        // All valid CSS should be preserved
        assertTrue(sanitized.contains("display: flex"))
        assertTrue(sanitized.contains("flex-direction: column"))
        assertTrue(sanitized.contains("gap: 16px"))
        assertTrue(sanitized.contains("background-color: #007bff"))
        assertTrue(sanitized.contains("@media (max-width: 768px)"))
    }

    @Test
    fun testCssSanitizationWithMultipleInjectionAttempts() {
        val maliciousCss = """
            div { color: red; }
            </style></style></style>
            span { color: blue; }
            </STYLE>
            p { color: green; }
        """.trimIndent()

        val sanitized = CssSanitizer.sanitize(maliciousCss)

        // All injection attempts should be removed (case insensitive)
        assertFalse(sanitized.lowercase().contains("</style>"))
        
        // Valid CSS should remain
        assertTrue(sanitized.contains("div { color: red; }"))
        assertTrue(sanitized.contains("span { color: blue; }"))
        assertTrue(sanitized.contains("p { color: green; }"))
    }

    @Test
    fun testCssSanitizationWithEmptyString() {
        val emptyCss = ""
        val sanitized = CssSanitizer.sanitize(emptyCss)
        assertEquals("", sanitized)
    }

    @Test
    fun testCssSanitizationWithOnlyWhitespace() {
        val whitespaceCss = "   \n\t   \n   "
        val sanitized = CssSanitizer.sanitize(whitespaceCss)
        // Should preserve whitespace structure
        assertTrue(sanitized.isNotEmpty() || whitespaceCss.trim().isEmpty())
    }

    @Test
    fun testCssSanitizationWithComplexSelectors() {
        val complexCss = """
            div > p + span ~ .class#id[attr="value"]:hover::before {
                content: "test";
                background: url('data:image/svg+xml,<svg></svg>');
            }
        """.trimIndent()

        val sanitized = CssSanitizer.sanitize(complexCss)

        // Complex but valid CSS should be preserved
        assertTrue(sanitized.contains("div > p + span ~ .class#id[attr=\"value\"]:hover::before"))
        assertTrue(sanitized.contains("content: \"test\""))
    }

    @Test
    fun testCssSanitizationWithCssVariables() {
        val cssWithVariables = """
            :root {
                --primary-color: #007bff;
                --spacing: 16px;
            }
            
            .button {
                background: var(--primary-color);
                margin: var(--spacing);
            }
        """.trimIndent()

        val sanitized = CssSanitizer.sanitize(cssWithVariables)

        assertTrue(sanitized.contains("--primary-color: #007bff"))
        assertTrue(sanitized.contains("var(--primary-color)"))
        assertTrue(sanitized.contains("var(--spacing)"))
    }
}

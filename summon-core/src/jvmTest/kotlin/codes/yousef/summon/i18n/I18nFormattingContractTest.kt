package codes.yousef.summon.i18n

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals

class I18nFormattingContractTest {
    private fun <T : Any> compose(language: Language, block: () -> T): T {
        lateinit var result: T
        PlatformRenderer().renderComposableRoot {
            LanguageProvider(language) { result = block() }
        }
        return result
    }

    @Test
    fun numberFormattingUsesLanguageSeparatorsAndFractionBounds() {
        assertEquals("1,234.5", compose(Language("en", "English", LayoutDirection.LTR)) { I18nUtils.formatNumber(1234.5) })
        assertEquals("1,234,50", compose(Language("fr", "French", LayoutDirection.LTR)) { I18nUtils.formatNumber(1234.5, 2, 2) })
        assertEquals("1٬234٫5", compose(Language("ar", "Arabic", LayoutDirection.RTL)) { I18nUtils.formatNumber(1234.5) })
        assertEquals("1234.5", compose(Language("zz", "Unknown", LayoutDirection.LTR)) { I18nUtils.formatNumber(1234.5) })
        assertEquals("1,234", compose(Language("de", "German", LayoutDirection.LTR)) { I18nUtils.formatNumber(1234) })
    }

    @Test
    fun dateFormattingHandlesLeapYearsAndEveryPresentation() {
        val leapDayUtc = 951_782_400_000L
        assertEquals("2000-02-29", compose(Language("en", "English", LayoutDirection.LTR)) { I18nUtils.formatDate(leapDayUtc, DateFormat.SHORT) })
        assertEquals("2000-02-29 00:00:00", compose(Language("en", "English", LayoutDirection.LTR)) { I18nUtils.formatDate(leapDayUtc, DateFormat.MEDIUM) })
        assertEquals("2000-02-29 00:00:00 UTC", compose(Language("en", "English", LayoutDirection.LTR)) { I18nUtils.formatDate(leapDayUtc, DateFormat.LONG) })
        assertEquals("2000-02-29 00:00:00 UTC (FR)", compose(Language("fr", "French", LayoutDirection.LTR)) { I18nUtils.formatDate(leapDayUtc, DateFormat.FULL) })
    }
}

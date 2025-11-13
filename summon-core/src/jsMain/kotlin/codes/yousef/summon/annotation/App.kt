package codes.yousef.summon.annotation

/**
 * An annotation which identifies a [Composable] function as one which will be used as the root skeleton for every
 * page.
 *
 * The method should take a `content: @Composable () -> Unit` parameter.
 *
 * If no method is annotated `@App` then a reasonable default will be used ([SummonApp] at a bare minimum).
 * Of course, your own custom app method can compose this function if it wishes to:
 *
 * ```
 * object SiteStyleSheet : StyleSheet() {
 *    /* ... global styles here, for example fonts or site-wide line spacing ... */
 * }
 *
 * @App
 * @Composable
 * fun AppEntry(content: @Composable () -> Unit) {
 *   SummonApp {
 *     // Add your global styles or components here
 *     content()
 *   }
 * }
 * ```
 *
 * There must either be **no methods** or **just a single method** marked with this annotation. If Summon encounters
 * more than one `@App` annotation, it will log an error and discard duplicates arbitrarily.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class App
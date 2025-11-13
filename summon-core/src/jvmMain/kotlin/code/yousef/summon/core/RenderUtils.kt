package codes.yousef.summon.core

import codes.yousef.summon.annotation.Composable

/**
 * JVM implementation of RenderUtils
 */
actual object RenderUtils {
    /**
     * Renders a composable to a DOM element (not supported in JVM).
     *
     * @param container The container element
     * @param composable The composable to render
     * @return A renderer instance
     */
    actual fun renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        throw NotImplementedError("renderComposable is not supported in JVM")
    }

    /**
     * Hydrates a server-rendered DOM tree with a composable (not supported in JVM).
     *
     * @param container The container element
     * @param composable The composable to hydrate with
     * @return A renderer instance
     */
    actual fun hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        throw NotImplementedError("hydrate is not supported in JVM")
    }

    /**
     * Renders a composable to a string.
     *
     * @param composable The composable to render
     * @return The rendered HTML string
     */
    actual fun renderToString(composable: @Composable () -> Unit): String {
        throw NotImplementedError("renderToString is not supported in JVM")
    }

    /**
     * Renders a composable to a file.
     *
     * @param composable The composable to render
     * @param file The file to write to
     */
    actual fun renderToFile(composable: @Composable () -> Unit, file: Any) {
        throw NotImplementedError("renderToFile is not supported in JVM")
    }
}

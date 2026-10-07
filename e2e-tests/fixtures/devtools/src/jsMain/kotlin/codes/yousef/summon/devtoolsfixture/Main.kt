package codes.yousef.summon.devtoolsfixture

import codes.yousef.summon.mountComposableRoot

fun main() {
    val first = InspectorFixture("first")
    val second = InspectorFixture("second")
    mountComposableRoot("root") { first.Content() }
    mountComposableRoot("second-root") { second.Content() }
    installInspector(first, second)
}

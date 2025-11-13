package codes.yousef.summon.js.testutil

import kotlin.test.BeforeTest

private var environmentInitialized = false

@BeforeTest
fun setupJsTestEnvironment() {
    if (!environmentInitialized) {
        ensureJsDom()
        environmentInitialized = true
    }
}

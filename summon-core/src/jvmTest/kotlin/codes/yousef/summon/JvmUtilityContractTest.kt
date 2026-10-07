package codes.yousef.summon

import codes.yousef.summon.ssr.CacheHeaders
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmUtilityContractTest {
    @AfterTest
    fun removeFrameworkListeners() {
        listOf("spring-boot", "ktor", "quarkus", "micronaut", "failing", "observed").forEach {
            BackendIntegrations.unregisterLifecycleListener(it)
        }
    }

    @Test
    fun lifecycleListenersAreGroupedRemovableAndFailureIsolated() {
        val observed = mutableListOf<BackendIntegrations.LifecycleEvent>()
        BackendIntegrations.registerLifecycleListener("failing") { error("synthetic listener failure") }
        BackendIntegrations.registerLifecycleListener("observed") { observed += it }
        BackendIntegrations.registerLifecycleListener("observed") { observed += it }

        BackendIntegrations.triggerLifecycleEvent(BackendIntegrations.LifecycleEvent.PAUSE)
        assertEquals(listOf(BackendIntegrations.LifecycleEvent.PAUSE, BackendIntegrations.LifecycleEvent.PAUSE), observed)
        BackendIntegrations.unregisterLifecycleListener("observed")
        BackendIntegrations.triggerLifecycleEvent(BackendIntegrations.LifecycleEvent.RESUME)
        assertEquals(2, observed.size)

        BackendIntegrations.setupSpringBootIntegration()
        BackendIntegrations.setupKtorIntegration()
        BackendIntegrations.setupQuarkusIntegration()
        BackendIntegrations.setupMicronautIntegration()
        BackendIntegrations.triggerLifecycleEvent(BackendIntegrations.LifecycleEvent.STARTUP)
        BackendIntegrations.triggerLifecycleEvent(BackendIntegrations.LifecycleEvent.SHUTDOWN)
    }

    @Test
    fun cacheHeadersClassifyHashedStableAndUnknownAssets() {
        assertEquals(CacheHeaders.IMMUTABLE_ASSET, CacheHeaders.forAsset("abcdef0123.wasm"))
        assertEquals(CacheHeaders.IMMUTABLE_ASSET, CacheHeaders.forAsset("runtime.abcdef.bundle.js"))
        assertEquals(CacheHeaders.VERSIONED_ASSET, CacheHeaders.forAsset("summon-hydration.js"))
        assertEquals(CacheHeaders.VERSIONED_ASSET, CacheHeaders.forAsset("summon-hydration.wasm"))
        assertEquals(CacheHeaders.VERSIONED_ASSET, CacheHeaders.forAsset("ABCDEF.wasm"))
        assertEquals(CacheHeaders.NO_CACHE, CacheHeaders.forAsset("index.html"))
    }
}

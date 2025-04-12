package code.yousef.summon.integrations.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

/**
 * CDI Producer for Summon components in Quarkus applications
 */
@ApplicationScoped
class SummonProducer {

    /**
     * Produces a singleton SummonRenderer that can be injected into Quarkus resources
     */
    @Produces
    @Singleton
    fun createRenderer(): QuarkusExtension.SummonRenderer {
        return QuarkusExtension.SummonRenderer()
    }
} 
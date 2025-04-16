package code.yousef.summon.integrations.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import org.jboss.logging.Logger

/**
 * CDI Producer for Summon components in Quarkus applications
 */
@ApplicationScoped
class SummonProducer {
    private val logger = Logger.getLogger(SummonProducer::class.java)

    /**
     * Produces a singleton SummonRenderer that can be injected into Quarkus resources
     */
    @Produces
    @Singleton
    fun createRenderer(): QuarkusExtension.SummonRenderer {
        logger.info("Creating a new SummonRenderer instance")
        return QuarkusExtension.SummonRenderer()
    }
} 
package code.yousef.example.quarkus

import code.yousef.summon.integrations.quarkus.QuarkusExtension
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

/**
 * Producer class for Summon Quarkus integration components.
 * This class provides CDI beans for Summon components that need to be injected.
 */
@ApplicationScoped
class QuarkusExtensionProducer {
    
    /**
     * Produces a SummonRenderer instance for injection.
     * This makes the QuarkusExtension.SummonRenderer available for CDI injection.
     * 
     * @return A singleton instance of QuarkusExtension.SummonRenderer
     */
    @Produces
    @Singleton
    fun produceSummonRenderer(): QuarkusExtension.SummonRenderer {
        return QuarkusExtension.SummonRenderer()
    }
}
package code.yousef.example.quarkus

import code.yousef.summon.integrations.quarkus.EnhancedQuarkusExtension
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
     * Produces an EnhancedSummonRenderer instance for injection.
     * This makes the EnhancedQuarkusExtension.EnhancedSummonRenderer available for CDI injection.
     * 
     * @return A singleton instance of EnhancedQuarkusExtension.EnhancedSummonRenderer
     */
    @Produces
    @Singleton
    fun produceSummonRenderer(): EnhancedQuarkusExtension.EnhancedSummonRenderer {
        return EnhancedQuarkusExtension.EnhancedSummonRenderer()
    }
}

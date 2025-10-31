package code.yousef.summon.integration.quarkus.deployment

import code.yousef.summon.integration.quarkus.QuarkusExtension
import io.quarkus.arc.deployment.AdditionalBeanBuildItem
import io.quarkus.deployment.annotations.BuildStep
import io.quarkus.deployment.builditem.FeatureBuildItem

/**
 * Deployment processor for the Summon Quarkus extension.
 * This class handles the build-time processing required to integrate
 * Summon with Quarkus applications.
 */
class SummonProcessor {

    companion object {
        private const val FEATURE = "summon"
    }

    /**
     * Register the Summon feature with Quarkus.
     */
    @BuildStep
    fun feature(): FeatureBuildItem {
        return FeatureBuildItem(FEATURE)
    }

    /**
     * Register beans for Summon services.
     */
    @BuildStep
    fun registerBeans(): AdditionalBeanBuildItem {
        return AdditionalBeanBuildItem.unremovableOf(QuarkusExtension.SummonRenderer::class.java)
    }

    /**
     * Register classes for reflection in native image.
     * Since we can't directly use the ReflectiveClassBuildItem constructors (they're package-private),
     * we'll just return null for now. This can be addressed in a future refactoring when the
     * integration with Quarkus is more mature.
     */
    @BuildStep
    fun registerReflection(): FeatureBuildItem {
        // This is a simple workaround for now
        // In a real integration, we would need to properly register the classes for reflection
        return FeatureBuildItem("summon-reflection")
    }
} 

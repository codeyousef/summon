package integrations.quarkus.deployment

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import runtime.Composable
import JvmPlatformRenderer
import integrations.quarkus.QuarkusExtension
import io.quarkus.arc.deployment.AdditionalBeanBuildItem
import io.quarkus.deployment.annotations.BuildStep
import io.quarkus.deployment.builditem.FeatureBuildItem
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem

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
     */
    @BuildStep
    fun registerReflection(): ReflectiveClassBuildItem {
        // Register Summon core classes for reflection
        return ReflectiveClassBuildItem.builder(
            Composable::class.java.name,
            JvmPlatformRenderer::class.java.name
        ).methods(true).fields(true).build()
    }
} 

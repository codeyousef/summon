package code.yousef.summon.integrations.quarkus

import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application

/**
 * Configures the base path for all JAX-RS resources.
 */
@ApplicationPath("/")
class SummonApplication : Application() 
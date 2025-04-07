package security.config

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import security.AuthenticationProvider
import security.JwtAuthenticationProvider

/**
 * Configuration class for security settings
 */
data class SecurityConfig(
    /**
     * The authentication provider to use
     */
    val authenticationProvider: AuthenticationProvider,

    /**
     * The URL to redirect to when authentication is required
     */
    val loginUrl: String = "/login",

    /**
     * The URL to redirect to after successful login
     */
    val defaultSuccessUrl: String = "/",

    /**
     * The URL to redirect to after logout
     */
    val logoutUrl: String = "/login",

    /**
     * Whether to use HTTPS
     */
    val requireHttps: Boolean = true,

    /**
     * The session timeout in seconds
     */
    val sessionTimeout: Long = 3600L,

    /**
     * CORS configuration
     */
    val corsConfig: CorsConfig = CorsConfig(),

    /**
     * CSRF configuration
     */
    val csrfConfig: CsrfConfig = CsrfConfig()
)

/**
 * Configuration for CORS settings
 */
data class CorsConfig(
    /**
     * Allowed origins
     */
    val allowedOrigins: Set<String> = setOf("*"),

    /**
     * Allowed methods
     */
    val allowedMethods: Set<String> = setOf("GET", "POST", "PUT", "DELETE", "OPTIONS"),

    /**
     * Allowed headers
     */
    val allowedHeaders: Set<String> = setOf("*"),

    /**
     * Whether to allow credentials
     */
    val allowCredentials: Boolean = true,

    /**
     * Max age of preflight requests
     */
    val maxAge: Long = 3600L
)

/**
 * Configuration for CSRF settings
 */
data class CsrfConfig(
    /**
     * Whether CSRF protection is enabled
     */
    val enabled: Boolean = true,

    /**
     * The name of the CSRF token header
     */
    val tokenHeaderName: String = "X-CSRF-TOKEN",

    /**
     * The name of the CSRF token cookie
     */
    val tokenCookieName: String = "XSRF-TOKEN"
)

/**
 * Builder for SecurityConfig
 */
class SecurityConfigBuilder {
    private var authenticationProvider: AuthenticationProvider? = null
    private var loginUrl: String = "/login"
    private var defaultSuccessUrl: String = "/"
    private var logoutUrl: String = "/login"
    private var requireHttps: Boolean = true
    private var sessionTimeout: Long = 3600L
    private var corsConfig: CorsConfig = CorsConfig()
    private var csrfConfig: CsrfConfig = CsrfConfig()

    fun authenticationProvider(provider: AuthenticationProvider) = apply {
        this.authenticationProvider = provider
    }

    fun loginUrl(url: String) = apply {
        this.loginUrl = url
    }

    fun defaultSuccessUrl(url: String) = apply {
        this.defaultSuccessUrl = url
    }

    fun logoutUrl(url: String) = apply {
        this.logoutUrl = url
    }

    fun requireHttps(required: Boolean) = apply {
        this.requireHttps = required
    }

    fun sessionTimeout(timeout: Long) = apply {
        this.sessionTimeout = timeout
    }

    fun corsConfig(config: CorsConfig) = apply {
        this.corsConfig = config
    }

    fun csrfConfig(config: CsrfConfig) = apply {
        this.csrfConfig = config
    }

    fun build(): SecurityConfig {
        requireNotNull(authenticationProvider) { "Authentication provider must be set" }
        return SecurityConfig(
            authenticationProvider = authenticationProvider!!,
            loginUrl = loginUrl,
            defaultSuccessUrl = defaultSuccessUrl,
            logoutUrl = logoutUrl,
            requireHttps = requireHttps,
            sessionTimeout = sessionTimeout,
            corsConfig = corsConfig,
            csrfConfig = csrfConfig
        )
    }
}

/**
 * Extension function to create a SecurityConfig
 */
fun securityConfig(init: SecurityConfigBuilder.() -> Unit): SecurityConfig {
    return SecurityConfigBuilder().apply(init).build()
}

/**
 * Extension function to create a JwtAuthenticationProvider
 */
fun createJwtAuthenticationProvider(
    apiBaseUrl: String,
    tokenExpiration: Long = 3600L
): JwtAuthenticationProvider {
    return JwtAuthenticationProvider(apiBaseUrl, tokenExpiration)
} 

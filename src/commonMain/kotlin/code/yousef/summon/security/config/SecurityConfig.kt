package security.config

import security.AuthenticationProvider
import security.JwtAuthenticationProvider

/**
 * Configuration class for security settings.
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
 * Configuration for CORS settings.
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
 * Configuration for CSRF settings.
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
 * Builder for SecurityConfig.
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

    fun authenticationProvider(provider: AuthenticationProvider): SecurityConfigBuilder {
        this.authenticationProvider = provider
        return this
    }

    fun loginUrl(url: String): SecurityConfigBuilder {
        this.loginUrl = url
        return this
    }

    fun defaultSuccessUrl(url: String): SecurityConfigBuilder {
        this.defaultSuccessUrl = url
        return this
    }

    fun logoutUrl(url: String): SecurityConfigBuilder {
        this.logoutUrl = url
        return this
    }

    fun requireHttps(required: Boolean): SecurityConfigBuilder {
        this.requireHttps = required
        return this
    }

    fun sessionTimeout(timeout: Long): SecurityConfigBuilder {
        this.sessionTimeout = timeout
        return this
    }

    fun corsConfig(config: CorsConfig): SecurityConfigBuilder {
        this.corsConfig = config
        return this
    }

    fun csrfConfig(config: CsrfConfig): SecurityConfigBuilder {
        this.csrfConfig = config
        return this
    }

    fun build(): SecurityConfig {
        requireNotNull(authenticationProvider) { "Authentication provider is required for security configuration" }
        
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
 * Extension function to create a SecurityConfig using a builder.
 */
fun securityConfig(init: SecurityConfigBuilder.() -> Unit): SecurityConfig {
    val builder = SecurityConfigBuilder()
    builder.init()
    return builder.build()
}

/**
 * Extension function to create a JwtAuthenticationProvider.
 */
fun createJwtAuthenticationProvider(
    apiBaseUrl: String,
    tokenExpiration: Long = 3600L
): JwtAuthenticationProvider {
    return JwtAuthenticationProvider(apiBaseUrl, tokenExpiration)
} 

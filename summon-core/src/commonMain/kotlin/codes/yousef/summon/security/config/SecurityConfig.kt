package codes.yousef.summon.security.config

import codes.yousef.summon.security.AuthenticationProvider
import codes.yousef.summon.security.JwtAuthenticationProvider

/**
 * Configuration class for security settings.

 * @property authenticationProvider The authentication provider value.
 * @property loginUrl The login url value.
 * @property defaultSuccessUrl The default success url value.
 * @property logoutUrl The logout url value.
 * @property requireHttps The require https value.
 * @property sessionTimeout The session timeout value.
 * @property corsConfig The cors config value.
 * @property csrfConfig The csrf config value.
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

 * @property allowedOrigins The allowed origins value.
 * @property allowedMethods The allowed methods value.
 * @property allowedHeaders The allowed headers value.
 * @property allowCredentials The allow credentials value.
 * @property maxAge The max age value.
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

 * @property enabled Whether the behavior is enabled.
 * @property tokenHeaderName The token header name value.
 * @property tokenCookieName The token cookie name value.
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

    /**
     * Executes the authentication provider operation.
     *
     * @param provider The provider value.
     * @return The resulting value.
     */
    fun authenticationProvider(provider: AuthenticationProvider): SecurityConfigBuilder {
        this.authenticationProvider = provider
        return this
    }

    /**
     * Executes the login URL operation.
     *
     * @param url Target URL.
     * @return The resulting value.
     */
    fun loginUrl(url: String): SecurityConfigBuilder {
        this.loginUrl = url
        return this
    }

    /**
     * Executes the default success URL operation.
     *
     * @param url Target URL.
     * @return The resulting value.
     */
    fun defaultSuccessUrl(url: String): SecurityConfigBuilder {
        this.defaultSuccessUrl = url
        return this
    }

    /**
     * Executes the logout URL operation.
     *
     * @param url Target URL.
     * @return The resulting value.
     */
    fun logoutUrl(url: String): SecurityConfigBuilder {
        this.logoutUrl = url
        return this
    }

    /**
     * Executes the require HTTPS operation.
     *
     * @param required The required value.
     * @return The resulting value.
     */
    fun requireHttps(required: Boolean): SecurityConfigBuilder {
        this.requireHttps = required
        return this
    }

    /**
     * Executes the session timeout operation.
     *
     * @param timeout Timeout in milliseconds.
     * @return The resulting value.
     */
    fun sessionTimeout(timeout: Long): SecurityConfigBuilder {
        this.sessionTimeout = timeout
        return this
    }

    /**
     * Executes the cors config operation.
     *
     * @param config The config value.
     * @return The resulting value.
     */
    fun corsConfig(config: CorsConfig): SecurityConfigBuilder {
        this.corsConfig = config
        return this
    }

    /**
     * Executes the CSRF config operation.
     *
     * @param config The config value.
     * @return The resulting value.
     */
    fun csrfConfig(config: CsrfConfig): SecurityConfigBuilder {
        this.csrfConfig = config
        return this
    }

    /**
     * Builds the operation.
     *
     * @return The resulting value.
     */
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

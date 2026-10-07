package codes.yousef.summon.components.auth

import codes.yousef.summon.routing.Router
import codes.yousef.summon.security.AuthenticationResult
import codes.yousef.summon.security.UsernamePasswordCredentials
import codes.yousef.summon.security.service.SecurityService

/**
 * A component that handles user login

 * @property securityService The security service value.
 * @property router The router value.
 */
class LoginComponent(
    private val securityService: SecurityService,
    private val router: Router
) {
    /**
     * Attempts to log in a user with the provided credentials
     */
    suspend fun login(username: String, password: String): LoginResult {
        val credentials = UsernamePasswordCredentials(username, password)
        return when (val result = securityService.login(credentials)) {
            is AuthenticationResult.Success -> {
                LoginResult.Success
            }

            is AuthenticationResult.Failure -> {
                LoginResult.Failure(result.error)
            }
        }
    }

    /**
     * Logs out the current user
     */
    suspend fun logout() {
        securityService.logout()
        router.navigate("/login")
    }

    /**
     * Result of a login attempt
     */
    sealed class LoginResult {
        /** Authentication succeeded. */
        object Success : LoginResult()
/**
 * Authentication failed.
 *
 * @property error failure retained for the caller
 */
        data class Failure(val error: Throwable) : LoginResult()
    }
}

/**
 * Extension function to create a LoginComponent
 */
fun createLoginComponent(
    securityService: SecurityService,
    router: Router
): LoginComponent {
    return LoginComponent(securityService, router)
}

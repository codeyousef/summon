package code.yousef.summon.security.components

import code.yousef.summon.routing.Router
import code.yousef.summon.security.AuthenticationResult
import code.yousef.summon.security.UsernamePasswordCredentials
import code.yousef.summon.security.service.SecurityService

/**
 * A component that handles user login
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
        return when (val result = securityService.authenticate(credentials)) {
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
        object Success : LoginResult()
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
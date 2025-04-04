package code.yousef.summon.examples

import code.yousef.summon.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Example demonstrating all state management features in Summon.
 */
class StateManagementExample : Composable {

    // Example ViewModel for the user profile
    class UserProfileViewModel : ViewModel() {
        val username = state("username", "Guest")
        val email = state("email", "")
        val isLoggedIn = state("isLoggedIn", false)

        fun login(user: String, emailAddress: String) {
            username.value = user
            email.value = emailAddress
            isLoggedIn.value = true
        }

        fun logout() {
            username.value = "Guest"
            email.value = ""
            isLoggedIn.value = false
        }
    }

    // Demonstrates usage of RememberSaveable
    private val counter = rememberSaveable("counter", 0)
    private val inputText = rememberSaveable("inputText", "")

    // Demonstrates usage of ViewModel
    private val userViewModel = viewModel { UserProfileViewModel() }

    // Demonstrates usage of Flow binding
    private val flowCounter = flow {
        var count = 0
        while (true) {
            emit(count++)
            delay(1000) // Emit a new value every second
        }
    }.toState(initialValue = 0)

    // Demonstrates usage of StateFlow
    private val temperatureStateFlow = MutableStateFlow(22.5)
    private val temperature = stateFromStateFlow(temperatureStateFlow)

    override fun <T> compose(receiver: T): T {
        // Create a Column composable to hold our example UI
        return Column(
            modifier = Modifier().padding("20px"),
            content = listOf(
                // Section header
                h2Text("State Management Examples"),

                // RememberSaveable example
                Card(
                    modifier = Modifier().padding("16px").marginBottom("20px"),
                    content = listOf(
                        h3Text("RememberSaveable Example"),
                        Text("Count: ${counter.value}"),
                        Row(
                            modifier = Modifier().marginTop("8px"),
                            content = listOf(
                                Button(
                                    label = "Increment",
                                    onClick = { counter.value++ }
                                ),
                                Button(
                                    label = "Reset",
                                    onClick = { counter.value = 0 },
                                    modifier = Modifier().marginLeft("8px")
                                )
                            )
                        ),
                        TextField(
                            placeholder = "Type something (persisted)",
                            state = inputText,
                            onValueChange = { inputText.value = it },
                            modifier = Modifier().marginTop("8px").width("100%")
                        )
                    )
                ),

                // ViewModel example
                Card(
                    modifier = Modifier().padding("16px").marginBottom("20px"),
                    content = listOf(
                        h3Text("ViewModel Example"),
                        Text("Username: ${userViewModel.username.value}"),
                        Text("Email: ${userViewModel.email.value}"),
                        Text("Logged in: ${userViewModel.isLoggedIn.value}"),
                        Row(
                            modifier = Modifier().marginTop("8px"),
                            content = listOf(
                                Button(
                                    label = "Login as Demo User",
                                    onClick = { userViewModel.login("DemoUser", "demo@example.com") }
                                ),
                                Button(
                                    label = "Logout",
                                    onClick = { userViewModel.logout() },
                                    modifier = Modifier().marginLeft("8px")
                                )
                            )
                        )
                    )
                ),

                // Flow binding example
                Card(
                    modifier = Modifier().padding("16px").marginBottom("20px"),
                    content = listOf(
                        h3Text("Flow Binding Example"),
                        Text("Auto-incrementing counter: ${flowCounter.value}"),
                        Text("This counter updates automatically from a Flow")
                    )
                ),

                // StateFlow example
                Card(
                    modifier = Modifier().padding("16px").marginBottom("20px"),
                    content = listOf(
                        h3Text("StateFlow Example"),
                        Text("Temperature: ${temperature.value}°C"),
                        Row(
                            modifier = Modifier().marginTop("8px"),
                            content = listOf(
                                Button(
                                    label = "Increase Temp",
                                    onClick = { temperatureStateFlow.value += 0.5 }
                                ),
                                Button(
                                    label = "Decrease Temp",
                                    onClick = { temperatureStateFlow.value -= 0.5 },
                                    modifier = Modifier().marginLeft("8px")
                                )
                            )
                        )
                    )
                )
            )
        ).compose(receiver)
    }
} 
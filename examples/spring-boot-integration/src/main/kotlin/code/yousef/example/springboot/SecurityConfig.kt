package code.yousef.example.springboot

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/**
 * Security configuration for the Spring Boot example application.
 * This configuration secures only the /admin path and allows all other paths to be accessed without authentication.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    /**
     * Configure security filter chain to secure only the /admin path.
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/admin/**").authenticated()
                    .requestMatchers("/", "/api/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/error").permitAll()
                    .anyRequest().permitAll()
            }
            .formLogin { form ->
                form
                    .defaultSuccessUrl("/admin", true)
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("/")
                    .permitAll()
            }

        return http.build()
    }

    /**
     * Configure in-memory user details service with a default admin user.
     */
    @Bean
    fun userDetailsService(): UserDetailsService {
        val userDetails = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("password")
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(userDetails)
    }
}
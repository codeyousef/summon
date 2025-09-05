package code.yousef.example.springboot

import code.yousef.example.springboot.security.JwtAuthenticationFilter
import code.yousef.example.springboot.service.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    
    @Autowired
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
    
    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customUserDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests { requests ->
                requests
                    // Public pages and assets
                    .requestMatchers("/", "/auth", "/dashboard", "/contact", "/chat", "/todos", "/test-buttons", "/debug-hydration").permitAll()
                    .requestMatchers("/api/health", "/api/info", "/health").permitAll()
                    .requestMatchers("/static/**", "/css/**", "/js/**", "/app.js", "/styles.css", "/summon-hydration.js", "/*.js").permitAll()
                    .requestMatchers("/h2-console/**").permitAll() // For development only
                    
                    // Authentication endpoints - public for login/register, but secure user-specific ones
                    .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/logout").permitAll()
                    .requestMatchers("/api/auth/me", "/api/auth/settings").authenticated()
                    
                    // User management pages - READ access public, but WRITE operations should be authenticated
                    .requestMatchers("GET", "/users", "/users/**").permitAll() // Allow all GET requests to user endpoints
                    .requestMatchers("POST", "/users/**").authenticated() // Create, update, delete users
                    
                    // Todo endpoints require authentication
                    .requestMatchers("/api/todos/**").authenticated()
                    
                    // Summon hydration callback - public but should have rate limiting in production
                    .requestMatchers("/summon/callback").permitAll()
                    
                    // Static resource handler
                    .requestMatchers("/summon-hydration.js", "/*.js").permitAll()
                    
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .headers { headers -> headers.frameOptions().disable() } // For H2 console

        return http.build()
    }
}
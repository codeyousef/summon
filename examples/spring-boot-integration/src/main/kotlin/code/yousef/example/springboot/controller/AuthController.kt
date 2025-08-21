package code.yousef.example.springboot.controller

import code.yousef.example.springboot.models.*
import code.yousef.example.springboot.service.AuthService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])
class AuthController {
    
    @Autowired
    private lateinit var authService: AuthService
    
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return if (response.success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return if (response.success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }
    
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<ApiResponse> {
        val user = authService.getCurrentUser(authentication.name)
        return if (user != null) {
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "User retrieved successfully",
                data = user.toDto()
            ))
        } else {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "User not found"
            ))
        }
    }
    
    @PutMapping("/settings")
    fun updateSettings(
        @Valid @RequestBody request: UpdateUserSettingsRequest,
        authentication: Authentication?
    ): ResponseEntity<ApiResponse> {
        // If not authenticated, return error
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse(
                success = false,
                message = "Authentication required to update settings"
            ))
        }
        
        val user = authService.updateUserSettings(authentication.name, request)
        return if (user != null) {
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Settings updated successfully",
                data = user.toDto()
            ))
        } else {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "Failed to update settings"
            ))
        }
    }
    
    @PostMapping("/logout")
    fun logout(): ResponseEntity<ApiResponse> {
        // With JWT, logout is handled client-side by removing the token
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Logout successful"
        ))
    }
}
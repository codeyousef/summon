package code.yousef.summon.core.error

/**
 * Standard error handling utilities for the Summon framework.
 * Provides consistent error handling patterns across the codebase.
 */

/**
 * Base class for Summon framework errors.
 */
open class SummonException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown when a required configuration is missing or invalid.
 */
class ConfigurationException(
    message: String,
    cause: Throwable? = null
) : SummonException(message, cause)

/**
 * Exception thrown when a required component or service is not available.
 */
class ComponentNotFoundException(
    componentName: String,
    cause: Throwable? = null
) : SummonException("Component not found: $componentName", cause)

/**
 * Exception thrown when validation fails.
 */
class ValidationException(
    message: String,
    val errors: List<String> = emptyList(),
    cause: Throwable? = null
) : SummonException(message, cause)

/**
 * Exception thrown when a renderer operation fails.
 */
class RenderException(
    message: String,
    cause: Throwable? = null
) : SummonException(message, cause)

/**
 * Result type for operations that can fail.
 * Provides a type-safe way to handle errors without exceptions.
 */
sealed class SummonResult<T, E> {
    data class Success<T, E>(val value: T) : SummonResult<T, E>()
    data class Failure<T, E>(val error: E) : SummonResult<T, E>()
    
    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure
    
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }
    
    fun getErrorOrNull(): E? = when (this) {
        is Success -> null
        is Failure -> error
    }
    
    inline fun getOrElse(default: () -> T): T = when (this) {
        is Success -> value
        is Failure -> default()
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> value
        is Failure -> throw SummonException("Operation failed: $error")
    }
}

/**
 * Utility functions for error handling
 */
object ErrorHandler {
    /**
     * Safely executes a block and returns a SummonResult.
     */
    inline fun <T> runCatching(block: () -> T): SummonResult<T, Throwable> {
        return try {
            SummonResult.Success(block())
        } catch (e: Throwable) {
            SummonResult.Failure(e)
        }
    }
    
    /**
     * Validates a condition and returns a SummonResult.
     */
    inline fun validate(
        condition: Boolean,
        lazyMessage: () -> String
    ): SummonResult<Unit, ValidationException> {
        return if (condition) {
            SummonResult.Success(Unit)
        } else {
            SummonResult.Failure(ValidationException(lazyMessage()))
        }
    }
    
    /**
     * Validates multiple conditions and returns a SummonResult with all errors.
     */
    fun validateAll(vararg validations: Pair<Boolean, String>): SummonResult<Unit, ValidationException> {
        val errors = validations.filterNot { it.first }.map { it.second }
        return if (errors.isEmpty()) {
            SummonResult.Success(Unit)
        } else {
            SummonResult.Failure(ValidationException("Validation failed", errors))
        }
    }
    
    /**
     * Requires a non-null value or returns a failure.
     */
    fun <T> requireNotNull(
        value: T?,
        lazyMessage: () -> String
    ): SummonResult<T, ConfigurationException> {
        return if (value != null) {
            SummonResult.Success(value)
        } else {
            SummonResult.Failure(ConfigurationException(lazyMessage()))
        }
    }
    
    /**
     * Standard error messages
     */
    object Messages {
        const val RENDERER_NOT_PROVIDED = "No PlatformRenderer provided in the current composition"
        const val INVALID_CONFIGURATION = "Invalid configuration provided"
        const val COMPONENT_NOT_INITIALIZED = "Component has not been initialized"
        const val OPERATION_NOT_SUPPORTED = "Operation not supported on this platform"
        const val VALIDATION_FAILED = "Validation failed"
        
        fun componentNotFound(name: String) = "Component not found: $name"
        fun invalidValue(name: String, value: Any?) = "Invalid value for $name: $value"
        fun outOfRange(name: String, value: Number, min: Number, max: Number) = 
            "$name must be between $min and $max, but was $value"
    }
}

/**
 * Extension functions for common error handling patterns
 */

/**
 * Validates that a value is within a specified range.
 */
fun Int.requireInRange(range: IntRange, lazyMessage: () -> String): Int {
    require(this in range, lazyMessage)
    return this
}

/**
 * Validates that a value is positive.
 */
fun Int.requirePositive(lazyMessage: () -> String = { "Value must be positive" }): Int {
    require(this > 0, lazyMessage)
    return this
}

/**
 * Validates that a string is not blank.
 */
fun String.requireNotBlank(lazyMessage: () -> String = { "String must not be blank" }): String {
    require(this.isNotBlank(), lazyMessage)
    return this
}

/**
 * Converts a nullable value to a SummonResult.
 */
fun <T> T?.toSummonResult(errorMessage: String = "Value is null"): SummonResult<T, String> {
    return if (this != null) {
        SummonResult.Success(this)
    } else {
        SummonResult.Failure(errorMessage)
    }
}
package code.yousef.example.portfolio

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

/**
 * Simple test to verify the application starts correctly
 */
@QuarkusTest
class SimpleHealthTest {

    @Test
    fun testHealthEndpoint() {
        given()
            .`when`().get("/q/health")
            .then()
            .statusCode(200)
    }

    @Test
    fun testApplicationStarts() {
        // If we get here, the application started successfully
        assert(true) { "Application started successfully" }
    }
}
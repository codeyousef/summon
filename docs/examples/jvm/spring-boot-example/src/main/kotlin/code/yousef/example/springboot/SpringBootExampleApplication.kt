package code.yousef.example.springboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Main Spring Boot application class for the Summon integration example.
 * 
 * This example demonstrates how to use the Summon UI library with Spring Boot
 * and Thymeleaf to build reactive web applications.
 */
@SpringBootApplication
class SpringBootExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringBootExampleApplication>(*args)
}
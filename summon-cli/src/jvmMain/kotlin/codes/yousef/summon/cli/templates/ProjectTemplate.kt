package codes.yousef.summon.cli.templates

import codes.yousef.summon.cli.util.VersionReader
import kotlinx.serialization.Serializable

/**
 * Represents a project template with metadata and file structure information.
 */
@Serializable
data class ProjectTemplate(
    val name: String,
    val description: String,
    val type: String,
    val tags: List<String> = emptyList(),
    val variables: Map<String, TemplateVariable> = emptyMap(),
    val files: List<TemplateFile> = emptyList(),
    val dependencies: TemplateDependencies = TemplateDependencies(),
    val postSetupInstructions: List<String> = emptyList()
) {
    companion object {
        /**
         * Factory method to create templates based on type
         */
        fun fromType(type: String): ProjectTemplate {
            return when (type) {
                "js" -> createJsTemplate()
                "quarkus" -> createQuarkusTemplate()
                "spring-boot" -> createSpringBootTemplate()
                "ktor" -> createKtorTemplate()
                "library" -> createLibraryTemplate()
                "example" -> createExampleTemplate()
                else -> createBasicTemplate()
            }
        }

        private fun createJsTemplate() = ProjectTemplate(
            name = "JavaScript/Browser App",
            description = "A browser-only Summon application with JavaScript target",
            type = "js",
            tags = listOf("frontend", "browser", "spa"),
            variables = mapOf(
                "PROJECT_NAME" to TemplateVariable("Project name", "string", "summon-js-app"),
                "PACKAGE_NAME" to TemplateVariable("Package name", "string", "com.example.app"),
                "APP_TITLE" to TemplateVariable("Application title", "string", "My Summon App")
            ),
            dependencies = TemplateDependencies(
                kotlin = listOf("kotlin-stdlib-js"),
                summon = listOf("codes.yousef:summon:0.4.8.8"),
                npm = listOf("core-js@3.46.0")
            ),
            postSetupInstructions = listOf(
                "Run './gradlew jsBrowserDevelopmentRun' to start development server",
                "Open http://localhost:8080 to view your app",
                "Edit src/jsMain/kotlin/ to modify components"
            )
        )

        private fun createQuarkusTemplate() = ProjectTemplate(
            name = "Quarkus Full-Stack App",
            description = "A full-stack application with Quarkus backend and Summon frontend",
            type = "quarkus",
            tags = listOf("fullstack", "backend", "quarkus", "rest"),
            variables = mapOf(
                "PROJECT_NAME" to TemplateVariable("Project name", "string", "summon-quarkus-app"),
                "PACKAGE_NAME" to TemplateVariable("Package name", "string", "com.example.app"),
                "APP_TITLE" to TemplateVariable("Application title", "string", "My Quarkus App"),
                "QUARKUS_VERSION" to TemplateVariable("Quarkus version", "string", "3.15.7")
            ),
            dependencies = TemplateDependencies(
                kotlin = listOf("kotlin-stdlib-jdk8", "kotlin-stdlib-js"),
                summon = listOf("codes.yousef:summon:0.4.8.8"),
                quarkus = listOf("quarkus-core", "quarkus-qute", "quarkus-kotlin", "quarkus-rest")
            ),
            postSetupInstructions = listOf(
                "Run './gradlew quarkusDev' to start in development mode",
                "Backend API available at http://localhost:8080/api",
                "Frontend available at http://localhost:8080",
                "Hot reload enabled for both frontend and backend"
            )
        )

        private fun createSpringBootTemplate() = ProjectTemplate(
            name = "Spring Boot Full-Stack App",
            description = "A full-stack application with Spring Boot backend and Summon frontend",
            type = "spring-boot",
            tags = listOf("fullstack", "backend", "spring", "rest"),
            variables = mapOf(
                "PROJECT_NAME" to TemplateVariable("Project name", "string", "summon-spring-app"),
                "PACKAGE_NAME" to TemplateVariable("Package name", "string", "com.example.app"),
                "APP_TITLE" to TemplateVariable("Application title", "string", "My Spring App"),
                "SPRING_BOOT_VERSION" to TemplateVariable("Spring Boot version", "string", "3.5.7")
            ),
            dependencies = TemplateDependencies(
                kotlin = listOf("kotlin-stdlib-jdk8", "kotlin-stdlib-js"),
                summon = listOf("codes.yousef:summon:0.4.8.8"),
                spring = listOf(
                    "spring-boot-starter-web",
                    "spring-boot-starter-thymeleaf",
                    "spring-boot-starter-webflux"
                )
            ),
            postSetupInstructions = listOf(
                "Run './gradlew bootRun' to start the application",
                "Backend API available at http://localhost:8080/api",
                "Frontend available at http://localhost:8080",
                "Use './gradlew bootJar' to create executable JAR"
            )
        )

        private fun createKtorTemplate() = ProjectTemplate(
            name = "Ktor Full-Stack App",
            description = "A full-stack application with Ktor backend and Summon frontend",
            type = "ktor",
            tags = listOf("fullstack", "backend", "ktor", "rest"),
            variables = mapOf(
                "PROJECT_NAME" to TemplateVariable("Project name", "string", "summon-ktor-app"),
                "PACKAGE_NAME" to TemplateVariable("Package name", "string", "com.example.app"),
                "APP_TITLE" to TemplateVariable("Application title", "string", "My Ktor App"),
                "KTOR_VERSION" to TemplateVariable("Ktor version", "string", "3.3.1")
            ),
            dependencies = TemplateDependencies(
                kotlin = listOf("kotlin-stdlib-jdk8", "kotlin-stdlib-js"),
                summon = listOf("codes.yousef:summon:0.4.8.8"),
                ktor = listOf("ktor-server-core", "ktor-server-netty", "ktor-server-html-builder")
            ),
            postSetupInstructions = listOf(
                "Run './gradlew run' to start the server",
                "Backend API available at http://localhost:8080/api",
                "Frontend available at http://localhost:8080",
                "Use './gradlew installDist' to create distribution"
            )
        )

        private fun createLibraryTemplate() = ProjectTemplate(
            name = "Component Library",
            description = "A Summon component library for sharing across projects",
            type = "library",
            tags = listOf("library", "components", "multiplatform"),
            variables = mapOf(
                "LIBRARY_NAME" to TemplateVariable("Library name", "string", "summon-components"),
                "PACKAGE_NAME" to TemplateVariable("Package name", "string", "com.example.components"),
                "DESCRIPTION" to TemplateVariable("Library description", "string", "My Summon Component Library")
            ),
            dependencies = TemplateDependencies(
                kotlin = listOf("kotlin-stdlib-common", "kotlin-stdlib-jdk8", "kotlin-stdlib-js"),
                summon = listOf("codes.yousef:summon:${VersionReader.readVersion()}")
            ),
            postSetupInstructions = listOf(
                "Run './gradlew build' to build the library",
                "Run './gradlew publishToMavenLocal' to publish locally",
                "Add components in src/commonMain/kotlin/",
                "Add tests in src/commonTest/kotlin/"
            )
        )

        private fun createExampleTemplate() = ProjectTemplate(
            name = "Example Project",
            description = "Example project showcasing various Summon components and patterns",
            type = "example",
            tags = listOf("example", "showcase", "tutorial"),
            variables = mapOf(
                "PROJECT_NAME" to TemplateVariable("Project name", "string", "summon-examples"),
                "PACKAGE_NAME" to TemplateVariable("Package name", "string", "com.example.demo")
            ),
            dependencies = TemplateDependencies(
                kotlin = listOf("kotlin-stdlib-js"),
                summon = listOf("codes.yousef:summon:${VersionReader.readVersion()}")
            ),
            postSetupInstructions = listOf(
                "Run './gradlew jsBrowserDevelopmentRun' to see examples",
                "Browse through src/jsMain/kotlin/examples/",
                "Each example demonstrates different Summon features"
            )
        )

        private fun createBasicTemplate() = ProjectTemplate(
            name = "Basic Multiplatform Project",
            description = "A basic Kotlin Multiplatform project with Summon",
            type = "basic",
            tags = listOf("multiplatform", "basic"),
            variables = mapOf(
                "PROJECT_NAME" to TemplateVariable("Project name", "string", "summon-app"),
                "PACKAGE_NAME" to TemplateVariable("Package name", "string", "com.example.app")
            ),
            dependencies = TemplateDependencies(
                kotlin = listOf("kotlin-stdlib-common", "kotlin-stdlib-jdk8", "kotlin-stdlib-js"),
                summon = listOf("codes.yousef:summon:${VersionReader.readVersion()}")
            ),
            postSetupInstructions = listOf(
                "Run './gradlew build' to build all targets",
                "Run './gradlew jsBrowserDevelopmentRun' for browser development",
                "Run './gradlew run' for JVM development"
            )
        )
    }
}

@Serializable
data class TemplateVariable(
    val description: String,
    val type: String,
    val defaultValue: String,
    val required: Boolean = true
)

@Serializable
data class TemplateFile(
    val sourcePath: String,
    val targetPath: String,
    val processAsTemplate: Boolean = true,
    val executable: Boolean = false
)

@Serializable
data class TemplateDependencies(
    val kotlin: List<String> = emptyList(),
    val summon: List<String> = emptyList(),
    val quarkus: List<String> = emptyList(),
    val spring: List<String> = emptyList(),
    val ktor: List<String> = emptyList(),
    val npm: List<String> = emptyList(),
    val gradle: List<String> = emptyList()
)

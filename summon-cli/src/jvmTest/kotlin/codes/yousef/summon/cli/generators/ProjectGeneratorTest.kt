package codes.yousef.summon.cli.generators

import codes.yousef.summon.cli.templates.ProjectTemplate
import codes.yousef.summon.cli.util.VersionReader
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.*

/**
 * TDD tests for ProjectGenerator to ensure generated projects compile correctly
 * Addresses GitHub issue #14: Generated projects have compilation errors
 */
class ProjectGeneratorTest {

    private lateinit var tempDir: File

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory("summon-test-").toFile()
    }

    @AfterTest
    fun cleanup() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `test version reads from version properties file`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val buildFile = File(tempDir, "build.gradle.kts")
        assertTrue(buildFile.exists(), "build.gradle.kts should exist")

        val content = buildFile.readText()
        val currentVersion = VersionReader.readVersion()

        // Should use version from version.properties, not hardcoded 0.4.0.0
        assertFalse(
            content.contains("summon:0.4.0.0"),
            "Generated build file should not use hardcoded old version 0.4.0.0"
        )
        assertTrue(
            content.contains("summon:$currentVersion"),
            "Generated build file should use current version $currentVersion from version.properties"
        )

        assertTrue(
            content.contains("kotlin(\"multiplatform\") version \"2.3.0\"") &&
                    content.contains("kotlin(\"plugin.serialization\") version \"2.3.0\""),
            "Generated build file should target Kotlin 2.3.0"
        )

        assertTrue(
            content.contains("org.jetbrains.kotlin:kotlin-stdlib-js:2.3.0"),
            "Generated JS template should include Kotlin stdlib for JS"
        )
    }

    @Test
    fun `test generated Main uses Modifier constructor correctly`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/test/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist")

        val content = mainFile.readText()

        // Should use Modifier() constructor, not bare Modifier
        assertFalse(
            content.contains("= Modifier.padding"),
            "Should not use Modifier as companion object"
        )
        assertTrue(
            content.contains("Modifier().padding"),
            "Should use Modifier() constructor with padding method"
        )
    }

    @Test
    fun `test generated Main uses correct Button API with label parameter`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/test/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist")

        val content = mainFile.readText()

        // Button should use 'label' parameter, not 'text'
        assertFalse(
            content.contains("Button(\n            text ="),
            "Button should not use 'text' parameter"
        )

        // Button API: Button(onClick, label, modifier, ...)
        assertTrue(
            content.contains("Button(") && content.contains("label ="),
            "Button should use 'label' parameter"
        )

        // onClick should come before label per API
        val buttonIndex = content.indexOf("Button(")
        if (buttonIndex != -1) {
            // We can't just look for the first ')' because the lambda might contain function calls like println()
            // So we'll look at a larger chunk of text, or just check relative positions in the file
            // since there's only one Button in the generated Main.kt
            
            val onClickPos = content.indexOf("onClick", buttonIndex)
            val labelPos = content.indexOf("label", buttonIndex)
            
            assertTrue(onClickPos != -1, "Button should have onClick parameter")
            assertTrue(labelPos != -1, "Button should have label parameter")
            
            assertTrue(
                onClickPos < labelPos,
                "onClick parameter should come before label parameter"
            )
        }
    }

    @Test
    fun `test generated ExampleComponent uses Modifier constructor`() {
        val template = ProjectTemplate.fromType("library")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "test-lib",
            packageName = "com.example.lib",
            targetDirectory = tempDir,
            templateType = "library",
            minimal = false
        )

        generator.generate(config)

        val exampleFile = File(tempDir, "src/commonMain/kotlin/com/example/lib/ExampleComponent.kt")
        assertTrue(exampleFile.exists(), "ExampleComponent.kt should exist")

        val content = exampleFile.readText()

        // Default parameter should use Modifier() constructor
        assertTrue(
            content.contains("modifier: Modifier = Modifier()"),
            "ExampleComponent should use Modifier() constructor as default parameter"
        )

        // Should not use bare Modifier as default
        assertFalse(
            content.contains("modifier: Modifier = Modifier\n") ||
                    content.contains("modifier: Modifier = Modifier)"),
            "Should not use bare Modifier without constructor call"
        )
    }

    @Test
    fun `test generated code has correct imports`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/test/Main.kt")
        val content = mainFile.readText()

        // Should import Modifier
        assertTrue(
            content.contains("import codes.yousef.summon.modifier.Modifier"),
            "Should import Modifier class"
        )

        // Should import padding extension
        assertTrue(
            content.contains("import codes.yousef.summon.modifier.padding"),
            "Should import padding extension function"
        )
    }

    @Test
    fun `test minimal project uses correct API`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "minimal-app",
            packageName = "com.example.minimal",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = true
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/minimal/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist for minimal project")

        val content = mainFile.readText()

        // Even minimal should use the current render API
        assertTrue(
            content.contains("renderComposableRoot("),
            "Should use renderComposableRoot API"
        )
    }

    @Test
    fun `test basic multiplatform template adds kotlin stdlib dependencies`() {
        val template = ProjectTemplate.fromType("basic")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "mp-app",
            packageName = "com.example.mp",
            targetDirectory = tempDir,
            templateType = "basic",
            minimal = false
        )

        generator.generate(config)

        val buildFile = File(tempDir, "build.gradle.kts")
        assertTrue(buildFile.exists(), "build.gradle.kts should exist for basic template")

        val content = buildFile.readText()
        assertTrue(
            content.contains("org.jetbrains.kotlin:kotlin-stdlib-common:2.3.0"),
            "Multiplatform template should include Kotlin stdlib-common"
        )
        assertTrue(
            content.contains("org.jetbrains.kotlin:kotlin-stdlib:2.3.0"),
            "Multiplatform template should include Kotlin stdlib for JVM"
        )
        assertTrue(
            content.contains("org.jetbrains.kotlin:kotlin-stdlib-js:2.3.0"),
            "Multiplatform template should include Kotlin stdlib for JS"
        )
    }

    @Test
    fun `test library project ExampleComponent default parameter`() {
        val template = ProjectTemplate.fromType("library")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "my-lib",
            packageName = "com.example.mylib",
            targetDirectory = tempDir,
            templateType = "library",
            minimal = false
        )

        generator.generate(config)

        val componentFile = File(tempDir, "src/commonMain/kotlin/com/example/mylib/ExampleComponent.kt")
        assertTrue(componentFile.exists())

        val content = componentFile.readText()

        // Check default parameter syntax
        val modifierParamPattern = Regex("""modifier:\s*Modifier\s*=\s*Modifier\(\)""")
        assertTrue(
            modifierParamPattern.containsMatchIn(content),
            "ExampleComponent should have 'modifier: Modifier = Modifier()' with constructor"
        )
    }

    @Test
    fun `test generated ButtonExamples uses correct API`() {
        val template = ProjectTemplate.fromType("example")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "example-app",
            packageName = "com.example.app",
            targetDirectory = tempDir,
            templateType = "example",
            includeExamples = true,
            minimal = false
        )

        generator.generate(config)

        val examplesFile = File(tempDir, "src/jsMain/kotlin/com/example/app/examples/ButtonExamples.kt")
        if (examplesFile.exists()) {
            val content = examplesFile.readText()

            // Button should use label parameter
            assertTrue(
                content.contains("label =") || content.contains("text ="),
                "ButtonExamples should use Button API"
            )

            // Should use Modifier() constructor
            if (content.contains("Modifier")) {
                assertTrue(
                    content.contains("Modifier()"),
                    "ButtonExamples should use Modifier() constructor"
                )
            }
        }
    }

    @Test
    fun `test all template types generate valid Kotlin code structure`() {
        val templateTypes = listOf("js", "library", "multiplatform")

        for (type in templateTypes) {
            val typeDir = File(tempDir, type)
            typeDir.mkdirs()

            val template = ProjectTemplate.fromType(type)
            val generator = ProjectGenerator(template)

            val config = ProjectGenerator.Config(
                projectName = "test-$type",
                packageName = "com.example.$type",
                targetDirectory = typeDir,
                templateType = type,
                minimal = false
            )

            generator.generate(config)

            // Verify basic structure
            assertTrue(
                File(typeDir, "build.gradle.kts").exists(),
                "$type template should generate build.gradle.kts"
            )
            assertTrue(
                File(typeDir, "settings.gradle.kts").exists(),
                "$type template should generate settings.gradle.kts"
            )
        }
    }

    @Test
    fun `test generated JS project code syntax is valid`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)

        val projectDir = File(tempDir, "syntax-test")
        val config = ProjectGenerator.Config(
            projectName = "syntax-test",
            packageName = "com.example.syntaxtest",
            targetDirectory = projectDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(projectDir, "src/jsMain/kotlin/com/example/syntaxtest/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist")

        val content = mainFile.readText()

        // Verify no syntax errors in critical sections

        // 1. Verify Modifier construction is correct
        assertTrue(
            content.contains("Modifier()"),
            "Should have Modifier() constructor calls"
        )
        assertFalse(
            content.contains("= Modifier.padding") || content.contains("= Modifier."),
            "Should not have bare Modifier. static calls"
        )

        // 2. Verify Button has onClick parameter
        val buttonPattern = Regex("""Button\s*\(\s*onClick\s*=\s*\{""")
        assertTrue(
            buttonPattern.containsMatchIn(content),
            "Button should have onClick parameter first"
        )

        // 3. Verify Button has label parameter (not text)
        // Note: We use [\s\S]*? to match across lines, as the Button call is multi-line
        // and we can't rely on [^)]* because the lambda contains function calls with parentheses
        assertTrue(
            content.contains("label =") && Regex("""Button[\s\S]*?label\s*=""").containsMatchIn(content),
            "Button should use 'label' parameter"
        )
        assertFalse(
            Regex("""Button\s*\([\s\S]*?text\s*=""").containsMatchIn(content),
            "Button should not use 'text' parameter"
        )

        // 4. Verify padding has string values with units
        println("DEBUG: Content of Main.kt:\n$content")
        assertTrue(
            content.contains("padding(\""),
            "padding() should use string values with units"
        )

        // 5. Verify renderComposableRoot usage
        assertTrue(
            content.contains("renderComposableRoot("),
            "Should use renderComposableRoot API"
        )

        println("✅ Generated Main.kt syntax validation passed")
    }

    @Test
    fun `fullstack ktor template wires shared summon ui and server`() {
        val template = ProjectTemplate.fromType("ktor")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "fullstack-app",
            packageName = "com.example.full",
            targetDirectory = tempDir,
            templateType = "ktor",
            minimal = false
        )

        generator.generate(config)

        val appFile = File(tempDir, "app/src/commonMain/kotlin/com/example/full/App.kt")
        assertTrue(appFile.exists(), "Shared App.kt should exist in the app module for fullstack templates")
        val appContent = appFile.readText()
        assertTrue(appContent.contains("@Composable"), "App.kt should declare a composable function")
        assertTrue(appContent.contains("Button("), "App.kt should use Summon components")

        val jsBootstrap = File(tempDir, "app/src/jsMain/kotlin/com/example/full/Main.kt")
        assertTrue(jsBootstrap.exists(), "JS bootstrap should exist inside the app module")
        val jsContent = jsBootstrap.readText()
        assertTrue(jsContent.contains("renderComposableRoot("), "JS bootstrap should use renderComposableRoot API")

        val serverFile = File(tempDir, "backend/src/main/kotlin/com/example/full/Application.kt")
        assertTrue(serverFile.exists(), "Ktor server entrypoint should be generated in backend module")
        val serverContent = serverFile.readText()
        assertTrue(serverContent.contains("PlatformRenderer"), "Server should use PlatformRenderer for SSR")
        assertTrue(serverContent.contains("/static/app.js"), "Server should expose the compiled JS bundle")

        val buildContent = File(tempDir, "build.gradle.kts").readText()
        assertTrue(
            buildContent.contains("dependsOn(\":backend:run\")"),
            "Root build script should wire run task to backend module"
        )
        assertTrue(
            buildContent.contains("kotlin(\"multiplatform\")"),
            "Root build script should declare shared Kotlin plugin aliases"
        )

        val backendBuildContent = File(tempDir, "backend/build.gradle.kts").readText()
        assertTrue(
            backendBuildContent.contains("dependsOn(\":app:jsBrowserDistribution\")"),
            "Backend build should ensure JS bundle is built before server tasks"
        )
        assertTrue(
            backendBuildContent.contains("into(\"static\")"),
            "Backend resources task should ship frontend assets for the Ktor server"
        )

        val appBuildContent = File(tempDir, "app/build.gradle.kts").readText()
        assertTrue(
            appBuildContent.contains("outputFileName = \"app.js\""),
            "App module should configure webpack output file"
        )
    }

    /**
     * Regression test for GitHub issue #35: "Root element with ID app not found".
     *
     * For every fullstack template the client-side Main.kt must call
     * renderComposableRoot with the same element ID that the server renderer
     * emits (currently "summon-app"), and the generated index.html must declare
     * a root element with the matching ID.
     */
    @Test
    fun `fullstack templates use consistent root element ID across client and server`() {
        val expectedRootId = "summon-app"
        val fullstackTypes = listOf("ktor", "spring-boot", "quarkus")

        for (type in fullstackTypes) {
            val typeDir = File(tempDir, "root-id-$type")
            typeDir.mkdirs()

            val template = ProjectTemplate.fromType(type)
            val generator = ProjectGenerator(template)

            val config = ProjectGenerator.Config(
                projectName = "test-$type",
                packageName = "com.example.test",
                targetDirectory = typeDir,
                templateType = type,
                minimal = false
            )

            generator.generate(config)

            // Client-side Main.kt should use the expected root element ID
            val jsMain = File(typeDir, "app/src/jsMain/kotlin/com/example/test/Main.kt")
            assertTrue(jsMain.exists(), "$type: JS Main.kt should exist")
            val jsContent = jsMain.readText()
            assertTrue(
                jsContent.contains("renderComposableRoot(\"$expectedRootId\")"),
                "$type: JS Main.kt should call renderComposableRoot(\"$expectedRootId\") but was:\n$jsContent"
            )

            // Generated index.html root element should match
            val indexHtml = File(typeDir, "app/src/jsMain/resources/index.html")
            assertTrue(indexHtml.exists(), "$type: index.html should exist")
            val htmlContent = indexHtml.readText()
            assertTrue(
                htmlContent.contains("id=\"$expectedRootId\""),
                "$type: index.html should contain id=\"$expectedRootId\" but was:\n$htmlContent"
            )
        }
    }

    @Test
    fun `fullstack quarkus template adds health test`() {
        val template = ProjectTemplate.fromType("quarkus")
        val generator = ProjectGenerator(template)

        val config = ProjectGenerator.Config(
            projectName = "portal",
            packageName = "com.example.portal",
            targetDirectory = tempDir,
            templateType = "quarkus",
            minimal = false
        )

        generator.generate(config)

        val testFile = File(tempDir, "backend/src/test/kotlin/com/example/portal/SummonResourceTest.kt")
        assertTrue(testFile.exists(), "Quarkus backend should generate SummonResourceTest.kt")
        val testContent = testFile.readText()
        assertTrue(
            testContent.contains("runSummonBackendChecks"),
            "SummonResourceTest should expose a main-backed check"
        )

        val backendBuild = File(tempDir, "backend/build.gradle.kts").readText()
        assertTrue(
            backendBuild.contains("quarkusGenerateCodeTests"),
            "Quarkus backend build script should configure code generation for tests"
        )
        assertTrue(
            backendBuild.contains("enabled = false"),
            "Quarkus test code generation should be disabled to work around Kotlin-only projects"
        )
        assertTrue(
            backendBuild.contains("unitTest"),
            "Quarkus backend build script should register a unitTest task"
        )
        assertTrue(
            backendBuild.contains("SummonResourceTestKt"),
            "Unit test task should target the generated SummonResourceTestKt entry point"
        )
    }

    /**
     * Regression test: the app module must target the same JVM toolchain as the
     * backend so that `./gradlew :backend:run` can load App.kt classes at runtime.
     *
     * Without jvmToolchain(17) in the app module, a system JDK > 17 compiles the
     * app to a higher class file version than the backend's JDK 17 can load,
     * causing UnsupportedClassVersionError at runtime.
     */
    @Test
    fun `fullstack app module targets JDK 17 toolchain to match backend`() {
        val fullstackTypes = listOf("ktor", "spring-boot", "quarkus")

        for (type in fullstackTypes) {
            val typeDir = File(tempDir, "toolchain-$type")
            typeDir.mkdirs()

            val template = ProjectTemplate.fromType(type)
            val generator = ProjectGenerator(template)
            val config = ProjectGenerator.Config(
                projectName = "test-$type",
                packageName = "com.example.test",
                targetDirectory = typeDir,
                templateType = type,
                minimal = false
            )

            generator.generate(config)

            val appBuild = File(typeDir, "app/build.gradle.kts")
            assertTrue(appBuild.exists(), "$type: app/build.gradle.kts should exist")
            val content = appBuild.readText()
            assertTrue(
                content.contains("jvmToolchain(17)"),
                "$type: app module must set jvmToolchain(17) to match backend but was:\n$content"
            )
        }
    }

    /**
     * The SSR route in generated server templates must have error handling so
     * that rendering failures produce a visible error page instead of a silent
     * HTTP 500 with an empty body.
     */
    @Test
    fun `fullstack server templates include SSR error handling`() {
        val fullstackTypes = listOf("ktor", "spring-boot", "quarkus")

        for (type in fullstackTypes) {
            val typeDir = File(tempDir, "errhandling-$type")
            typeDir.mkdirs()

            val template = ProjectTemplate.fromType(type)
            val generator = ProjectGenerator(template)
            val config = ProjectGenerator.Config(
                projectName = "test-$type",
                packageName = "com.example.test",
                targetDirectory = typeDir,
                templateType = type,
                minimal = false
            )

            generator.generate(config)

            val serverDir = File(typeDir, "backend/src/main/kotlin/com/example/test")
            val serverFiles = serverDir.listFiles()?.filter { it.extension == "kt" } ?: emptyList()
            assertTrue(serverFiles.isNotEmpty(), "$type: should generate server file(s)")

            val serverContent = serverFiles.joinToString("\n") { it.readText() }
            assertTrue(
                serverContent.contains("catch (e: Throwable)"),
                "$type: SSR route should catch Throwable to surface errors"
            )
            assertTrue(
                serverContent.contains("SSR Error"),
                "$type: SSR error handler should return an error page"
            )
        }
    }
}

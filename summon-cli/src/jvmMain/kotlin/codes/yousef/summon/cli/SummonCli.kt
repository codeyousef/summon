package codes.yousef.summon.cli

import codes.yousef.summon.cli.commands.InitCommand
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption

/**
 * Main entry point for the Summon CLI tool.
 *
 * The Summon CLI provides commands to scaffold new projects and generate components
 * for the Summon Kotlin Multiplatform UI framework.
 */
class SummonCli : CliktCommand(
    name = "summon"
) {

    private val verbose by option("--verbose", "-v", help = "Enable verbose output")
        .default("false")

    init {
        versionOption("0.5.0.2")
    }

    override fun run() {
        if (verbose == "true") {
            echo("Verbose mode enabled")
        }

        if (currentContext.invokedSubcommand == null) {
            echo(
                """
                Welcome to Summon CLI!
                
                Summon is a Kotlin Multiplatform UI framework that brings Jetpack Compose-style 
                declarative UI to browser and JVM environments.
                
                📦 If you downloaded the JAR file:
                   java -jar summon-cli-0.5.0.2.jar init <name>
                
                Quick start:
                  1. Pick a project folder: --here, --dir <path>, or default subdirectory
                  2. Run `java -jar summon-cli-0.5.0.2.jar init <name>`
                  3. Choose project type when prompted:
                     - 1) Standalone site (browser-only)
                     - 2) Full stack (Summon UI + backend)
                  4. If full stack, pick a backend:
                     - 1) Spring Boot
                     - 2) Ktor
                     - 3) Quarkus

                Available command:
                  init      Scaffold a Summon project (standalone or full-stack)

                Useful flags:
                  --here            Generate in current directory
                  --dir <path>      Output into a custom directory ('.' for current)
                  --mode <type>     Skip the prompt (standalone, fullstack)
                  --backend <type>  Skip the prompt for backends (spring, ktor, quarkus)

                Example (non-interactive):
                  java -jar summon-cli-0.5.0.2.jar init portal --mode=fullstack --backend=quarkus
                  java -jar summon-cli-0.5.0.2.jar init landing-page --mode=standalone --here
                For Quarkus projects, run `./gradlew unitTest` before `./gradlew quarkusDev`.
            """.trimIndent()
            )
        }
    }
}

/**
 * Main function - entry point for the CLI application
 */
fun main(args: Array<String>) = SummonCli()
    .subcommands(InitCommand())
    .main(args)

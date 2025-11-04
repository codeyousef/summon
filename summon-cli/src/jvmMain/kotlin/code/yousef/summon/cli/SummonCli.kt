package code.yousef.summon.cli

import code.yousef.summon.cli.commands.CreateCommand
import code.yousef.summon.cli.commands.GenerateCommand
import code.yousef.summon.cli.commands.InitCommand
import code.yousef.summon.cli.commands.InstallCommand
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
        versionOption("0.4.2.1")
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
                   java -jar summon-cli-0.4.2.1.jar <command>
                
                💡 To install globally (adds 'summon' command):
                   java -jar summon-cli-0.4.2.1.jar install
                   (Then restart your terminal)
                
                Recommended workflow:
                  1. Pick a project folder: --here, --dir <path>, or default subdirectory
                  2. Run `init` to scaffold the UI + optional backend
                     - Standalone browser UI:  summon init my-app --mode=standalone
                     - Full-stack app:        summon init my-app --mode=fullstack --backend=<ktor|spring|quarkus>
                  3. Use `generate` to add components, pages, routes, etc.

                Available commands:
                  init      Scaffold a new Summon project (standalone or full-stack)
                  create    Direct template access (legacy shortcuts for js, ktor, spring, quarkus)
                  generate  Add components, pages, routes, and other artifacts to an existing project
                  install   Install Summon CLI globally and add to PATH

                Directory options (init/create):
                  --here            Generate in current directory
                  --dir <path>      Output into a custom directory ('.' for current)
                  --output <path>   Alias for --dir when using create

                Use 'summon --help' to list commands.
                Use 'summon <command> --help' for details and advanced flags.

                Example commands (JAR download):
                  java -jar summon-cli-0.4.2.1.jar init my-app --mode=fullstack --backend=ktor
                  java -jar summon-cli-0.4.2.1.jar init my-app --mode=standalone --here
                  java -jar summon-cli-0.4.2.1.jar generate component Button

                After installation:
                  summon init my-app --mode=fullstack --backend=quarkus
                  summon generate component Button
            """.trimIndent()
            )
        }
    }
}

/**
 * Main function - entry point for the CLI application
 */
fun main(args: Array<String>) = SummonCli()
    .subcommands(
        InitCommand(),
        CreateCommand(),
        GenerateCommand(),
        InstallCommand()
    )
    .main(args)

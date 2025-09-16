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
        versionOption("0.3.2.0")
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
                
                Available commands:
                  init      Initialize a new Summon project with flexible directory options
                  create    Create a new project from predefined templates  
                  generate  Generate components, pages, and other files
                  install   Install Summon CLI globally and add to PATH
                
                Directory Options (init/create):
                  --here            Generate in current directory
                  --dir <path>      Specify custom output directory (use '.' for current)
                  --output <path>   Alternative to --dir for create command
                  (default)         Creates subdirectory with project name
                
                Use 'summon --help' to see all available options.
                Use 'summon <command> --help' for help on specific commands.
                
                Examples:
                  summon init my-app                    # Creates ./my-app/
                  summon init my-app --here             # Creates in current directory
                  summon create js-app --name my-app    # Creates ./my-app/ from template
                  summon generate component Button      # Generate component in existing project
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
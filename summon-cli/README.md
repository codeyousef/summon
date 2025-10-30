# Summon CLI

Command-line tool for scaffolding Summon projects and generating components.

## Installation

### Download JAR

[Download from GitHub Releases](https://github.com/codeyousef/summon/releases)

### Run Without Installation

```bash
java -jar summon-cli-0.4.1.0.jar <command>
```

### Install Globally

```bash
java -jar summon-cli-0.4.1.0.jar install
# Restart terminal, then use:
summon <command>
```

## Commands

### init - Create New Project

```bash
summon init my-app                    # Creates ./my-app/
summon init my-app --here             # Creates in current directory  
summon init my-app --template=wasm    # WASM template
```

### create - Create from Template

```bash
summon create js-app --name my-app
summon create quarkus-app --name backend
summon create ktor-app --name api
```

### generate - Generate Components

```bash
summon generate component Button
summon generate page About
summon generate route /api/users
```

### install - Install Globally

```bash
summon install              # Install for current user
summon install --global     # System-wide (requires admin)
summon install --force      # Reinstall
```

## Usage Examples

### JavaScript/Browser Project

```bash
java -jar summon-cli-0.4.1.0.jar init my-web-app
cd my-web-app
./gradlew jsBrowserDevelopmentRun
```

### Full-Stack Quarkus Project

```bash
java -jar summon-cli-0.4.1.0.jar create quarkus-app --name my-fullstack
cd my-fullstack
./gradlew quarkusDev
```

### WebAssembly Project

```bash
java -jar summon-cli-0.4.1.0.jar init wasm-app --template=wasm
cd wasm-app
./gradlew wasmJsBrowserDevelopmentRun
```

## Building Native Executables

If you want to build a native executable for faster startup:

```bash
./gradlew :summon-cli:buildNativeExecutable
# Output: summon-cli/build/native/summon.exe (or summon on Unix)
```

**Note**: Requires GraalVM to be installed.

## Development

### Build the JAR

```bash
./gradlew :summon-cli:shadowJar
```

### Run Locally

```bash
java -jar summon-cli/build/libs/summon-cli-0.4.1.0.jar --help
```

### Run Tests

```bash
./gradlew :summon-cli:jvmTest
```

## Troubleshooting

### "java: command not found"

Make sure Java 17+ is installed:
```bash
java -version
```

Download from: https://adoptium.net/

### JAR Won't Run

Ensure you have Java 17 or higher:
```bash
java -version
# Should show version 17 or higher
```

### Install Command Fails

On Windows, you may need to run as Administrator for global installation:
```bash
# Right-click terminal → Run as Administrator
java -jar summon-cli-0.4.1.0.jar install --global
```

### Command Not Found After Install

Restart your terminal or run:
```bash
# Windows
refreshenv

# Unix/Mac
source ~/.profile
```

## More Information

- [Main Documentation](../README.md)
- [GitHub Repository](https://github.com/codeyousef/summon)
- [Report Issues](https://github.com/codeyousef/summon/issues)

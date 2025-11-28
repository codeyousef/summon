<p align="center">
  <img src="https://raw.githubusercontent.com/codeyousef/summon/main/assets/logo.png" alt="Summon logo" width="160" />
</p>

# Summon CLI

Command-line tool for scaffolding Summon projects and generating components.

## Installation

### Download JAR

[Download from GitHub Releases](https://github.com/codeyousef/summon/releases)

### Run Without Installation

```bash
java -jar summon-cli-0.5.4.1.jar init my-app
java -jar summon-cli-0.5.4.1.jar --help
```

## Command

### init – Scaffold a Summon Project

`init` now drives the entire workflow. Run it once and follow the prompts:

1. Choose the project type:
    - `1` – Standalone site (browser-only)
    - `2` – Full stack (Summon UI + backend)
2. If full stack, choose the backend:
    - `1` – Spring Boot
    - `2` – Ktor
    - `3` – Quarkus

Useful flags:

```bash
java -jar summon-cli-0.5.4.1.jar init portal --mode=fullstack --backend=quarkus  # Skip prompts
java -jar summon-cli-0.5.4.1.jar init marketing --mode=standalone --here          # Generate in current dir
```

## Usage Examples

### Standalone Browser Project

```bash
java -jar summon-cli-0.5.4.1.jar init landing --mode=standalone
cd landing
./gradlew jsBrowserDevelopmentRun
```

### Full-Stack Ktor Project

```bash
java -jar summon-cli-0.5.4.1.jar init portal --mode=fullstack --backend=ktor
cd portal
./gradlew build
./gradlew run
```

### Full-Stack Quarkus Project

```bash
java -jar summon-cli-0.5.4.1.jar init portal --mode=fullstack --backend=quarkus
cd portal
./gradlew build
./gradlew unitTest
./gradlew quarkusDev
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
java -jar summon-cli/build/libs/summon-cli-0.5.4.1.jar --help
```

### Run Tests

```bash
./gradlew :summon-cli:jvmTest
```

This command scaffolds fresh standalone, Ktor, Spring, and Quarkus samples and runs their Gradle builds, so expect it to
take a few minutes.

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

# Summon Examples

This directory contains examples demonstrating how to use Summon in different environments and with various frameworks.

## Examples Available

- **[JavaScript Example](js/js-example/)** - Browser-based application using Summon
- **[Quarkus Example](jvm/quarkus-example/)** - Server-side application with Quarkus integration

## Prerequisites

All examples require:
- JDK 17 or higher
- GitHub account with package access
- Personal Access Token with `read:packages` permission

## GitHub Packages Authentication

Since Summon is published to GitHub Packages, you need to authenticate to download the dependencies.

### 1. Create Personal Access Token

1. Go to [GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Give it a name like "Summon Examples"
4. Select the `read:packages` scope
5. Click "Generate token"
6. **Copy the token value** (you won't be able to see it again)

### 2. Configure Authentication

Choose one of these methods:

#### Method A: Global Gradle Properties (Recommended)

Create or edit `~/.gradle/gradle.properties` and add:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_PERSONAL_ACCESS_TOKEN
```

This will work for all Summon examples and projects.

#### Method B: Project-Specific Properties

Create a `gradle.properties` file in each example directory:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_PERSONAL_ACCESS_TOKEN
```

#### Method C: Environment Variables

Set these environment variables:

```bash
# Linux/macOS
export USERNAME=YOUR_GITHUB_USERNAME
export TOKEN=YOUR_PERSONAL_ACCESS_TOKEN

# Windows
set USERNAME=YOUR_GITHUB_USERNAME
set TOKEN=YOUR_PERSONAL_ACCESS_TOKEN
```

### 3. Run Examples

Once authentication is configured, you can run any example:

```bash
cd js/js-example
./gradlew jsBrowserDevelopmentRun
```

```bash
cd jvm/quarkus-example  
./gradlew quarkusDev
```

## Troubleshooting

### 401 Unauthorized Error

If you get a `401 Unauthorized` error when building:

1. Verify your Personal Access Token has `read:packages` permission
2. Check your authentication configuration (gradle.properties or environment variables)
3. Ensure your GitHub username is correct
4. Try regenerating your Personal Access Token

### Build Issues

1. **Gradle wrapper permissions**: Run `chmod +x gradlew` on Unix systems
2. **Java version**: Ensure you're using JDK 17 or higher
3. **Network issues**: Check that you can access `https://maven.pkg.github.com`

### Getting Help

- Check the individual example READMEs for specific instructions
- Review the [main Summon documentation](../README.md)
- Open an issue on the [Summon GitHub repository](https://github.com/codeyousef/summon/issues)
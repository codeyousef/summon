# Publishing and CI/CD Setup Guide

This guide will help you set up Maven publishing and GitHub CI/CD for your Kotlin Multiplatform project.

## Prerequisites

1. GitHub repository set up
2. Sonatype OSSRH account (for Maven Central) - optional
3. GPG key for signing (for Maven Central) - optional

## 1. Setting up Maven Central Publishing (Optional)

### Create Central Portal Account (New Process 2024+)

1. Go to [Central Portal](https://central.sonatype.org/register/central-portal/)
2. Sign up with GitHub account (recommended) or email
3. Verify your namespace:
   - For GitHub users: Use `io.github.yourusername` (automatically verified)
   - For custom domains: Use `com.yourdomain` (requires DNS verification)
4. Generate User Token from "View Account" → "Generate User Token"
5. **No waiting required** - immediate access!

### Generate GPG Key

```bash
# Generate a new GPG key
gpg --gen-key

# List your keys to get the key ID
gpg --list-secret-keys --keyid-format LONG

# Export your public key to upload to key servers
gpg --armor --export YOUR_KEY_ID

# Export your private key (base64 encoded for GitHub secrets)
gpg --armor --export-secret-keys YOUR_KEY_ID | base64
```

Upload your public key to key servers:
```bash
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

## 2. GitHub Secrets Configuration

Go to your GitHub repository → Settings → Secrets and variables → Actions, and add these secrets:

### For Maven Central Publishing (New Central Portal):
- `CENTRAL_USERNAME`: Your Central Portal username (from User Token page)
- `CENTRAL_PASSWORD`: Your Central Portal token (from User Token page)
- `SIGNING_KEY_ID`: Your GPG key ID (last 8 characters)
- `SIGNING_PASSWORD`: Your GPG key passphrase
- `SIGNING_SECRET_KEY`: Your GPG private key (base64 encoded)

### For GitHub Packages (automatically available):
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions
- `GITHUB_ACTOR`: Automatically provided by GitHub Actions

## 3. Update Project Configuration

### Update build.gradle.kts

Replace the GitHub URLs in `publish.gradle.kts`:
```kotlin
// Update these URLs with your actual repository
url.set("https://github.com/YOUR_USERNAME/summon")
connection.set("scm:git:git://github.com/YOUR_USERNAME/summon.git")
developerConnection.set("scm:git:ssh://github.com/YOUR_USERNAME/summon.git")
url = uri("https://maven.pkg.github.com/YOUR_USERNAME/summon")
```

### Update Developer Information

In `publish.gradle.kts`, update the developer section:
```kotlin
developers {
    developer {
        id.set("your_id")
        name.set("Your Name")
        email.set("your.email@example.com")
    }
}
```

## 4. Local Development Setup

Create a `local.properties` file (this file is git-ignored):

```properties
# Central Portal credentials (New process 2024+)
centralUsername=your_central_portal_username
centralPassword=your_central_portal_token

# GPG signing
signing.keyId=YOUR_KEY_ID
signing.password=your_gpg_passphrase
signing.secretKeyRingFile=/path/to/your/secring.gpg

# GitHub Packages
gpr.user=your_github_username
gpr.key=your_github_personal_access_token
```

## 5. Testing Chrome Headless Locally

Install Chrome on your development machine and ensure tests run:

```bash
# Run all tests
./gradlew test

# Run only JS tests with Chrome headless
./gradlew jsTest

# Run only JVM tests
./gradlew jvmTest
```

## 6. Publishing Commands

### Local Publishing (for testing)
```bash
# Publish to local Maven repository
./gradlew publishToMavenLocal

# Build and test everything
./gradlew build
```

### Manual Publishing
```bash
# Publish snapshot to GitHub Packages
./gradlew publishAllPublicationsToGitHubPackagesRepository

# Publish release to Maven Central (if configured)
./gradlew publishAllPublicationsToCentralRepository
```

## 7. CI/CD Workflow Triggers

The GitHub Actions workflow automatically:

1. **On Pull Request**: Runs all tests
2. **On Push to Main**: Runs tests, builds, and publishes snapshot to GitHub Packages
3. **On Release**: Runs tests, builds, and publishes release to Maven Central (or GitHub Packages)

## 8. Creating a Release

1. Update the version in `build.gradle.kts`
2. Commit and push the changes
3. Create a new release on GitHub:
   - Go to Releases → Create a new release
   - Create a new tag (e.g., `v0.2.6`)
   - Write release notes
   - Publish the release

The CI/CD pipeline will automatically publish the release to your configured Maven repository.

## 9. Chrome Headless Configuration

The project is configured to use Chrome headless for JavaScript tests:

- In CI: Chrome is automatically installed and configured
- Locally: You need Chrome installed on your system
- The Karma configuration uses Chrome headless mode
- Tests run with source maps for better debugging

## 10. Troubleshooting

### Chrome Headless Issues:
- Ensure Chrome is installed on your system
- For CI, the `browser-actions/setup-chrome` action handles installation
- Check `CHROME_BIN` environment variable in CI

### Publishing Issues:
- Verify all secrets are correctly set in GitHub
- Check GPG key is properly formatted (base64 encoded for GitHub secrets)
- Ensure group ID matches your approved Sonatype namespace

### Build Issues:
- Clear Gradle cache: `./gradlew clean`
- Clear Kotlin/JS cache: `rm -rf build/js`
- Update dependencies: `./gradlew dependencyUpdates`

## 11. Monitoring

- Check GitHub Actions tab for CI/CD pipeline status
- Monitor Maven Central releases at: https://central.sonatype.com/search?q=YOUR_GROUP_ID
- Check GitHub Packages at: https://github.com/YOUR_USERNAME/summon/packages

## Next Steps

1. Set up your GitHub secrets
2. Update the URLs and developer information
3. Test locally with `./gradlew test`
4. Push to trigger the first CI/CD run
5. Create your first release when ready

For more information, see:
- [Kotlin Multiplatform Publishing](https://kotlinlang.org/docs/multiplatform-library.html)
- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [GitHub Packages Documentation](https://docs.github.com/en/packages)

# GitHub Actions Publishing Setup Guide

This guide explains how to set up automated publishing to Maven Central and GitHub Packages using GitHub Actions.

## Prerequisites

1. **Maven Central Portal Account**
   - Sign up at https://central.sonatype.com/
   - Generate an authentication token (username/password pair)
   - Note: This uses the new Central Portal, NOT the legacy OSSRH system

2. **GPG Signing Key**
   - Generate a GPG key pair for signing artifacts
   - Upload the public key to a keyserver
   - Export the private key for use in CI/CD

## Setting Up GitHub Secrets

Navigate to your repository Settings → Secrets and variables → Actions, then add these repository secrets:

### Required Secrets

1. **MAVEN_CENTRAL_USERNAME**
   - Your Central Portal token username
   - Format: tokenuser_xxxxxxxx

2. **MAVEN_CENTRAL_PASSWORD**
   - Your Central Portal token password
   - Keep this secure!

3. **SIGNING_KEY_ID**
   - The last 8 characters of your GPG key ID
   - Find it with: `gpg --list-secret-keys --keyid-format SHORT`

4. **SIGNING_PASSWORD**
   - The passphrase for your GPG key

5. **GPG_KEY_CONTENTS**
   - Your complete GPG private key
   - Export with: `gpg --armor --export-secret-keys YOUR_KEY_ID`
   - Copy the entire output including BEGIN/END markers

## How It Works

### Release Publishing

The release workflow (`.github/workflows/publish.yml`) is triggered when you create a new release on GitHub:

1. **Trigger**: Create a new release via GitHub UI
2. **Build**: The workflow builds and tests your project
3. **Sign**: Signs all artifacts with GPG
4. **Publish**: Automatically publishes to:
   - Maven Central Portal (using vanniktech plugin)
   - GitHub Packages

### Snapshot Publishing

The snapshot workflow (`.github/workflows/publish-snapshot.yml`) is triggered on:
- Pushes to `main` or `develop` branches
- Manual workflow dispatch

Snapshots:
- Automatically append `-SNAPSHOT` to the version
- Don't require GPG signing
- Are published to Maven Central's snapshot repository
- Are useful for testing pre-release versions

## Creating a Release

1. Go to your repository's Releases page
2. Click "Create a new release"
3. Choose a tag (e.g., `v0.2.6`)
4. Fill in release title and notes
5. Click "Publish release"

The workflow will automatically start and handle the publishing process.

## Monitoring Publication

- **GitHub Actions**: Check the Actions tab for workflow status
- **Maven Central**: Artifacts appear within 30 minutes at https://central.sonatype.com/
- **Maven Search**: Indexed within 2-4 hours at https://search.maven.org/

## Manual Publishing (Fallback)

If automated publishing fails, you can publish manually:

```bash
# Ensure secrets are set as environment variables
export ORG_GRADLE_PROJECT_mavenCentralUsername="your_username"
export ORG_GRADLE_PROJECT_mavenCentralPassword="your_password"
export ORG_GRADLE_PROJECT_signingInMemoryKeyId="your_key_id"
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword="your_passphrase"
export ORG_GRADLE_PROJECT_signingInMemoryKey="your_gpg_key"

# Run the publish command
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
```

## Troubleshooting

### Common Issues

1. **"Task 'publishAndReleaseToMavenCentral' not found"**
   - Ensure vanniktech plugin is properly configured in build.gradle.kts
   - Clean Gradle cache: `./clean-gradle-cache.sh`

2. **Signing failures**
   - Verify GPG_KEY_CONTENTS includes full armor block
   - Check SIGNING_KEY_ID is exactly 8 characters
   - Ensure signing password is correct
   - For snapshots: Signing is optional and will be skipped if credentials aren't available

3. **"Cannot perform signing task because it has no configured signatory"**
   - This happens when signing credentials aren't properly configured
   - For snapshots: The build.gradle.kts now includes conditional signing
   - For releases: Ensure all signing environment variables are set

4. **Authentication failures**
   - Verify Central Portal credentials (not OSSRH)
   - Ensure tokens haven't expired
   - Check secret names match exactly

### Verifying Setup Locally

Before relying on CI/CD, test locally:

```bash
# Dry run (doesn't actually publish)
./gradlew publishToMavenLocal

# Check generated artifacts
ls -la ~/.m2/repository/io/github/codeyousef/summon/
```

## Security Best Practices

1. **Never commit credentials** to the repository
2. **Rotate tokens** periodically
3. **Use repository secrets** only (not organization secrets)
4. **Limit secret access** to required workflows only
5. **Review workflow permissions** regularly

## Version Management

The version is defined in `build.gradle.kts`:

```kotlin
version = "0.2.5.1"
```

Update this before creating a new release to ensure proper versioning.
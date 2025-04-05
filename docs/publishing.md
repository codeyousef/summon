# Publishing to Maven Central

This document outlines the steps required to publish the Summon library to Maven Central.

## Prerequisites

1. **Sonatype Account**: Create an account on [Sonatype OSSRH](https://central.sonatype.org/publish/publish-guide/).
2. **GPG Key**: Generate a GPG key pair for signing artifacts.
3. **Gradle Configuration**: Configure your local environment for publishing.

## Setup

### 1. Sonatype OSSRH Account

If you don't already have one, create an account on [Sonatype JIRA](https://issues.sonatype.org/secure/Signup!default.jspa).

Then create a new project ticket to request access to the desired group ID:
- Project: Community Support - Open Source Project Repository Hosting (OSSRH)
- Issue Type: New Project
- Summary: Request for new project: Summon
- Group ID: code.yousef (adjust to your actual group ID)

### 2. GPG Key Setup

Generate a GPG key:

```bash
gpg --gen-key
```

Export your GPG key:

```bash
# Export public key
gpg --armor --export your-email@example.com > public-key.asc

# Export private key (keep this secure!)
gpg --armor --export-secret-keys your-email@example.com > private-key.asc
```

Distribute your public key:

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 3. Gradle Configuration

Create or edit the `~/.gradle/gradle.properties` file and add:

```properties
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password

signing.keyId=YOUR_KEY_ID_LAST_8_CHARS
signing.password=YOUR_GPG_KEY_PASSWORD
signing.secretKeyRingFile=/path/to/your/secring.gpg

# Or for in-memory signing
# signing.gnupg.keyName=YOUR_KEY_ID
# signing.gnupg.passphrase=YOUR_GPG_KEY_PASSWORD
```

## Preparing for Publishing

Run the prepare script to clean up demo files and build artifacts:

**On Windows:**
```powershell
scripts/prepare-for-publish.ps1
```

**On macOS/Linux:**
```bash
./scripts/prepare-for-publish.sh
```

## Publishing

### 1. Local Testing

Test the publishing process locally first:

```bash
./gradlew publishToMavenLocal
```

Verify the artifacts in your local Maven repository (typically in `~/.m2/repository/`).

### 2. Publishing to Maven Central

Publish the artifacts to Maven Central:

```bash
./gradlew publish
```

### 3. Release on OSSRH

1. Log in to [Sonatype Nexus](https://s01.oss.sonatype.org/)
2. Go to "Staging Repositories"
3. Find your repository and verify its contents
4. Click "Close" and wait for validation
5. If validation succeeds, click "Release"

## Troubleshooting

- **Signing Errors**: Ensure your GPG key is correctly set up and the password is correct.
- **Upload Errors**: Verify your Sonatype credentials and network connection.
- **Repository Not Found**: Make sure you're using the correct URL for your Sonatype account (s01.oss.sonatype.org for new accounts). 
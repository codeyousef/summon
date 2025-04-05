# Publishing to Maven Central

This guide will walk you through the process of publishing the Summon library to Maven Central.

## Prerequisites

Before you begin, you'll need to:

1. **Create a Sonatype OSSRH account**
   - Sign up at [Sonatype OSSRH](https://s01.oss.sonatype.org/)
   - Create a ticket to request access to the `code.yousef` group ID

2. **Generate GPG key**
   - Install GPG (GnuPG) from [https://gnupg.org/download/](https://gnupg.org/download/)
   - Generate a key pair: `gpg --gen-key`
   - List your keys: `gpg --list-keys`
   - Distribute your public key: `gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID`

## Gradle Setup

### Configure Gradle Properties

Create or edit your `~/.gradle/gradle.properties` file with the following:

```properties
# Sonatype credentials
ossrhUsername=your-jira-username
ossrhPassword=your-jira-password

# Signing configuration
signing.keyId=last8CharsOfGPGKeyID
signing.password=yourGPGKeyPassword
signing.secretKeyRingFile=/path/to/.gnupg/secring.gpg

# For newer GPG versions that don't have secring.gpg, use this instead:
# signing.gnupg.keyName=yourGPGKeyID
# signing.gnupg.passphrase=yourGPGKeyPassword
```

### Prepare the Library for Publishing

The Summon project already includes the necessary publishing configuration in the build scripts. You can find these configurations in:

- `build.gradle.kts`: Contains the main publishing configuration

## Publishing Process

### 1. Prepare for Publishing

Run the prepare scripts which will:
- Update version numbers
- Generate documentation
- Verify all tests pass

On Windows:
```bash
scripts\prepare-for-publish.bat
```

On macOS/Linux:
```bash
./scripts/prepare-for-publish.sh
```

### 2. Test the Publishing Process Locally

Before publishing to Maven Central, it's a good idea to test with a local Maven repository:

```bash
./gradlew publishToMavenLocal
```

Verify that the artifacts are correctly generated in your local Maven repository:
- Windows: `%USERPROFILE%\.m2\repository\code\yousef\summon`
- macOS/Linux: `~/.m2/repository/code/yousef/summon`

### 3. Publish to Maven Central

When you're ready to publish:

```bash
./gradlew publishAllPublicationsToSonatypeRepository
```

This will:
1. Compile the library
2. Generate sources and javadoc JARs
3. Sign all artifacts with your GPG key
4. Upload to Sonatype OSSRH staging repository

### 4. Release from Staging

After publishing to the staging repository:

1. Log in to [Sonatype OSSRH](https://s01.oss.sonatype.org/)
2. Go to "Staging Repositories"
3. Find your repository (usually named something like "codeyousef-####")
4. Verify the content
5. Click "Close" to perform validation
6. Once validation passes, click "Release"

It may take up to 2 hours for artifacts to sync to Maven Central and up to 24 hours to appear in search results.

## Troubleshooting

### Common Issues:

1. **GPG Key Issues**
   - Error: `No public key sent`. Make sure your key is correctly published to key servers.
   - Solution: Try publishing to another key server: `gpg --keyserver hkp://pool.sks-keyservers.net --send-keys YOUR_KEY_ID`

2. **Sonatype Repository Issues**
   - Error: `Failed to publish to Sonatype OSSRH`
   - Solution: Check your credentials in gradle.properties and verify you have the necessary permissions.

3. **Validation Failures During Close**
   - Error: Validation fails during the "Close" operation in Sonatype OSSRH
   - Solution: Review the "Activity" tab for detailed errors. Common issues include:
     - Missing POM information
     - Missing Javadoc or sources
     - Signature verification problems

4. **Version Already Exists**
   - Error: `Cannot publish version X.Y.Z because it already exists`
   - Solution: You cannot re-publish the same version. Update the version number in build.gradle.kts.

## Additional Resources

- [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)
- [Gradle Publishing Plugin Documentation](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [GitHub repository](https://github.com/yebaital/summon) 
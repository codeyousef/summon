# How to Run the Summon-Quarkus Example

The Summon-Quarkus example demonstrates integration between Summon and Quarkus. Due to build system complexities, the best way to run the example is to create a new Quarkus project and then add the Summon integration.

## Step 1: Install Quarkus CLI or Maven

You'll need either the Quarkus CLI or Maven installed:

- [Install Quarkus CLI](https://quarkus.io/guides/cli-tooling)
- [Install Maven](https://maven.apache.org/install.html)

## Step 2: Create a New Quarkus Project

Create a new Quarkus project using one of these methods:

```bash
# Using Quarkus CLI
quarkus create app org.example:summon-quarkus-example:1.0.0-SNAPSHOT \
  --extension=resteasy-reactive,qute,kotlin,jackson \
  --no-code

# Or using Maven
mvn io.quarkus.platform:quarkus-maven-plugin:3.6.5:create \
  -DprojectGroupId=org.example \
  -DprojectArtifactId=summon-quarkus-example \
  -DprojectVersion=1.0.0-SNAPSHOT \
  -DclassName="org.example.HelloResource" \
  -Dextensions="resteasy-reactive,qute,kotlin,jackson"
```

## Step 3: Add Summon Dependencies

Summon is available from Maven Central, so no authentication is required.

## Step 4: Add Summon Dependencies to Your Project

Add the following to your `build.gradle.kts` (or `pom.xml`):

For Gradle:
```kotlin
repositories {
    mavenCentral()
    // Existing repositories...
}

dependencies {
    // Existing dependencies...

    // Summon JVM library from Maven Central
    implementation("io.github.codeyousef:summon-jvm:0.2.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
}
```

For Maven:
```xml
<dependencies>
    <!-- Summon JVM Dependencies from Maven Central -->
    <dependency>
        <groupId>io.github.codeyousef</groupId>
        <artifactId>summon-jvm</artifactId>
        <version>0.2.9.1</version>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.kotlinx</groupId>
        <artifactId>kotlinx-html-jvm</artifactId>
        <version>0.12.0</version>
    </dependency>
</dependencies>
```

Note: All dependencies are available from Maven Central, no authentication required.

## Step 5: Copy the Example Files

Copy the following files from the Summon example to your project:

1. `SummonQuarkusApplication.kt` (renamed to `Application.kt`)
2. `HelloResource.kt`
3. Any components you want to use

Remember to update the package names to match your project's structure.

## Step 6: Run the Application

```bash
# For Gradle
./gradlew quarkusDev

# For Maven
./mvnw quarkus:dev
```

## Sample Code to Test

Here's a minimal example to test the Summon integration:

```kotlin
// HelloResource.kt
@Path("/hello")
class HelloResource {
    private val renderer = JvmPlatformRenderer()

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "Hello from Quarkus!"

    @GET
    @Path("/summon")
    @Produces(MediaType.TEXT_HTML)
    fun helloSummon(): String {
        return renderer.renderToHtml {
            SummonHelloComponent("Quarkus User")
        }
    }
}

@Composable
fun SummonHelloComponent(name: String) {
    Column {
        Text("Hello, $name!")
        Text("Welcome to Summon with Quarkus")
    }
}

// Helper extension function
fun JvmPlatformRenderer.renderToHtml(content: @Composable () -> Unit): String {
    val sb = StringBuilder()
    sb.append("<!DOCTYPE html><html><head><title>Summon Example</title></head><body>")
    val htmlContent = kotlinx.html.stream.createHTML().div {
        renderComposable(content, this)
    }
    sb.append(htmlContent)
    sb.append("</body></html>")
    return sb.toString()
}
```

## Troubleshooting

If you encounter issues:

1. Check that the packages and imports match your project structure
2. Verify that the Kotlin version in your Quarkus project is compatible with Summon (2.0.0 or later)
3. If you get compile errors with Summon imports, make sure you're using the correct artifact IDs and versions
4. Ensure you're using `summon-jvm` for JVM/Quarkus projects

For more detailed examples, refer to the full source code in the Summon repository. 

# Strategies for Annotation Processing in Kotlin Multiplatform Projects Targeting JavaScript

## 1. Introduction

Developers embarking on Kotlin Multiplatform (KMP) projects often leverage annotations to streamline configuration, reduce boilerplate code, and enable powerful framework features.1 This approach is common in the JVM ecosystem, where libraries frequently rely on Java's robust runtime reflection capabilities to process these annotations. However, a significant challenge arises when targeting JavaScript (JS) with KMP. The Kotlin/JS platform provides only limited support for runtime reflection, creating a direct conflict with libraries or patterns that declare annotations as mandatory based on JVM assumptions.2 This discrepancy leads to the core problem addressed in this report: how can developers effectively use or work around mandatory annotations in a KMP project when the JavaScript target lacks the necessary runtime reflection capabilities?

Annotations promise simplified development, but this promise encounters friction on the Kotlin/JS target. Frameworks designed with JVM reflection in mind cannot simply inspect the code at runtime to discover and act upon annotations when compiled to JavaScript.3 This report provides a comprehensive technical analysis of the viable strategies available to navigate this challenge.

The objective is to equip developers with the knowledge to understand the limitations of Kotlin/JS reflection and to effectively employ alternative mechanisms. The scope encompasses:

- A detailed examination of Kotlin/JS reflection constraints.
- An in-depth look at Kotlin Symbol Processing (KSP) as the primary compile-time solution.
- Guidance on implementing KSP specifically for the JS target within a KMP project.
- The role of KMP's `expect`/`actual` mechanism in managing platform differences.
- Analysis of how specific libraries handle annotations for JS.
- Exploration of alternative design patterns that obviate the need for reflection-based annotations.

This report will first establish the nature of the reflection problem in Kotlin/JS. It will then introduce KSP as the main compile-time alternative, detailing its configuration and use in a multiplatform context targeting JS. Subsequently, it will discuss the `expect`/`actual` pattern, analyze library-specific approaches, and cover reflection-free alternatives. Finally, it will synthesize these findings into actionable recommendations for developers facing this common KMP/JS challenge.

## 2. The Kotlin/JS Reflection Hurdle: Understanding the Limitations

The fundamental constraint when dealing with annotations in Kotlin/JS is the platform's deliberately _limited_ support for the Kotlin reflection API.2 Unlike the JVM target, which benefits from the comprehensive `kotlin-reflect` library 2, Kotlin/JS restricts runtime introspection capabilities. This is not an oversight but a design choice influenced by the nature of the JavaScript environment and the critical need to minimize the size of generated JS artifacts. Including extensive reflection metadata and the corresponding runtime implementation would significantly inflate download sizes, which is often unacceptable for web applications.5 The stability status of Kotlin/JS as "Stable" 6 guarantees a reliable platform but does not imply feature parity with Kotlin/JVM, especially concerning runtime reflection.

### 2.1 Supported Reflection Features in JS

The Kotlin/JS runtime provides access to only a specific subset of the reflection API:

- **Class References (`::class`):** This syntax allows obtaining a reference to a class. However, the `KClass` instance returned in Kotlin/JS is a stripped-down version compared to its JVM counterpart. It primarily supports:
    - `simpleName`: Getting the simple name of the class.
    - `isInstance()`: Checking if an object is an instance of the class.
    - `cast()` and `safeCast()`: Extension functions for type casting.
    - `KClass.js`: An escape hatch to access the underlying JavaScript constructor function, useful for interoperability with JS libraries expecting constructor references.3
- **Type References (`KType` and `typeOf()`):** The `typeOf()` function can construct `KType` instances, representing types at runtime. The `KType` API is largely supported, excluding Java-specific elements.3
- **Class Instantiation (`KClass.createInstance()`):** Basic support for creating instances of classes reflectively is available.3

### 2.2 Unsupported Reflection Features in JS

Crucially, many reflection features commonly used by annotation-driven frameworks on the JVM are _not_ supported in Kotlin/JS:

- **Detailed Introspection of Members:** There is no general mechanism to reflectively access and analyze class members like properties, functions, constructors, and their parameters at runtime. This includes retrieving annotations associated with these elements.2 For instance, attempting to access a property's delegate using reflection (`KProperty0<*>::getDelegate`) explicitly results in an "Unsupported" error.4
- **Full `KCallable` API:** The rich API for introspecting callable references (functions, properties, constructors) provided by `KCallable` and its subtypes (like `KFunction`, `KProperty`) is largely unavailable.2 This prevents detailed runtime analysis of function signatures, parameter types, or property characteristics.
- **`kotlin-reflect` Library:** The `kotlin-reflect.jar` artifact, which provides the comprehensive reflection implementation on the JVM, is a platform-specific dependency and is not included or usable in Kotlin/JS projects.2

### 2.3 Consequences for Annotation Processing

These limitations have direct and significant consequences for annotation processing frameworks that rely on runtime reflection:

- **Runtime Discovery Failure:** Frameworks cannot scan the classpath or runtime environment to find classes or members marked with specific annotations.
- **Dynamic Instantiation/Injection Failure:** Dependency injection containers that use reflection to analyze constructor parameters or find annotated fields/methods for injection will fail. They cannot determine _what_ to inject or _how_ to construct objects based solely on runtime annotation discovery.
- **Library Functionality Limitations:** Libraries heavily dependent on reflection, such as mocking frameworks like MockK which often use reflection and bytecode manipulation on the JVM, face substantial hurdles. Their functionality on non-JVM targets like JS and Native is typically either severely restricted or entirely unavailable.7 Attempts to bridge this gap with third-party reflection libraries for JS have been made in the past (e.g., `decembrist-kotlin2js-reflection`) but were limited and appear abandoned.8

The history of Kotlin/JS reflects a long-standing community desire for more extensive reflection capabilities.4 While JetBrains acknowledged this (tracking issues KT-13775, KT-13776), the technical challenges, particularly concerning code size and performance 5, and a strategic shift towards compile-time solutions have meant that full reflection parity with the JVM has not materialized and is unlikely to be the primary path forward. The lack of full reflection support is therefore systemic, rooted in platform constraints and design trade-offs favoring optimized JS output.

Consequently, when a library documentation states that annotations are "mandatory," it often carries an implicit assumption of a JVM environment where runtime reflection is available to process them. This assumption is the root cause of the conflict developers face on Kotlin/JS. The annotations themselves might be syntactically present, but the _mechanism_ intended to process them at runtime is absent. This necessitates finding alternative strategies – primarily compile-time processing – that fulfill the library's _purpose_ (e.g., dependency wiring, serialization mapping) without relying on the unavailable runtime reflection features.

## 3. Kotlin Symbol Processing (KSP): The Compile-Time Annotation Solution

Given the inherent limitations of runtime reflection in Kotlin/JS, the ecosystem has embraced compile-time code generation as the standard solution for annotation processing. Kotlin Symbol Processing (KSP) stands as the primary API for this purpose.9 KSP is specifically designed to enable the development of lightweight Kotlin compiler plugins that analyze source code during compilation and generate new files – typically Kotlin code – based on this analysis.1

### 3.1 Advantages of KSP over `kapt`

KSP offers significant advantages over the older Kotlin Annotation Processing Tool (`kapt`), making it the preferred choice, especially in multiplatform projects:

- **Performance:** `kapt` works by first generating Java stub files from Kotlin code and then running Java annotation processors against these stubs.12 This stub generation is notoriously slow. KSP, in contrast, analyzes Kotlin code directly using the compiler's internal representations, bypassing the stub generation phase entirely. This results in substantially faster build times, often up to twice as fast.9
- **Kotlin-Awareness:** Because `kapt` relies on Java stubs, it loses information about Kotlin-specific language features like extension functions, nullability distinctions, primary constructors, sealed classes, and properties.12 KSP, being Kotlin-native, fully understands these constructs, allowing processors to leverage them accurately.9
- **Multiplatform Compatibility:** `kapt` is fundamentally tied to the JVM compilation process. KSP, however, was designed with Kotlin Multiplatform in mind from the outset and supports processing code across all target platforms, including JVM, JavaScript, Native, and WebAssembly.1 This makes KSP the natural fit for KMP development. Reflecting this shift, `kapt` is officially in maintenance mode.12
- **API Stability:** While KSP itself evolves (KSP2 is currently in Beta 10), it aims to provide a more stable API surface for annotation processor developers compared to relying directly on the internal Kotlin compiler APIs, which can change more frequently between compiler versions.12 The K2 compiler explicitly supports KSP (via KSP2).16

### 3.2 KSP Workflow

The typical workflow for a KSP-based annotation processor involves several steps integrated into the Kotlin compilation process:

1. **Provider Discovery:** The Kotlin compiler discovers KSP processors through implementations of the `SymbolProcessorProvider` interface, typically registered via standard Java service provider mechanisms (`META-INF/services`).9
2. **Processor Instantiation:** The `SymbolProcessorProvider` creates instances of the `SymbolProcessor`.9
3. **Code Analysis:** The `SymbolProcessor`'s `process` method is invoked by the compiler. This method receives a `Resolver` object, which provides access to the program's source code structure represented as symbols (classes, functions, properties, annotations, etc.).1 Processors typically analyze these symbols to find elements annotated with specific annotations.
4. **Code Generation:** Based on the analysis, the processor uses a `CodeGenerator` API to create new output files.9 While KSP allows generating various output types ("code or other forms of output" 9), the most common output is new Kotlin source code (`.kt` files). KSP itself doesn't dictate the generation method; libraries like KotlinPoet are frequently employed for structured code generation.1 It's important to note that KSP processors cannot _modify_ existing source files; they only generate new ones.9
5. **Compilation:** The Kotlin compiler takes both the original source code and the newly generated Kotlin code from KSP and compiles them together into the final platform-specific output (e.g., JavaScript, JVM bytecode).9

KSP operates at the symbol level, meaning it understands the declarations and structure of the code (classes, members, types, annotations) similarly to how reflection does, but crucially, it performs this analysis at compile time rather than runtime.9 It does not typically parse or understand the logic _within_ function bodies (like control flow statements).9

The combination of reflection limitations on non-JVM targets, the performance drawbacks and JVM-centric nature of `kapt`, and KSP's design focus on Kotlin-awareness, multiplatform support, and performance firmly establishes KSP as the de facto standard and the strategically aligned solution for annotation processing in modern Kotlin Multiplatform projects, including those targeting JavaScript. Libraries are increasingly adopting KSP 14, reinforcing its central role in the KMP ecosystem.

## 4. Implementing KSP for the JavaScript Target

Successfully leveraging KSP in a Kotlin Multiplatform project targeting JavaScript requires careful configuration within the Gradle build system. Understanding how to apply the KSP plugin and specify dependencies for the correct compilation targets is essential.

### 4.1 Gradle Plugin Setup

The first step is to include the KSP Gradle plugin in the project.

1. **Declare the Plugin:** In the root `build.gradle.kts` file, declare the KSP plugin within the `plugins` block, ensuring `apply false` is used. It's crucial to select a KSP plugin version that aligns with the project's Kotlin version (e.g., KSP version `2.0.21-1.0.27` corresponds to Kotlin `2.0.21`).14 A list of releases is available on the KSP GitHub page.14
    
    Kotlin
    
    ```
    // Top-level build.gradle.kts
    plugins {
        //... other plugins
        id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false // Use version matching your Kotlin version
    }
    ```
    
2. **Apply the Plugin:** In the `build.gradle.kts` file of the module where KSP processing is needed (often the shared module or platform-specific modules), apply the KSP plugin.
    
    Kotlin
    
    ```
    // shared/build.gradle.kts or other module
    plugins {
        kotlin("multiplatform") // or other Kotlin plugin
        id("com.google.devtools.ksp")
        //... other plugins
    }
    ```
    

### 4.2 Target-Specific KSP Dependencies

A critical aspect of KSP configuration in KMP is using target-specific dependency scopes instead of the generic `ksp(...)` configuration. The generic `ksp` configuration, while simpler initially, applies the processor to _all_ compilation tasks for all targets (including main, test, and potentially other build variants), leading to unnecessary processing and increased build times.21 This configuration is deprecated for KMP projects starting from KSP 1.0.1+.21

Instead, dependencies on KSP processors should be added using scopes specific to the source sets and targets being processed. Common configurations include:

- `add("kspCommonMainMetadata", project(":your-processor"))`: This is arguably the most important configuration for KMP. It applies the processor during the compilation of the `commonMain` source set's metadata. Code generated via this configuration resides in a common output directory (e.g., `build/generated/ksp/metadata/commonMain/kotlin`) and becomes available as input for _all_ platform-specific compilations (JVM, JS, Native) that depend on `commonMain`.18 Use this for processing annotations in shared code to generate code usable across platforms.
- `add("kspJs", project(":your-processor"))` or `add("kspJsMain", project(":your-processor"))`: Applies the processor specifically to the `jsMain` source set during the JS compilation task. Generated code goes into a JS-specific directory (e.g., `build/generated/ksp/js/jsMain/kotlin`) and is only included in the JS target's output.18 Use this if annotations exist only in `jsMain` or if the generated code needs to be JS-specific (e.g., using JS interop).
- `add("kspJvm", project(":your-processor"))`, `add("kspIosX64", project(":your-processor"))`, etc.: Apply the processor to other specific platform targets (JVM, iOS, etc.).18
- `add("kspJsTest", project(":your-processor"))`, `add("kspJvmTest", project(":your-processor"))`, etc.: Apply the processor to platform-specific test source sets.21

An example `dependencies` block in a shared module's `build.gradle.kts` might look like this:

Kotlin

```
// shared/build.gradle.kts
kotlin {
    jvm()
    js(IR) { // Ensure IR compiler is used
        browser()
        // or nodejs()
    }
    iosX64()
    //... other targets

    sourceSets {
        val commonMain by getting {
            dependencies {
                //... common dependencies
            }
        }
        val jsMain by getting {
            dependencies {
                //... js dependencies
            }
        }
        //... other source sets
    }
}

dependencies {
    // Apply 'my-processor' to common code, generating code visible to all platforms
    add("kspCommonMainMetadata", project(":my-processor"))

    // Apply 'my-processor' specifically to JS main code (if needed)
    add("kspJsMain", project(":my-processor"))

    // Apply 'my-processor' specifically to JVM main code (if needed)
    add("kspJvmMain", project(":my-processor"))

    // Apply 'my-processor' specifically to iOS main code (if needed)
    add("kspIosX64Main", project(":my-processor")) // Adjust target name as needed

    // Apply 'my-processor' to JS test code (if needed)
    add("kspJsTest", project(":my-processor"))
}
```

### 4.3 Code Generation Flow for JS

The process for generating JS-compatible code via KSP follows these steps:

1. **Analysis:** When Gradle triggers a compilation task for which a KSP configuration is defined (e.g., `compileKotlinJs`), the associated KSP task (e.g., `kspJsKotlin`) runs first. The KSP processor analyzes the Kotlin source code specified by the configuration (e.g., `jsMain` sources if `kspJsMain` is used, or `commonMain` sources if `kspCommonMainMetadata` is used).
2. **Kotlin Code Generation:** The processor generates new Kotlin (`.kt`) source files.9 These files are placed in designated output directories structured by target and source set, such as `build/generated/ksp/js/jsMain/kotlin/` or `build/generated/ksp/metadata/commonMain/kotlin/`.17 The processor typically generates standard Kotlin code that is compatible with the target platform (JS in this case). It does not usually generate JavaScript code directly.21
3. **Kotlin Compilation:** The standard Kotlin compiler (e.g., `kotlin-js` via the multiplatform plugin) then runs. It compiles _both_ the original Kotlin source files _and_ the newly generated Kotlin files from KSP into the final JavaScript output.21

### 4.4 Making Generated Code Visible

By default, the IDE and the Gradle build process might not be aware of the code generated by KSP. This can lead to "unresolved reference" errors in the IDE and compilation failures. To resolve this, the generated source directories must be explicitly added to the relevant Kotlin source sets in the `build.gradle.kts` file:

Kotlin

```
// shared/build.gradle.kts
kotlin {
    //... targets definition

    sourceSets {
        val commonMain by getting {
            // Make code generated from commonMain annotations visible
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                //...
            }
        }
        val jsMain by getting {
            // Make code generated from jsMain annotations visible (if using kspJsMain)
            kotlin.srcDir("build/generated/ksp/js/jsMain/kotlin")
            dependencies {
                //...
            }
        }
        val jvmMain by getting {
             // Make code generated from jvmMain annotations visible (if using kspJvmMain)
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {
                //...
            }
        }
        //... configure other source sets similarly
    }
}
```

Adding these `srcDir` entries ensures that the IDE recognizes the generated symbols and that the Kotlin compiler includes them during compilation.17 Some setups might also require explicitly setting dependencies between compilation tasks, for example, ensuring the `kspCommonMainKotlinMetadata` task runs before platform compilation tasks.23

### 4.5 KSP Configuration Scopes Table

The following table summarizes the common KSP configurations for KMP:

|   |   |   |   |   |
|---|---|---|---|---|
|**Configuration**|**Input Source Set(s) Processed**|**Example Output Directory**|**Generated Code Visibility**|**Typical Use Case**|
|`kspCommonMainMetadata`|`commonMain`|`build/generated/ksp/metadata/commonMain/kotlin`|Common (All Platforms)|Processing shared annotations to generate code usable by JVM, JS, Native, etc.|
|`kspJsMain`|`jsMain` (+ `commonMain`)|`build/generated/ksp/js/jsMain/kotlin`|JS Only|Processing JS-specific annotations or generating JS-specific helper code.|
|`kspJvmMain`|`jvmMain` (+ `commonMain`)|`build/generated/ksp/jvm/jvmMain/kotlin`|JVM Only|Processing JVM-specific annotations or generating JVM-specific helper code.|
|`kspIosX64Main` (example)|`iosX64Main` (+ `commonMain`)|`build/generated/ksp/iosX64/iosX64Main/kotlin`|Native (iOS x64) Only|Processing Native-specific annotations or generating Native-specific helper code.|
|`kspJsTest`|`jsTest` (+ `commonTest`)|`build/generated/ksp/js/jsTest/kotlin`|JS Test Only|Generating code specifically for JS tests.|
|`kspJvmTest`|`jvmTest` (+ `commonTest`)|`build/generated/ksp/jvm/jvmTest/kotlin`|JVM Test Only|Generating code specifically for JVM tests.|

_(Note: Platform-specific tasks like `kspJsMain` typically process both the platform-specific source set and the common source set(s) it depends on. The generated code, however, is usually placed in the platform-specific output directory and is only compiled into that platform's artifact.)_

### 4.6 Potential Pitfalls and Troubleshooting

- **Duplicate Processing/Declarations:** Incorrect configuration, such as applying both generic `ksp` and specific `add("ksp...")` configurations, or potential bugs in older KSP versions interacting with KMP, could lead to the processor running multiple times on the same sources (especially `commonMain`), resulting in "duplicate declaration" errors during compilation.24 The solution is to strictly use the target-specific `add("ksp...")` configurations.
- **Configuration Complexity:** While the target-specific approach is more efficient, managing configurations across multiple targets and source sets (main, test, intermediate shared sets) can become complex.22 Careful planning of where annotations reside and where generated code is needed is essential.
- **Plugin Conflicts:** Although less common with current versions, conflicts could potentially arise between KSP and other Gradle plugins, particularly if using outdated versions of the Kotlin JS plugin (`kotlin("js")` should generally be avoided in favor of `kotlin("multiplatform")` for KMP projects).30
- **Output Directory Issues:** The KSP processor implementation must correctly use the `CodeGenerator` API provided by the KSP environment to ensure generated files land in the expected, target-specific directories. Incorrect usage might default to an unexpected location.27

Implementing KSP effectively shifts the burden of annotation processing from runtime (where it's problematic for JS) to compile time. While this avoids runtime reflection issues, it introduces a degree of complexity into the build configuration. Developers need to understand the KSP lifecycle, the nuances of multiplatform Gradle configurations, and how to correctly wire up source sets and dependencies.22 This trade-off exchanges runtime uncertainty for build-time setup, ultimately enabling cross-platform compatibility for annotation-driven features.

## 5. `expect`/`actual` Declarations for Platform Divergence

Kotlin Multiplatform provides the `expect` and `actual` keywords as a fundamental mechanism for managing platform-specific code while maximizing shared logic in `commonMain`. This mechanism allows developers to declare expected APIs or types in common code (`expect`) and provide concrete, platform-specific implementations (`actual`) in each target's source set (e.g., `jvmMain`, `jsMain`, `nativeMain`).31

### 5.1 Bypassing Reflection with `expect`/`actual`

The `expect`/`actual` pattern offers a direct strategy for handling situations where a common API needs behavior that differs significantly across platforms, particularly when the JVM implementation might rely on reflection, while the JS implementation cannot.

The approach involves:

1. **Declare in Common:** Define an `expect` declaration in `commonMain`. This can be an `expect class`, `expect interface`, `expect fun`, `expect val`/`var`, `expect annotation`, or `expect enum`.31 This declaration defines the API contract available to the shared code but contains no implementation details.
2. **Implement for JS:** Provide an `actual` implementation in the `jsMain` source set. This implementation fulfills the `expect` contract using JavaScript-specific APIs, standard Kotlin/JS features, or manual logic, crucially _avoiding_ any reliance on unsupported reflection APIs.31
3. **Implement for Other Platforms:** Provide corresponding `actual` implementations in other platform source sets (e.g., `jvmMain`, `iosMain`). These implementations can use platform-native APIs, Java libraries (for JVM), or even reflection if it's appropriate and available on that specific platform.31

The Kotlin compiler ensures that every `expect` declaration has a corresponding `actual` declaration in each platform source set it's compiled for. During compilation, the compiler effectively merges the `expect` declaration with its platform-specific `actual` implementation, generating the appropriate code for each target.31

### 5.2 Common `expect`/`actual` Patterns

Several patterns commonly employ `expect`/`actual` to bridge platform gaps:

- **Platform-Specific Factories:** A highly recommended pattern involves defining an `expect fun` in `commonMain` that returns a common interface or class. Each platform then provides an `actual fun` implementation that constructs and returns the appropriate platform-specific instance conforming to the common interface.31 This avoids the experimental nature of `expect class` while still achieving platform-specific instantiation.
    
    Kotlin
    
    ```
    // commonMain
    interface PlatformSpecificService {
        fun performAction(): String
    }
    expect fun createPlatformService(): PlatformSpecificService
    
    // jsMain
    private class JsPlatformService : PlatformSpecificService {
        override fun performAction(): String = "Action performed via JS API"
    }
    actual fun createPlatformService(): PlatformSpecificService = JsPlatformService()
    
    // jvmMain
    private class JvmPlatformService : PlatformSpecificService {
        override fun performAction(): String = "Action performed via JVM API" // Could use reflection here if needed
    }
    actual fun createPlatformService(): PlatformSpecificService = JvmPlatformService()
    ```
    
- **Platform-Specific Implementations (`expect class`/`interface`):** Directly define an `expect class` or `expect interface` in `commonMain` and provide full `actual` implementations on each platform.31 While powerful, it's worth noting that `expect class` specifically is still considered a Beta feature by the Kotlin compiler, and using the factory pattern with interfaces is often more stable and flexible.31
    
- **Providing Platform Dependencies:** `expect`/`actual` is frequently used to inject platform-contextual information or dependencies into common code. For example, an `expect class` or `expect val` might provide access to the Android `Context` on Android, the browser `Window` object on JS, or platform-specific file system APIs.18 This is often used in conjunction with dependency injection frameworks.
    
- **Platform-Specific Annotations:** While less common for the core problem of _processing_ annotations, `expect annotation` / `actual typealias` can be used to map a common annotation concept to different underlying platform-specific annotations if needed.33
    

### 5.3 Integration with KSP

`expect`/`actual` does not replace KSP but often complements it. A KSP processor running on `commonMain` (via `kspCommonMainMetadata`) can generate code that utilizes `expect` declarations. The generated code remains platform-agnostic, relying on the target-specific `actual` implementations to provide the concrete behavior during the final platform compilation stage.

For instance, a dependency injection framework using KSP might process annotations like `@Inject` in `commonMain`. If an injected dependency relies on a platform-specific component, the KSP-generated code might call an `expect fun getPlatformComponent()` factory. The `actual fun` implementations for this factory in `jsMain`, `jvmMain`, etc., would then provide the correct component for each platform.18

### 5.4 Limitations

While powerful, `expect`/`actual` has considerations:

- **Manual Implementation:** It requires writing and maintaining separate `actual` implementations for every targeted platform, which can increase boilerplate if the platform differences are minimal or numerous.
- **Potential for Overuse:** Over-reliance on `expect`/`actual` for minor variations can fragment the codebase and reduce the amount of truly shared logic, potentially undermining the benefits of KMP. It's best reserved for genuine, unavoidable platform divergences.
- **`expect class` Stability:** As mentioned, `expect class` is still Beta 31, making the interface + factory pattern generally preferable for type abstraction.

In essence, `expect`/`actual` serves as a crucial tool for defining platform boundaries within shared code. It provides the necessary hooks for platform-specific logic, which can be essential for bypassing reflection limitations on targets like JS. While it can sometimes eliminate the need for KSP by providing direct implementations, it more frequently works in tandem with KSP. KSP automates the processing of annotations and generation of wiring code based on common declarations, while `expect`/`actual` supplies the indispensable platform-specific building blocks or configurations that this generated code might depend upon.18 They address complementary aspects of the multiplatform development challenge.

## 6. Navigating Library Annotations in Kotlin/JS

When integrating third-party libraries that utilize annotations into a KMP project targeting JavaScript, the approach depends heavily on the library's design and its support for KMP and compile-time processing. The general principle is that libraries aiming for KMP compatibility, especially for reflection-limited targets like JS, should provide a KSP processor or an alternative compile-time mechanism.

### 6.1 General Approach: KSP Adoption

For libraries that traditionally rely on runtime reflection for annotation processing on the JVM, the standard path to KMP/JS compatibility involves providing a KSP processor. Developers using such libraries must:

1. Verify the library offers a KSP artifact.
2. Include the KSP Gradle plugin in their project.
3. Add the library's KSP processor artifact as a dependency using the appropriate target-specific KSP configurations (`kspCommonMainMetadata`, `kspJsMain`, `kspJvmMain`, etc.) as detailed in Section 4.
4. Consult the library's documentation for any specific KMP/JS setup instructions or required patterns (e.g., potential use of `expect`/`actual`).

### 6.2 Case Study: Dependency Injection (Koin)

Koin is a popular pragmatic dependency injection framework for Kotlin that embraces KMP and uses KSP for its annotation-based configuration.

- **Mechanism:** Koin provides the `koin-ksp-compiler` artifact, which processes annotations like `@Module`, `@Single`, `@Factory`, and `@ComponentScan` at compile time.18
- **Setup:** Integration requires adding the KSP Gradle plugin and configuring the `koin-ksp-compiler` dependency for the relevant KMP source sets (e.g., `kspCommonMainMetadata`, `kspAndroid`, `kspIosX64`, `kspJs`).18
- **Platform Specifics:** Koin effectively utilizes the `expect`/`actual` pattern alongside KSP. Developers can define `expect` declarations (e.g., an `expect class PlatformModule`) annotated for KSP in `commonMain`, and provide `actual` implementations in platform source sets, also potentially annotated for KSP, to handle platform-specific dependencies or configurations.18 This synergy demonstrates how KSP handles the annotation processing and code generation, while `expect`/`actual` provides the necessary platform integration points.

### 6.3 Case Study: Serialization (`kotlinx.serialization`)

`kotlinx.serialization` is the official Kotlin library for multiplatform serialization and represents a different compile-time approach.

- **Mechanism:** Instead of KSP, `kotlinx.serialization` uses its own dedicated Kotlin compiler plugin (`org.jetbrains.kotlin.plugin.serialization`).34 This plugin analyzes `@Serializable` annotations and generates efficient serializer/deserializer code at compile time, completely avoiding runtime reflection.35
- **Setup:** Configuration involves applying the specific `kotlin("plugin.serialization")` Gradle plugin and adding dependencies on the runtime libraries for the desired formats (e.g., `kotlinx-serialization-json`).34
- **Multiplatform:** The library and its plugin are inherently designed for KMP, working seamlessly across JVM, JS, and Native targets.34
- **Significance:** This illustrates that while KSP is the general-purpose API for compiler plugins, specific, highly integrated tasks like serialization might utilize dedicated compiler plugins to achieve compile-time processing and JS compatibility.

### 6.4 Case Study: Mocking (MockK)

Mocking libraries present a more challenging scenario due to their deep reliance on platform-specific runtime capabilities.

- **Mechanism:** Libraries like MockK heavily depend on JVM runtime reflection and bytecode manipulation techniques to create mock objects and verify interactions.7
- **KMP/JS Limitation:** These underlying mechanisms (runtime reflection, dynamic class generation/modification) are either absent or severely restricted on Kotlin/JS and Kotlin/Native.7 Consequently, the functionality of such mocking libraries is often limited or entirely unavailable on these targets.
- **Alternatives:** While KSP _could_ theoretically be used to generate mock implementations at compile time 7, this is not the common approach for established mocking libraries. Developers targeting JS often need to resort to alternative testing strategies:
    - Manual Fakes or Stubs: Creating simple, hand-written implementations of interfaces for testing purposes.
    - Using Interfaces + `expect`/`actual`: Defining interfaces in `commonMain` and using `expect`/`actual` factories to provide real implementations in production code (`actual` in `jsMain`, `jvmMain`) and test doubles in test code (`actual` in `jsTest`, `jvmTest`).

### 6.5 Importance of Library Documentation

Given the varying approaches (KSP, dedicated plugin, limited support), it is absolutely essential for developers to **thoroughly consult the documentation** of any annotation-using library they intend to use in a KMP/JS project. The documentation should clarify:

- Explicit KMP support, particularly for the JavaScript target.
- The mechanism used for annotation processing (KSP, dedicated plugin, runtime reflection, or manual configuration).
- Detailed Gradle setup instructions, including required plugin IDs, dependency coordinates, and necessary KSP configurations (`add("ksp...")`).
- Any known limitations, workarounds, or specific patterns (like required `expect`/`actual` usage) for the JS target.

Ultimately, the strategy for handling annotations for a given task (like DI or serialization) is often predetermined by the chosen library. If a library provides a KMP/JS-compatible KSP processor or compiler plugin, that is the path to follow. If it doesn't, developers must fall back on `expect`/`actual` for platform divergence or adopt manual, reflection-free patterns entirely. Evaluating a library's KMP maturity and its annotation processing mechanism for JS is therefore a critical step before committing to its use.

## 7. Alternative Patterns Avoiding Reflection and Code Generation

In scenarios where libraries lack KSP or dedicated plugin support for Kotlin/JS, or when developers prefer to avoid the added build complexity and potential "magic" of code generation, several alternative design patterns allow for managing dependencies and configurations without relying on annotations processed at runtime or compile time. These patterns often emphasize explicitness and leverage standard Kotlin language features.

### 7.1 Manual Dependency Injection ("Pure DI")

This approach involves wiring dependencies together explicitly in code, rather than relying on a framework to do it automatically based on annotations.

- **Constructor Injection:** Dependencies are passed directly as arguments to a class's primary constructor. This is the most fundamental and transparent form of DI.40 Kotlin's features like default parameter values can simplify this, allowing default implementations to be provided directly in the constructor signature, potentially sourced from a central component or factory.40
    
    Kotlin
    
    ```
    // Manual wiring component (example)
    object AppComponent {
        val networkClient: NetworkClient = RealNetworkClient()
        val database: Database = SqlDelightDatabase()
    }
    
    // Class using constructor injection with defaults from component
    class DataRepository(
        private val client: NetworkClient = AppComponent.networkClient,
        private val db: Database = AppComponent.database
    ) {
        //... uses client and db
    }
    
    // Usage:
    val repository = DataRepository() // Uses defaults
    val testRepository = DataRepository(FakeNetworkClient(), FakeDatabase()) // Provides test doubles
    ```
    
- **Factory Pattern:** Dedicated factory classes or functions are created with the sole responsibility of constructing objects and providing their dependencies.41 This encapsulates the creation logic. The `expect`/`actual` factory pattern (Section 5.2) is a KMP-specific variant useful for platform-dependent object creation.32
    
- **Manual Wiring Root:** A specific location in the application (e.g., the `main` function, an application initialization block) is responsible for explicitly creating and connecting the entire object graph.5
    

**Advantages of Manual DI:**

- **Explicitness:** The dependency graph is clearly visible in the code. There's no hidden magic.40
- **Compile-Time Safety:** Dependency errors are caught at compile time, not runtime.41
- **No Overhead:** Avoids the build-time overhead of KSP/`kapt` and the runtime overhead (if any) of reflection-based frameworks.41
- **Testability:** Makes substituting dependencies with test doubles straightforward, especially with constructor injection.40
- **Simplicity (Conceptual):** The core pattern is simple to understand.40

**Disadvantages of Manual DI:**

- **Boilerplate:** Can become verbose for applications with large, complex dependency graphs.41
- **Manual Maintenance:** Requires developers to manually update wiring code when dependencies change.

### 7.2 Service Locator Pattern

This pattern involves a central registry (the "locator") where services (dependencies) are registered, often by key or type. Code needing a dependency explicitly requests it from the locator. While sometimes simpler to set up initially than full manual wiring, it can obscure dependencies (they aren't visible in constructors/signatures) and potentially make unit testing more difficult if not used carefully, as components become coupled to the locator itself.

### 7.3 Configuration via Code/DSLs

Kotlin's strong support for Domain-Specific Languages (DSLs) can be leveraged to create type-safe builders for configuring application components and their relationships.42 Instead of annotations, developers use a dedicated configuration block in code to define how objects should be created and wired together. Many DI frameworks (like Koin, Kodein) offer DSLs as an alternative or complement to annotation processing.42 This approach maintains explicitness while potentially reducing boilerplate compared to pure manual wiring.

### 7.4 Kotlin Delegation

Kotlin's delegated properties offer concise ways to manage object instantiation, particularly for singletons or lazily initialized dependencies.

- **`by lazy {}`:** A standard library delegate ideal for creating singleton instances on first access.43 This is a common pattern for simple dependency management within manual DI setups.
    
    Kotlin
    
    ```
    object ServiceLocator {
        val userService: UserService by lazy { RealUserService(httpClient.value) }
        val httpClient: Lazy<HttpClient> = lazy { RealHttpClient() } // Using Lazy directly
    }
    ```
    
- **Custom Delegates:** Developers can create custom delegates to encapsulate more complex dependency retrieval or lifecycle management logic.43 However, care must be taken to ensure the delegate's implementation (`getValue`/`setValue`) doesn't inadvertently rely on unsupported reflection features, especially if using the `KProperty` parameter.45
    

### 7.5 When to Use Alternatives

These reflection-free, non-KSP patterns are particularly relevant when:

- A required library lacks KMP/JS support via KSP or a dedicated plugin.
- Working on smaller projects where the setup cost and complexity of a DI framework or KSP seem disproportionate.41
- Prioritizing minimal build times and avoiding additional build dependencies.
- A high degree of explicitness and compile-time checking of the dependency graph is desired.

The constraints imposed by Kotlin/JS's limited reflection, combined with the build-time complexity that KSP can introduce, elevate the status of manual DI patterns. What might be considered "poor man's DI" 5 or simply "the pattern without the framework" 40 becomes a highly practical, robust, and viable strategy in the KMP/JS context. Kotlin's language features, such as constructor injection, default arguments, objects, and lazy delegation, make these manual approaches quite ergonomic and effective.40

## 8. Synthesis and Recommendations

Navigating the landscape of annotations in Kotlin Multiplatform projects targeting JavaScript requires understanding the platform's limitations and leveraging the appropriate tools and patterns. While the absence of full runtime reflection presents a challenge for traditional JVM-centric annotation processing, the Kotlin ecosystem provides effective compile-time solutions and alternative patterns.

### 8.1 Recap of Strategies

The primary strategies available to developers are:

1. **Kotlin Symbol Processing (KSP):** The standard and recommended approach for compile-time annotation processing in KMP. It requires libraries to provide KSP processors and necessitates correct Gradle configuration using target-specific scopes (e.g., `kspCommonMainMetadata`, `kspJsMain`). This is the main way to make annotation-driven libraries work on JS.
2. **Dedicated Compiler Plugins:** Certain core libraries, notably `kotlinx.serialization`, use their own specialized compiler plugins to achieve compile-time code generation, bypassing the need for KSP for their specific domain but following the same principle of avoiding runtime reflection.
3. **`expect`/`actual` Declarations:** KMP's core mechanism for platform-specific implementations. It's crucial for providing platform-dependent building blocks (e.g., platform APIs, context objects) or implementing platform-specific logic, often used in conjunction with KSP or manual patterns to bridge the gap between common code and platform realities.
4. **Manual Patterns (Reflection-Free):** Techniques like pure Dependency Injection (constructor injection, factories), Service Locator, configuration via DSLs, and careful use of Kotlin delegation (`by lazy`). These serve as essential alternatives when KSP/plugin support is unavailable or when developers prefer explicitness and minimal build tooling.

### 8.2 Decision Flow for Handling Annotations

When faced with annotations in a KMP/JS project, developers can follow this general decision process:

1. **Identify the Requirement:** Is the annotation truly mandatory for the library's function on JS, or is it primarily for a JVM-based mechanism (like runtime reflection)?
2. **Check Library Support:** Does the library explicitly support KMP and the JS target? Does it provide a KSP processor or a dedicated compiler plugin for its annotations?
    - **If YES (KSP/Plugin Provided):**
        - Implement the KSP/plugin approach.
        - Configure Gradle meticulously using target-specific dependencies (`add("kspCommonMainMetadata",...)`, `add("kspJs",...)`, etc.) as per the library's documentation (See Section 4).
        - Add generated source directories to Kotlin source sets (See Section 4.4).
        - Determine if `expect`/`actual` is needed to provide platform-specific dependencies or configurations required by the library or the generated code (See Section 5 & 6).
    - **If NO (No KSP/Plugin for JS):**
        - The library, in its annotation-driven form, is likely incompatible with Kotlin/JS due to reliance on unsupported runtime reflection.
        - **Option A:** Find an alternative library that _does_ offer KMP/JS support (preferably with KSP).
        - **Option B:** Implement the required functionality manually using reflection-free patterns (Manual DI, factories, etc. - See Section 7). This might involve wrapping parts of the original library or replacing it entirely for the JS target, potentially using `expect`/`actual` to abstract the difference.
        - **Option C (Rare):** Check if the library offers a non-annotation-based configuration method (e.g., a manual DSL) for JS.
3. **No Mandatory Annotations:** If annotations are optional or not central to the required functionality, proceed with standard KMP development, using manual patterns like constructor injection or factories as needed for structuring the code (See Section 7).

### 8.3 Recommendations for Common Scenarios

- **Dependency Injection:** Strongly prefer libraries offering KSP processors designed for KMP (e.g., Koin 18). Configure KSP scopes (`kspCommonMainMetadata`, `kspJs`, etc.) correctly. Utilize `expect`/`actual` to inject platform-specific dependencies when needed. If a suitable KSP library isn't available or desired, implement Manual DI using constructor injection and factories.5
- **Serialization:** Use `kotlinx.serialization`.34 It's the official, multiplatform standard, uses a dedicated compiler plugin for compile-time generation, and avoids reflection, making it ideal for JS.
- **Network API Definition (e.g., Retrofit-style):** Look for KMP libraries that use KSP to generate type-safe client implementations from annotated interfaces (e.g., Ktorfit, though not detailed in snippets, follows this model). Alternatively, use a library like Ktor directly and define API calls manually without code generation.
- **Mocking and Testing:** Acknowledge the severe limitations of reflection-based mocking frameworks like MockK on JS.7 Prioritize creating manual fakes or stubs for interfaces. Leverage the `expect`/`actual` pattern to provide test doubles in platform-specific test source sets (`jsTest`, `jvmTest`). Investigate emerging KSP-based mocking solutions if available, but manual approaches are currently more reliable for JS testing.

### 8.4 Best Practices

- **Prioritize KMP/JS Support:** When selecting libraries, give preference to those with explicit KMP support, JS target compatibility, and KSP processors (or dedicated plugins).
- **Precise KSP Configuration:** Avoid the generic `ksp` configuration in KMP. Use target-specific scopes (`add("kspCommonMainMetadata",...)`, `add("kspJs",...)`, etc.) diligently.21
- **Judicious `expect`/`actual`:** Use `expect`/`actual` primarily for genuine platform differences or providing essential platform context, not for minor variations.31
- **Build Awareness:** Understand that KSP adds a compile-time step. Monitor build performance and ensure generated sources are correctly configured.17
- **Stay Updated:** Keep Kotlin, KSP plugin, and library versions aligned and updated, as the KMP ecosystem evolves rapidly.14
- **Test Thoroughly:** Always perform comprehensive testing specifically on the JavaScript target to catch any issues related to code generation, platform differences, or JS interop.

## 9. Conclusion

The requirement to use annotations in Kotlin Multiplatform projects targeting JavaScript initially appears problematic due to the platform's constrained runtime reflection capabilities. Libraries and patterns originating from the JVM often implicitly assume reflection is available, leading to incompatibility when ported directly to JS.

However, the Kotlin ecosystem provides robust and effective solutions centered around compile-time processing. Kotlin Symbol Processing (KSP) has emerged as the standard mechanism for developing lightweight compiler plugins that analyze Kotlin code and generate necessary boilerplate or wiring code during compilation, entirely bypassing the need for runtime reflection. Libraries supporting KMP/JS increasingly offer KSP processors. Alongside KSP, Kotlin's intrinsic `expect`/`actual` mechanism provides the essential capability to handle unavoidable platform-specific implementations and configurations, working synergistically with code generation or manual patterns. Furthermore, when code generation is not available or desired, manual Dependency Injection patterns, facilitated by Kotlin's language features, offer a viable and often preferable alternative, emphasizing explicitness and compile-time safety.

By understanding the specific limitations of Kotlin/JS reflection, mastering the configuration and application of KSP for multiplatform targets, leveraging the `expect`/`actual` pattern appropriately, and considering reflection-free manual patterns, developers can successfully overcome the challenges of annotation usage. They can build sophisticated, maintainable, and performant Kotlin Multiplatform applications that seamlessly target JavaScript alongside other platforms, embracing the shift towards compile-time safety and cross-platform code sharing.
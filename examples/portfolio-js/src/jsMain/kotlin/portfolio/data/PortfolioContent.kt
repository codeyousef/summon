package portfolio.data

/**
 * Static portfolio content data.
 * In a real app, this could be loaded from JSON or an API.
 */
object PortfolioContent {
    val profile = Profile(
        name = "Your Name",
        title = "Full-Stack Engineer",
        subtitle = "Building elegant solutions with Kotlin Multiplatform",
        location = "San Francisco, CA",
        email = "hello@example.com",
        github = "https://github.com/yourusername",
        linkedin = "https://linkedin.com/in/yourusername",
        twitter = "https://twitter.com/yourusername"
    )

    val skills = listOf(
        Skill("Kotlin", "Expert", listOf("Multiplatform", "Coroutines", "Compose")),
        Skill("TypeScript", "Advanced", listOf("React", "Node.js", "Deno")),
        Skill("Rust", "Intermediate", listOf("WebAssembly", "Systems")),
        Skill("Infrastructure", "Advanced", listOf("Kubernetes", "AWS", "Terraform"))
    )

    val projects = listOf(
        Project(
            title = "Summon",
            description = "A Kotlin Multiplatform UI framework for building web applications with a Compose-like API.",
            tags = listOf("Kotlin", "Compose", "WebAssembly"),
            link = "https://github.com/codeyousef/summon",
            featured = true
        ),
        Project(
            title = "Materia",
            description = "WebGL/WebGPU rendering engine for Kotlin with declarative shader DSL.",
            tags = listOf("Kotlin", "WebGPU", "Graphics"),
            link = "https://github.com/codeyousef/materia",
            featured = true
        ),
        Project(
            title = "Sigil",
            description = "Screen-space effects library with shader-based visual effects.",
            tags = listOf("Kotlin", "Shaders", "Effects"),
            link = "https://github.com/codeyousef/sigil",
            featured = true
        )
    )

    val experience = listOf(
        Experience(
            company = "Example Corp",
            role = "Senior Engineer",
            period = "2022 - Present",
            description = "Building next-gen developer tools with Kotlin Multiplatform."
        ),
        Experience(
            company = "Tech Startup",
            role = "Full-Stack Developer",
            period = "2019 - 2022",
            description = "Led development of web and mobile applications."
        )
    )
}

data class Profile(
    val name: String,
    val title: String,
    val subtitle: String,
    val location: String,
    val email: String,
    val github: String,
    val linkedin: String,
    val twitter: String
)

data class Skill(
    val name: String,
    val level: String,
    val tags: List<String>
)

data class Project(
    val title: String,
    val description: String,
    val tags: List<String>,
    val link: String,
    val featured: Boolean = false
)

data class Experience(
    val company: String,
    val role: String,
    val period: String,
    val description: String
)

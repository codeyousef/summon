package code.yousef.example.portfolio.repository

import code.yousef.example.portfolio.models.*
import code.yousef.example.portfolio.plugins.*
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

/**
 * Exposed-based implementation of PortfolioRepository
 */
class PortfolioRepositoryImpl : PortfolioRepository {
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    // Sample portfolio files
    private val sampleFiles = ConcurrentHashMap<String, PortfolioFile>()
    
    // Implementation of interface method
    override fun getPortfolioFile(id: String): PortfolioFile? = sampleFiles[id]
    
    // Projects storage
    private val projects = ConcurrentHashMap<String, Project>()
    
    // Journal entries storage
    private val journalEntries = ConcurrentHashMap<String, JournalEntry>()
    
    // Services storage
    private val services = ConcurrentHashMap<String, Service>()
    
    init {
        // Initialize sample files
        val summonLogo = PortfolioFile(
            id = "1",
            title = "Summon Framework Logo",
            filename = "summon-logo.png",
            width = 512,
            height = 512,
            url = "/images/summon-logo.png",
            mimeType = "image/png"
        )
        sampleFiles[summonLogo.id] = summonLogo
        
        val auroraPortal = PortfolioFile(
            id = "2", 
            title = "Liquid Glass Portal",
            filename = "aurora-portal.jpg",
            width = 1920,
            height = 1080,
            url = "/images/aurora-portal.jpg",
            mimeType = "image/jpeg"
        )
        sampleFiles[auroraPortal.id] = auroraPortal
        
        val glassShard = PortfolioFile(
            id = "3",
            title = "Glass Shard Design",
            filename = "glass-shard.png", 
            width = 800,
            height = 600,
            url = "/images/glass-shard.png",
            mimeType = "image/png"
        )
        sampleFiles[glassShard.id] = glassShard
        
        // Initialize sample projects with categories
        val summonProject = Project(
            id = "1",
            title = "Summon UI Framework",
            slug = "summon-ui-framework",
            description = "A Kotlin Multiplatform declarative UI framework bringing Jetpack Compose-style development to browser and JVM environments.",
            longDescriptionHtml = """
                <p>Summon is a comprehensive UI framework that enables developers to build sophisticated web applications using pure Kotlin.</p>
                <p>Key features include:</p>
                <ul>
                    <li>Type-safe CSS styling with modifier system</li>
                    <li>Component-based architecture</li>
                    <li>Reactive state management</li>
                    <li>Server-side rendering capabilities</li>
                    <li>Framework integrations (Quarkus, Ktor, Spring Boot)</li>
                </ul>
            """.trimIndent(),
            mainImage = summonLogo,
            galleryImages = listOf(summonLogo, auroraPortal),
            categoryLegacy = "Open Source",
            year = "2024",
            techStack = listOf("Kotlin", "Kotlin/JS", "Gradle", "CSS"),
            tags = listOf("UI Framework", "Kotlin Multiplatform", "Web Development"),
            liveUrl = "https://github.com/codeyousef/summon",
            repoUrl = "https://github.com/codeyousef/summon",
            status = ContentStatus.PUBLISHED,
            sort = 1
        )
        projects[summonProject.id] = summonProject
        
        val auroraProject = Project(
            id = "2",
            title = "Liquid Glass Aurora Portal",
            slug = "liquid-glass-aurora-portal",
            description = "A sophisticated portfolio website implementing the 'Liquid Glass Aurora Portal' design system with floating crystalline UI elements.",
            longDescriptionHtml = """
                <p>An advanced portfolio design that abandons traditional flat interfaces for a dimensional experience where UI elements exist as floating crystalline shards within an aurora energy field.</p>
                <p>Design innovations include:</p>
                <ul>
                    <li>Asymmetric glass fragments with beveled edges</li>
                    <li>Chromatic aberration and light refraction effects</li>
                    <li>Gravitational UI with orbital positioning</li>
                    <li>Aurora breathing and portal intensity variations</li>
                    <li>Prismatic navigation with faceted geometry</li>
                </ul>
            """.trimIndent(),
            mainImage = auroraPortal,
            galleryImages = listOf(auroraPortal, glassShard),
            categoryLegacy = "Portfolio Design",
            year = "2024", 
            techStack = listOf("React", "TypeScript", "Three.js", "Django", "CSS3"),
            tags = listOf("Portfolio", "3D Design", "Aurora Effects", "Glass Morphism"),
            liveUrl = "/portfolio",
            repoUrl = "https://github.com/codeyousef/aethelframe-portfolio",
            status = ContentStatus.PUBLISHED,
            sort = 2
        )
        projects[auroraProject.id] = auroraProject
        
        val quantumProject = Project(
            id = "3",
            title = "Quantum Card Components",
            slug = "quantum-card-components",
            description = "Penrose triangle-inspired information containers with impossible geometry effects implemented in pure CSS.",
            longDescriptionHtml = """
                <p>A collection of advanced UI components that implement impossible geometry and optical illusions in web interfaces.</p>
                <p>Features include:</p>
                <ul>
                    <li>Penrose triangle impossible geometry</li>
                    <li>Quantum tunneling transitions</li>
                    <li>Dimensional depth illusions</li>
                    <li>Interactive perspective shifts</li>
                </ul>
            """.trimIndent(),
            mainImage = glassShard,
            categoryLegacy = "Component Library",
            year = "2024",
            techStack = listOf("CSS3", "JavaScript", "WebGL", "SVG"),
            tags = listOf("Components", "Impossible Geometry", "Optical Illusions"),
            status = ContentStatus.PUBLISHED,
            sort = 3
        )
        projects[quantumProject.id] = quantumProject
        
        // Initialize sample journal entries with categories
        val auroraEntry = JournalEntry(
            id = "1",
            title = "Building the Aurora Portal Design System",
            slug = "building-aurora-portal-design-system",
            excerpt = "Exploring the creation of a dimensional UI system that abandons flat glass surfaces for prismatic structures that capture and split portal light.",
            contentRichText = """
                <p>The journey of creating the Aurora Portal design system began with a simple question: What if UI elements existed as floating crystalline shards within an energy field instead of traditional flat interfaces?</p>
                
                <h2>Design Philosophy</h2>
                <p>The Aurora Portal system is built on several core principles:</p>
                <ul>
                    <li><strong>Dimensional Interaction:</strong> Moving beyond 2D layouts to true 3D spatial relationships</li>
                    <li><strong>Gravitational Physics:</strong> UI elements drift toward the central portal when idle</li>
                    <li><strong>Light Refraction:</strong> Portal energy splits into brand colors (teal, maroon, navy)</li>
                    <li><strong>Asymmetric Beauty:</strong> Glass fragments with irregular, beveled geometries</li>
                </ul>
                
                <h2>Technical Implementation</h2>
                <p>Implementing these effects required pushing CSS and WebGL to their limits...</p>
            """.trimIndent(),
            featuredImage = auroraPortal,
            publicationDate = LocalDateTime.now().minusDays(7),
            tags = listOf("Design System", "UI/UX", "CSS", "WebGL"),
            status = ContentStatus.PUBLISHED,
            sort = 1
        )
        journalEntries[auroraEntry.id] = auroraEntry
        
        val kmpEntry = JournalEntry(
            id = "2", 
            title = "Kotlin Multiplatform UI: The Future of Cross-Platform Development",
            slug = "kotlin-multiplatform-ui-future",
            excerpt = "Exploring how Kotlin Multiplatform is revolutionizing UI development by enabling shared business logic while maintaining platform-specific UI excellence.",
            contentRichText = """
                <p>Kotlin Multiplatform (KMP) represents a paradigm shift in how we approach cross-platform development. Unlike other solutions that compromise on native capabilities, KMP allows developers to share business logic while maintaining platform-specific UI excellence.</p>
                
                <h2>The Summon Approach</h2>
                <p>Summon takes this philosophy and applies it to web development, providing:</p>
                <ul>
                    <li>Type-safe CSS styling with compile-time validation</li>
                    <li>Component reusability across JVM and JS platforms</li>
                    <li>Server-side rendering without sacrificing interactivity</li>
                    <li>Framework-agnostic integration patterns</li>
                </ul>
                
                <p>This approach enables teams to build sophisticated web applications while maintaining code quality and developer productivity.</p>
            """.trimIndent(),
            featuredImage = summonLogo,
            publicationDate = LocalDateTime.now().minusDays(14),
            tags = listOf("Kotlin", "Multiplatform", "Web Development", "Architecture"),
            status = ContentStatus.PUBLISHED,
            sort = 2
        )
        journalEntries[kmpEntry.id] = kmpEntry
    
        // Initialize sample services
        val uiFrameworkService = Service(
            id = "1",
            title = "UI Framework Development",
            slug = "ui-framework-development", 
            descriptionRichText = """
                <p>Custom UI framework development and implementation for modern web applications.</p>
                <ul>
                    <li>Component system architecture</li>
                    <li>Type-safe styling solutions</li>
                    <li>Framework integrations</li>
                    <li>Performance optimization</li>
                </ul>
            """.trimIndent(),
            iconSvg = """<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2L2 7V10C2 16 6 21.5 12 22C18 21.5 22 16 22 10V7L12 2Z"/></svg>""",
            status = ContentStatus.PUBLISHED,
            sort = 1
        )
        services[uiFrameworkService.id] = uiFrameworkService
        
        val designSystemsService = Service(
            id = "2", 
            title = "Advanced Design Systems",
            slug = "advanced-design-systems",
            descriptionRichText = """
                <p>Cutting-edge design systems that push the boundaries of web interfaces.</p>
                <ul>
                    <li>Dimensional UI design</li>
                    <li>Aurora portal effects</li>
                    <li>Glass morphism implementation</li>
                    <li>Interactive 3D components</li>
                </ul>
            """.trimIndent(),
            iconSvg = """<svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 11H7v3h2v-3zm4 0h-2v3h2v-3zm4 0h-2v3h2v-3zm2-7h-1V2h-2v2H8V2H6v2H5c-1.1 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/></svg>""",
            status = ContentStatus.PUBLISHED,
            sort = 2
        )
        services[designSystemsService.id] = designSystemsService
        
        val kmpConsultingService = Service(
            id = "3",
            title = "Kotlin Multiplatform Consulting", 
            slug = "kotlin-multiplatform-consulting",
            descriptionRichText = """
                <p>Expert consulting on Kotlin Multiplatform architecture and implementation strategies.</p>
                <ul>
                    <li>Architecture planning</li>
                    <li>Code sharing strategies</li>
                    <li>Platform integration</li>
                    <li>Team training and mentorship</li>
                </ul>
            """.trimIndent(),
            iconSvg = """<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/></svg>""",
            status = ContentStatus.PUBLISHED,
            sort = 3
        )
        services[kmpConsultingService.id] = kmpConsultingService
    }
    
    /**
     * Initialize categories after dependency injection is complete
     */
    @PostConstruct
    fun initCategories() {
        connectWithCategories()
    }
    
    /**
     * Connect projects and journal entries with categories
     * This method should be called after both repositories are fully initialized
     */
    fun connectWithCategories() {
        // Connect projects with categories
        val webDevCategory = categoryRepository.getCategoriesByType(CategoryType.PROJECT)
            .find { it.name == "Web Development" }
        val uiUxCategory = categoryRepository.getCategoriesByType(CategoryType.PROJECT)
            .find { it.name == "UI/UX Design" }
            
        if (webDevCategory != null) {
            projects.values.filter { 
                it.categoryLegacy == "Open Source" || it.categoryLegacy == "Component Library" 
            }.forEach { project ->
                val updatedProject = project.copy(categories = listOf(webDevCategory))
                projects[project.id] = updatedProject
            }
        }
        
        if (uiUxCategory != null) {
            projects.values.filter { 
                it.categoryLegacy == "Portfolio Design" 
            }.forEach { project ->
                val updatedProject = project.copy(categories = listOf(uiUxCategory))
                projects[project.id] = updatedProject
            }
        }
        
        // Connect journal entries with categories
        val tutorialsCategory = categoryRepository.getCategoriesByType(CategoryType.BLOG)
            .find { it.name == "Tutorials" }
        val techNewsCategory = categoryRepository.getCategoriesByType(CategoryType.BLOG)
            .find { it.name == "Tech News" }
            
        if (tutorialsCategory != null) {
            journalEntries.values.filter { 
                it.tags.any { tag -> tag.equals("Design System", ignoreCase = true) } 
            }.forEach { entry ->
                val updatedEntry = entry.copy(categories = listOf(tutorialsCategory))
                journalEntries[entry.id] = updatedEntry
            }
        }
        
        if (techNewsCategory != null) {
            journalEntries.values.filter { 
                it.tags.any { tag -> tag.equals("Kotlin", ignoreCase = true) } 
            }.forEach { entry ->
                val updatedEntry = entry.copy(categories = listOf(techNewsCategory))
                journalEntries[entry.id] = updatedEntry
            }
        }
    }
    
    // Repository implementation methods
    override fun getPublishedProjects(): List<Project> =
        projects.values
            .filter { it.status == ContentStatus.PUBLISHED }
            .sortedBy { it.sort }

    override fun getProjectBySlug(slug: String): Project? =
        projects.values.find { it.slug == slug && it.status == ContentStatus.PUBLISHED }

    override fun getProjectsByTechStack(techStack: String): List<Project> =
        getPublishedProjects().filter { project ->
            project.techStack.any { it.equals(techStack, ignoreCase = true) }
        }

    override fun getProjectsByTag(tag: String): List<Project> =
        getPublishedProjects().filter { project ->
            project.tags.any { it.equals(tag, ignoreCase = true) }
        }
    
    fun getProjectsByCategory(categoryId: String): List<Project> =
        getPublishedProjects().filter { project ->
            project.categories.any { it.id == categoryId }
        }
    
    override fun getPublishedJournalEntries(): List<JournalEntry> =
        journalEntries.values
            .filter { it.status == ContentStatus.PUBLISHED }
            .sortedWith(compareByDescending<JournalEntry> { it.publicationDate }.thenBy { it.sort })
    
    override fun getJournalEntryBySlug(slug: String): JournalEntry? =
        journalEntries.values.find { it.slug == slug && it.status == ContentStatus.PUBLISHED }
    
    override fun getJournalEntriesByTag(tag: String): List<JournalEntry> =
        getPublishedJournalEntries().filter { entry ->
            entry.tags.any { it.equals(tag, ignoreCase = true) }
        }
    
    fun getJournalEntriesByCategory(categoryId: String): List<JournalEntry> =
        getPublishedJournalEntries().filter { entry ->
            entry.categories.any { it.id == categoryId }
        }
    
    override fun getPublishedServices(): List<Service> =
        services.values
            .filter { it.status == ContentStatus.PUBLISHED }
            .sortedBy { it.sort }
    
    override fun getServiceBySlug(slug: String): Service? =
        services.values.find { it.slug == slug && it.status == ContentStatus.PUBLISHED }
        
    // CRUD operations for projects
    override fun createProject(project: Project): Project {
        // Check if slug already exists
        if (getProjectBySlug(project.slug) != null) {
            throw IllegalArgumentException("Project with this slug already exists")
        }
        
        projects[project.id] = project
        return project
    }
    
    override fun updateProject(project: Project): Project {
        // Check if project exists
        if (!projects.containsKey(project.id)) {
            throw IllegalArgumentException("Project not found")
        }
        
        // Check if slug is being changed and if it already exists
        val existingProject = getProjectBySlug(project.slug)
        if (existingProject != null && existingProject.id != project.id) {
            throw IllegalArgumentException("Project with this slug already exists")
        }
        
        projects[project.id] = project
        return project
    }
    
    override fun deleteProject(id: String): Boolean {
        return projects.remove(id) != null
    }
    
    // CRUD operations for journal entries
    override fun createJournalEntry(entry: JournalEntry): JournalEntry {
        // Check if slug already exists
        if (getJournalEntryBySlug(entry.slug) != null) {
            throw IllegalArgumentException("Journal entry with this slug already exists")
        }
        
        journalEntries[entry.id] = entry
        return entry
    }
    
    override fun updateJournalEntry(entry: JournalEntry): JournalEntry {
        // Check if entry exists
        if (!journalEntries.containsKey(entry.id)) {
            throw IllegalArgumentException("Journal entry not found")
        }
        
        // Check if slug is being changed and if it already exists
        val existingEntry = getJournalEntryBySlug(entry.slug)
        if (existingEntry != null && existingEntry.id != entry.id) {
            throw IllegalArgumentException("Journal entry with this slug already exists")
        }
        
        journalEntries[entry.id] = entry
        return entry
    }
    
    override fun deleteJournalEntry(id: String): Boolean {
        return journalEntries.remove(id) != null
    }
    
    // CRUD operations for services
    override fun createService(service: Service): Service {
        // Check if slug already exists
        if (getServiceBySlug(service.slug) != null) {
            throw IllegalArgumentException("Service with this slug already exists")
        }
        
        services[service.id] = service
        return service
    }
    
    override fun updateService(service: Service): Service {
        // Check if service exists
        if (!services.containsKey(service.id)) {
            throw IllegalArgumentException("Service not found")
        }
        
        // Check if slug is being changed and if it already exists
        val existingService = getServiceBySlug(service.slug)
        if (existingService != null && existingService.id != service.id) {
            throw IllegalArgumentException("Service with this slug already exists")
        }
        
        services[service.id] = service
        return service
    }
    
    override fun deleteService(id: String): Boolean {
        return services.remove(id) != null
    }
    
    // Portfolio file operations
    override fun createPortfolioFile(file: PortfolioFile): PortfolioFile {
        // Check if file with this ID already exists
        if (sampleFiles.containsKey(file.id)) {
            throw IllegalArgumentException("Portfolio file with this ID already exists")
        }
        
        sampleFiles[file.id] = file
        return file
    }
    
    override fun updatePortfolioFile(file: PortfolioFile): PortfolioFile {
        // Check if file exists
        if (!sampleFiles.containsKey(file.id)) {
            throw IllegalArgumentException("Portfolio file not found")
        }
        
        sampleFiles[file.id] = file
        return file
    }
    
    override fun deletePortfolioFile(id: String): Boolean {
        return sampleFiles.remove(id) != null
    }
}
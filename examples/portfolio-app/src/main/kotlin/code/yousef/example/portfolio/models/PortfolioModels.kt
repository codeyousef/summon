package code.yousef.example.portfolio.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Base interface for all portfolio content models
 */
interface BaseModel {
    val id: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
    val status: ContentStatus
}

/**
 * Content status enum matching Django model choices
 */
enum class ContentStatus(val value: String, val displayName: String) {
    DRAFT("draft", "Draft"),
    PUBLISHED("published", "Published"),
    ARCHIVED("archived", "Archived");
    
    companion object {
        fun fromValue(value: String): ContentStatus = 
            values().find { it.value == value } ?: DRAFT
    }
}

/**
 * File/Image model for portfolio assets
 */
data class PortfolioFile(
    override val id: String,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now(),
    override val status: ContentStatus = ContentStatus.PUBLISHED,
    val title: String? = null,
    val description: String? = null,
    val filename: String,
    val width: Int? = null,
    val height: Int? = null,
    val filesize: Long? = null,
    val mimeType: String? = null,
    val url: String // Relative URL to the file
) : BaseModel

/**
 * Portfolio Project model matching Django Project model
 */
data class Project(
    override val id: String,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now(),
    override val status: ContentStatus = ContentStatus.DRAFT,
    val title: String,
    val slug: String,
    val description: String? = null,
    val longDescriptionHtml: String? = null,
    val mainImage: PortfolioFile? = null,
    val galleryImages: List<PortfolioFile> = emptyList(),
    val categories: List<Category> = emptyList(),
    val categoryLegacy: String? = null, // For backward compatibility
    val year: String? = null,
    val techStack: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val liveUrl: String? = null,
    val repoUrl: String? = null,
    val sort: Int = 0
) : BaseModel {
    
    companion object {
        fun createSlug(title: String): String {
            return title.lowercase()
                .replace(Regex("[^a-z0-9\\s-]"), "")
                .replace(Regex("\\s+"), "-")
                .trim('-')
        }
        
        /**
         * Create a new project with auto-generated slug
         */
        fun create(
            title: String,
            description: String? = null,
            longDescriptionHtml: String? = null,
            mainImage: PortfolioFile? = null,
            galleryImages: List<PortfolioFile> = emptyList(),
            categories: List<Category> = emptyList(),
            year: String? = null,
            techStack: List<String> = emptyList(),
            tags: List<String> = emptyList(),
            liveUrl: String? = null,
            repoUrl: String? = null,
            sort: Int = 0,
            status: ContentStatus = ContentStatus.DRAFT
        ): Project {
            val slug = createSlug(title)
            return Project(
                id = UUID.randomUUID().toString(),
                title = title,
                slug = slug,
                description = description,
                longDescriptionHtml = longDescriptionHtml,
                mainImage = mainImage,
                galleryImages = galleryImages,
                categories = categories,
                year = year,
                techStack = techStack,
                tags = tags,
                liveUrl = liveUrl,
                repoUrl = repoUrl,
                sort = sort,
                status = status
            )
        }
    }
    
    /**
     * Get the primary category (first in the list) or null if no categories
     */
    val primaryCategory: Category?
        get() = categories.firstOrNull()
}

/**
 * Journal Entry model matching Django JournalEntry model
 * Can be used as a blog post with categories
 */
data class JournalEntry(
    override val id: String,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now(),
    override val status: ContentStatus = ContentStatus.DRAFT,
    val title: String,
    val titleAr: String? = null,
    val slug: String,
    val excerpt: String? = null,
    val excerptAr: String? = null,
    val contentRichText: String? = null,
    val contentRichTextAr: String? = null,
    val language: ContentLanguage = ContentLanguage.ENGLISH,
    val publicationDate: LocalDateTime? = null,
    val featuredImage: PortfolioFile? = null,
    val categories: List<Category> = emptyList(),
    val tags: List<String> = emptyList(),
    val tagsAr: List<String> = emptyList(),
    val sort: Int = 0
) : BaseModel {
    
    val displayTitle: String get() = title
    val displayExcerpt: String? get() = excerpt
    val displayContent: String? get() = contentRichText
    val displayTags: List<String> get() = tags
    
    val formattedPublicationDate: String?
        get() = publicationDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        
    /**
     * Get the primary category (first in the list) or null if no categories
     */
    val primaryCategory: Category?
        get() = categories.firstOrNull()
        
    companion object {
        /**
         * Create a new journal entry with auto-generated slug
         */
        fun create(
            title: String,
            titleAr: String? = null,
            excerpt: String? = null,
            excerptAr: String? = null,
            contentRichText: String? = null,
            contentRichTextAr: String? = null,
            language: ContentLanguage = ContentLanguage.ENGLISH,
            publicationDate: LocalDateTime? = LocalDateTime.now(),
            featuredImage: PortfolioFile? = null,
            categories: List<Category> = emptyList(),
            tags: List<String> = emptyList(),
            tagsAr: List<String> = emptyList(),
            sort: Int = 0,
            status: ContentStatus = ContentStatus.DRAFT
        ): JournalEntry {
            val slug = Project.createSlug(title)
            return JournalEntry(
                id = UUID.randomUUID().toString(),
                title = title,
                titleAr = titleAr,
                slug = slug,
                excerpt = excerpt,
                excerptAr = excerptAr,
                contentRichText = contentRichText,
                contentRichTextAr = contentRichTextAr,
                language = language,
                publicationDate = publicationDate,
                featuredImage = featuredImage,
                categories = categories,
                tags = tags,
                tagsAr = tagsAr,
                sort = sort,
                status = status
            )
        }
    }
}

/**
 * Content language enum
 */
enum class ContentLanguage(val value: String, val displayName: String) {
    ENGLISH("en", "English"),
    ARABIC("ar", "Arabic"),
    BOTH("both", "Both");
    
    companion object {
        fun fromValue(value: String): ContentLanguage = 
            values().find { it.value == value } ?: ENGLISH
    }
}

/**
 * Service model matching Django Service model
 */
data class Service(
    override val id: String,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now(),
    override val status: ContentStatus = ContentStatus.DRAFT,
    val title: String,
    val titleAr: String? = null,
    val slug: String,
    val descriptionRichText: String? = null,
    val descriptionRichTextAr: String? = null,
    val iconSvg: String? = null,
    val featuredImage: PortfolioFile? = null,
    val sort: Int = 0
) : BaseModel {
    
    val displayTitle: String get() = title
    val displayDescription: String? get() = descriptionRichText
}

/**
 * Repository interface for portfolio data operations
 */
interface PortfolioRepository {
    // Project operations
    fun getPublishedProjects(): List<Project>
    fun getProjectBySlug(slug: String): Project?
    fun getProjectsByTechStack(techStack: String): List<Project>
    fun getProjectsByTag(tag: String): List<Project>
    fun createProject(project: Project): Project
    fun updateProject(project: Project): Project
    fun deleteProject(id: String): Boolean
    
    // Journal entry operations
    fun getPublishedJournalEntries(): List<JournalEntry>
    fun getJournalEntryBySlug(slug: String): JournalEntry?
    fun getJournalEntriesByTag(tag: String): List<JournalEntry>
    fun createJournalEntry(entry: JournalEntry): JournalEntry
    fun updateJournalEntry(entry: JournalEntry): JournalEntry
    fun deleteJournalEntry(id: String): Boolean
    
    // Service operations
    fun getPublishedServices(): List<Service>
    fun getServiceBySlug(slug: String): Service?
    fun createService(service: Service): Service
    fun updateService(service: Service): Service
    fun deleteService(id: String): Boolean
    
    // Portfolio file operations
    fun getPortfolioFile(id: String): PortfolioFile?
    fun createPortfolioFile(file: PortfolioFile): PortfolioFile
    fun updatePortfolioFile(file: PortfolioFile): PortfolioFile
    fun deletePortfolioFile(id: String): Boolean
}
package code.yousef.example.portfolio.repository

import code.yousef.example.portfolio.models.*
import code.yousef.example.portfolio.plugins.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * Exposed-based implementation of PortfolioRepository
 */
class PortfolioRepositoryExposedImpl : PortfolioRepository {
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    // Projects
    override fun getAllProjects(): List<Project> = transaction {
        Projects.selectAll()
            .map { it.toProject() }
    }
    
    override fun getPublishedProjects(): List<Project> = transaction {
        Projects.select { Projects.status eq "published" }
            .map { it.toProject() }
    }
    
    override fun getProjectById(id: String): Project? = transaction {
        Projects.select { Projects.id eq UUID.fromString(id) }
            .singleOrNull()
            ?.toProject()
    }
    
    override fun getProjectBySlug(slug: String): Project? = transaction {
        Projects.select { Projects.title eq slug.replace("-", " ") }
            .singleOrNull()
            ?.toProject()
    }
    
    override fun getProjectsByCategory(categoryId: String): List<Project> = transaction {
        Projects.select { Projects.categoryId eq UUID.fromString(categoryId) }
            .map { it.toProject() }
    }
    
    override fun createProject(project: Project): Project = transaction {
        val id = Projects.insert {
            it[title] = project.title
            it[description] = project.description
            it[content] = project.content
            it[imageUrl] = project.imageUrl
            it[techStack] = json.encodeToString(ListSerializer(String.serializer()), project.techStack)
            it[liveUrl] = project.liveUrl
            it[githubUrl] = project.githubUrl
            it[year] = project.year
            it[status] = project.status.value
            it[categoryId] = project.categoryId?.let { UUID.fromString(it) }
        } get Projects.id
        
        project.copy(id = id.value.toString())
    }
    
    override fun updateProject(project: Project): Project = transaction {
        Projects.update({ Projects.id eq UUID.fromString(project.id) }) {
            it[title] = project.title
            it[description] = project.description
            it[content] = project.content
            it[imageUrl] = project.imageUrl
            it[techStack] = json.encodeToString(ListSerializer(String.serializer()), project.techStack)
            it[liveUrl] = project.liveUrl
            it[githubUrl] = project.githubUrl
            it[year] = project.year
            it[status] = project.status.value
            it[categoryId] = project.categoryId?.let { UUID.fromString(it) }
        }
        project
    }
    
    override fun deleteProject(id: String): Boolean = transaction {
        Projects.deleteWhere { Projects.id eq UUID.fromString(id) } > 0
    }
    
    // Journal Entries
    override fun getAllJournalEntries(): List<JournalEntry> = transaction {
        JournalEntries.selectAll()
            .orderBy(JournalEntries.publicationDate, SortOrder.DESC)
            .map { it.toJournalEntry() }
    }
    
    override fun getPublishedJournalEntries(): List<JournalEntry> = transaction {
        JournalEntries.select { JournalEntries.status eq "published" }
            .orderBy(JournalEntries.publicationDate, SortOrder.DESC)
            .map { it.toJournalEntry() }
    }
    
    override fun getJournalEntryById(id: String): JournalEntry? = transaction {
        JournalEntries.select { JournalEntries.id eq UUID.fromString(id) }
            .singleOrNull()
            ?.toJournalEntry()
    }
    
    override fun getJournalEntryBySlug(slug: String): JournalEntry? = transaction {
        JournalEntries.select { JournalEntries.title eq slug.replace("-", " ") }
            .singleOrNull()
            ?.toJournalEntry()
    }
    
    override fun getJournalEntriesByCategory(categoryId: String): List<JournalEntry> = transaction {
        JournalEntries.select { JournalEntries.categoryId eq UUID.fromString(categoryId) }
            .orderBy(JournalEntries.publicationDate, SortOrder.DESC)
            .map { it.toJournalEntry() }
    }
    
    override fun createJournalEntry(entry: JournalEntry): JournalEntry = transaction {
        val id = JournalEntries.insert {
            it[title] = entry.title
            it[content] = entry.content
            it[summary] = entry.summary
            it[imageUrl] = entry.imageUrl
            it[tags] = json.encodeToString(ListSerializer(String.serializer()), entry.tags)
            it[status] = entry.status.value
            it[publicationDate] = entry.publicationDate
            it[categoryId] = entry.categoryId?.let { UUID.fromString(it) }
        } get JournalEntries.id
        
        entry.copy(id = id.value.toString())
    }
    
    override fun updateJournalEntry(entry: JournalEntry): JournalEntry = transaction {
        JournalEntries.update({ JournalEntries.id eq UUID.fromString(entry.id) }) {
            it[title] = entry.title
            it[content] = entry.content
            it[summary] = entry.summary
            it[imageUrl] = entry.imageUrl
            it[tags] = json.encodeToString(ListSerializer(String.serializer()), entry.tags)
            it[status] = entry.status.value
            it[publicationDate] = entry.publicationDate
            it[categoryId] = entry.categoryId?.let { UUID.fromString(it) }
        }
        entry
    }
    
    override fun deleteJournalEntry(id: String): Boolean = transaction {
        JournalEntries.deleteWhere { JournalEntries.id eq UUID.fromString(id) } > 0
    }
    
    // Services
    override fun getAllServices(): List<Service> = transaction {
        Services.selectAll()
            .orderBy(Services.orderIndex)
            .map { it.toService() }
    }
    
    override fun getActiveServices(): List<Service> = transaction {
        Services.select { Services.status eq "active" }
            .orderBy(Services.orderIndex)
            .map { it.toService() }
    }
    
    override fun getServiceById(id: String): Service? = transaction {
        Services.select { Services.id eq UUID.fromString(id) }
            .singleOrNull()
            ?.toService()
    }
    
    override fun createService(service: Service): Service = transaction {
        val id = Services.insert {
            it[title] = service.title
            it[description] = service.description
            it[features] = json.encodeToString(ListSerializer(String.serializer()), service.features)
            it[price] = service.price
            it[icon] = service.icon
            it[orderIndex] = service.orderIndex
            it[status] = service.status.value
        } get Services.id
        
        service.copy(id = id.value.toString())
    }
    
    override fun updateService(service: Service): Service = transaction {
        Services.update({ Services.id eq UUID.fromString(service.id) }) {
            it[title] = service.title
            it[description] = service.description
            it[features] = json.encodeToString(ListSerializer(String.serializer()), service.features)
            it[price] = service.price
            it[icon] = service.icon
            it[orderIndex] = service.orderIndex
            it[status] = service.status.value
        }
        service
    }
    
    override fun deleteService(id: String): Boolean = transaction {
        Services.deleteWhere { Services.id eq UUID.fromString(id) } > 0
    }
    
    // Portfolio Files
    override fun getPortfolioFile(id: String): PortfolioFile? = transaction {
        PortfolioFiles.select { PortfolioFiles.id eq UUID.fromString(id) }
            .singleOrNull()
            ?.toPortfolioFile()
    }
    
    // Helper functions
    private fun ResultRow.toProject(): Project {
        return Project(
            id = this[Projects.id].value.toString(),
            title = this[Projects.title],
            slug = this[Projects.title].lowercase().replace(" ", "-"),
            description = this[Projects.description],
            content = this[Projects.content],
            imageUrl = this[Projects.imageUrl],
            techStack = this[Projects.techStack]?.let { 
                json.decodeFromString(ListSerializer(String.serializer()), it) 
            } ?: emptyList(),
            liveUrl = this[Projects.liveUrl],
            githubUrl = this[Projects.githubUrl],
            year = this[Projects.year],
            status = ContentStatus.fromValue(this[Projects.status]),
            categoryId = this[Projects.categoryId]?.value?.toString(),
            createdAt = this[Projects.createdAt],
            updatedAt = this[Projects.updatedAt]
        )
    }
    
    private fun ResultRow.toJournalEntry(): JournalEntry {
        return JournalEntry(
            id = this[JournalEntries.id].value.toString(),
            title = this[JournalEntries.title],
            slug = this[JournalEntries.title].lowercase().replace(" ", "-"),
            content = this[JournalEntries.content],
            summary = this[JournalEntries.summary],
            imageUrl = this[JournalEntries.imageUrl],
            tags = this[JournalEntries.tags]?.let { 
                json.decodeFromString(ListSerializer(String.serializer()), it) 
            } ?: emptyList(),
            status = ContentStatus.fromValue(this[JournalEntries.status]),
            publicationDate = this[JournalEntries.publicationDate],
            categoryId = this[JournalEntries.categoryId]?.value?.toString(),
            createdAt = this[JournalEntries.createdAt],
            updatedAt = this[JournalEntries.updatedAt]
        )
    }
    
    private fun ResultRow.toService(): Service {
        return Service(
            id = this[Services.id].value.toString(),
            title = this[Services.title],
            description = this[Services.description],
            features = this[Services.features]?.let { 
                json.decodeFromString(ListSerializer(String.serializer()), it) 
            } ?: emptyList(),
            price = this[Services.price],
            icon = this[Services.icon],
            orderIndex = this[Services.orderIndex],
            status = ContentStatus.fromValue(this[Services.status]),
            createdAt = this[Services.createdAt],
            updatedAt = this[Services.updatedAt]
        )
    }
    
    private fun ResultRow.toPortfolioFile(): PortfolioFile {
        return PortfolioFile(
            id = this[PortfolioFiles.id].value.toString(),
            title = this[PortfolioFiles.fileName],
            filename = this[PortfolioFiles.fileName],
            width = 800, // Default values as we don't store dimensions
            height = 600,
            url = this[PortfolioFiles.filePath],
            mimeType = this[PortfolioFiles.fileType]
        )
    }
}
package code.yousef.example.portfolio.plugins

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

// Base table with common fields
abstract class BaseTable(name: String) : UUIDTable(name) {
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}

// User table
object Users : BaseTable("users") {
    val username = varchar("username", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val email = varchar("email", 255).nullable()
    val fullName = varchar("full_name", 255).nullable()
    val role = varchar("role", 50).default("user")
    val status = varchar("status", 50).default("active")
}

// Category table
object Categories : BaseTable("categories") {
    val name = varchar("name", 255).uniqueIndex()
    val description = text("description").nullable()
    val color = varchar("color", 50).nullable()
    val icon = varchar("icon", 255).nullable()
    val orderIndex = integer("order_index").default(0)
}

// Project table
object Projects : BaseTable("projects") {
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val content = text("content").nullable()
    val imageUrl = varchar("image_url", 500).nullable()
    val techStack = text("tech_stack").nullable() // JSON array stored as text
    val liveUrl = varchar("live_url", 500).nullable()
    val githubUrl = varchar("github_url", 500).nullable()
    val year = varchar("year", 10).nullable()
    val status = varchar("status", 50).default("draft")
    val categoryId = reference("category_id", Categories.id).nullable()
}

// Journal Entry (Blog) table
object JournalEntries : BaseTable("journal_entries") {
    val title = varchar("title", 255)
    val content = text("content").nullable()
    val summary = text("summary").nullable()
    val imageUrl = varchar("image_url", 500).nullable()
    val tags = text("tags").nullable() // JSON array stored as text
    val status = varchar("status", 50).default("draft")
    val publicationDate = timestamp("publication_date").nullable()
    val categoryId = reference("category_id", Categories.id).nullable()
}

// Service table
object Services : BaseTable("services") {
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val features = text("features").nullable() // JSON array stored as text
    val price = varchar("price", 100).nullable()
    val icon = varchar("icon", 255).nullable()
    val orderIndex = integer("order_index").default(0)
    val status = varchar("status", 50).default("active")
}

// Portfolio Files table
object PortfolioFiles : BaseTable("portfolio_files") {
    val fileName = varchar("file_name", 255)
    val filePath = varchar("file_path", 500)
    val fileType = varchar("file_type", 100)
    val fileSize = long("file_size")
    val uploadedBy = reference("uploaded_by", Users.id).nullable()
}
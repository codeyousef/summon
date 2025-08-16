package code.yousef.example.portfolio.repository

import code.yousef.example.portfolio.models.*
import code.yousef.example.portfolio.plugins.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * Exposed-based implementation of CategoryRepository
 */
class CategoryRepositoryImpl : CategoryRepository {
    
    init {
        // Initialize default categories if database is empty
        transaction {
            if (Categories.selectAll().count() == 0L) {
                initializeDefaultCategories()
            }
        }
    }
    
    private fun initializeDefaultCategories() {
        // Create default categories for projects
        Categories.insert {
            it[name] = "Web Development"
            it[description] = "Web applications and websites"
            it[color] = "#3498db"
            it[icon] = null
            it[orderIndex] = 1
        }
        
        Categories.insert {
            it[name] = "Mobile Development"
            it[description] = "Mobile applications for iOS and Android"
            it[color] = "#2ecc71"
            it[icon] = null
            it[orderIndex] = 2
        }
        
        Categories.insert {
            it[name] = "UI/UX Design"
            it[description] = "User interface and experience design"
            it[color] = "#9b59b6"
            it[icon] = null
            it[orderIndex] = 3
        }
        
        // Create default categories for blog posts
        Categories.insert {
            it[name] = "Tutorials"
            it[description] = "Step-by-step guides and tutorials"
            it[color] = "#e74c3c"
            it[icon] = null
            it[orderIndex] = 4
        }
        
        Categories.insert {
            it[name] = "Tech News"
            it[description] = "Latest news and updates in technology"
            it[color] = "#f39c12"
            it[icon] = null
            it[orderIndex] = 5
        }
        
        Categories.insert {
            it[name] = "Thoughts & Opinions"
            it[description] = "Personal thoughts and opinions on various topics"
            it[color] = "#1abc9c"
            it[icon] = null
            it[orderIndex] = 6
        }
    }
    
    override fun getAllCategories(): List<Category> = transaction {
        Categories.selectAll()
            .orderBy(Categories.orderIndex)
            .map { it.toCategory() }
    }
    
    override fun getCategoriesByType(type: CategoryType): List<Category> = transaction {
        // Since we removed the type field from the Categories table,
        // we'll need to determine type based on usage in Projects or JournalEntries
        getAllCategories() // For now, return all categories
    }
    
    override fun getCategoryBySlug(slug: String): Category? = transaction {
        Categories.select { Categories.name eq slug.replace("-", " ") }
            .singleOrNull()
            ?.toCategory()
    }
    
    override fun getCategoryById(id: String): Category? = transaction {
        Categories.select { Categories.id eq UUID.fromString(id) }
            .singleOrNull()
            ?.toCategory()
    }
    
    override fun createCategory(category: Category): Category = transaction {
        val id = Categories.insert {
            it[name] = category.name
            it[description] = category.description
            it[color] = category.color
            it[icon] = category.icon
            it[orderIndex] = category.sort
        } get Categories.id
        
        category.copy(id = id.value.toString())
    }
    
    override fun updateCategory(category: Category): Category = transaction {
        Categories.update({ Categories.id eq UUID.fromString(category.id) }) {
            it[name] = category.name
            it[description] = category.description
            it[color] = category.color
            it[icon] = category.icon
            it[orderIndex] = category.sort
        }
        category
    }
    
    override fun deleteCategory(id: String): Boolean = transaction {
        Categories.deleteWhere { Categories.id eq UUID.fromString(id) } > 0
    }
    
    private fun ResultRow.toCategory(): Category {
        return Category(
            id = this[Categories.id].value.toString(),
            name = this[Categories.name],
            slug = this[Categories.name].lowercase().replace(" ", "-"),
            description = this[Categories.description],
            type = CategoryType.BOTH, // Default to BOTH since we don't store type
            color = this[Categories.color],
            icon = this[Categories.icon],
            sort = this[Categories.orderIndex],
            createdAt = this[Categories.createdAt],
            updatedAt = this[Categories.updatedAt]
        )
    }
}
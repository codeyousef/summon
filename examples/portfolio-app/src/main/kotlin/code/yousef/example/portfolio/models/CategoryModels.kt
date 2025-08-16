package code.yousef.example.portfolio.models

import java.time.LocalDateTime
import java.util.*

/**
 * Category type enum to distinguish between project and blog post categories
 */
enum class CategoryType(val value: String, val displayName: String) {
    PROJECT("project", "Project Category"),
    BLOG("blog", "Blog Category"),
    BOTH("both", "Both Types");
    
    companion object {
        fun fromValue(value: String): CategoryType = 
            values().find { it.value == value } ?: BOTH
    }
}

/**
 * Category model for projects and blog posts
 */
data class Category(
    override val id: String = UUID.randomUUID().toString(),
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now(),
    override val status: ContentStatus = ContentStatus.PUBLISHED,
    val name: String,
    val slug: String,
    val description: String? = null,
    val type: CategoryType = CategoryType.BOTH,
    val color: String? = null, // Optional color code for UI display
    val sort: Int = 0
) : BaseModel {
    
    companion object {
        /**
         * Create a new category with auto-generated slug
         */
        fun create(
            name: String,
            description: String? = null,
            type: CategoryType = CategoryType.BOTH,
            color: String? = null,
            sort: Int = 0
        ): Category {
            val slug = createSlug(name)
            return Category(
                name = name,
                slug = slug,
                description = description,
                type = type,
                color = color,
                sort = sort
            )
        }
        
        /**
         * Create a slug from a name
         */
        fun createSlug(name: String): String {
            return name.lowercase()
                .replace(Regex("[^a-z0-9\\s-]"), "")
                .replace(Regex("\\s+"), "-")
                .trim('-')
        }
    }
}

/**
 * Repository interface for category operations
 */
interface CategoryRepository {
    fun getAllCategories(): List<Category>
    fun getCategoriesByType(type: CategoryType): List<Category>
    fun getCategoryBySlug(slug: String): Category?
    fun getCategoryById(id: String): Category?
    fun createCategory(category: Category): Category
    fun updateCategory(category: Category): Category
    fun deleteCategory(id: String): Boolean
}
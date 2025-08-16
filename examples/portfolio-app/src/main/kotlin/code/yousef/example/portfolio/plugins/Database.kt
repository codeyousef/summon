package code.yousef.example.portfolio.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val database = Database.connect(createHikariDataSource())
    
    // Create tables if they don't exist
    transaction(database) {
        SchemaUtils.create(
            Users,
            Categories,
            Projects,
            JournalEntries,
            Services,
            PortfolioFiles
        )
    }
    
    // Initialize default data if needed
    initializeDefaultData(database)
}

private fun createHikariDataSource(): HikariDataSource {
    val config = HikariConfig().apply {
        val dbType = System.getenv("DB_TYPE") ?: "h2"
        
        when (dbType) {
            "postgresql" -> {
                driverClassName = "org.postgresql.Driver"
                jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/portfolio"
                username = System.getenv("DB_USER") ?: "postgres"
                password = System.getenv("DB_PASSWORD") ?: "postgres"
                maximumPoolSize = 10
            }
            else -> { // H2 in-memory database for development
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:portfolio;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
                username = "sa"
                password = ""
                maximumPoolSize = 5
            }
        }
        
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    
    return HikariDataSource(config)
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

private fun initializeDefaultData(database: Database) {
    transaction(database) {
        // Check if admin user exists
        val adminExists = Users.select { Users.username eq "admin" }.count() > 0
        
        if (!adminExists) {
            // Create default admin user
            Users.insert {
                it[username] = "admin"
                it[passwordHash] = org.mindrot.jbcrypt.BCrypt.hashpw("admin123", org.mindrot.jbcrypt.BCrypt.gensalt())
                it[email] = "admin@example.com"
                it[fullName] = "Administrator"
                it[role] = "admin"
                it[status] = "active"
            }
        }
    }
}
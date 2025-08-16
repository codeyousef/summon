package code.yousef.summon.examples.ktor.database

import code.yousef.summon.examples.ktor.models.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object DatabaseConfig {
    fun init() {
        // Connect to SQLite database
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        
        // Create tables
        transaction {
            SchemaUtils.create(Users, Todos)
            
            // Create sample users if none exist
            if (User.all().empty()) {
                createSampleData()
            }
        }
    }
    
    private fun createSampleData() {
        // Create demo user
        val demoUser = User.new {
            username = "demo"
            email = "demo@example.com"
            passwordHash = BCrypt.hashpw("demo", BCrypt.gensalt())
        }
        
        // Create sample todos
        Todo.new {
            userId = demoUser.id
            text = "Welcome to the Ktor Todo App!"
            completed = false
        }
        
        Todo.new {
            userId = demoUser.id
            text = "Try adding a new todo"
            completed = false
        }
        
        Todo.new {
            userId = demoUser.id
            text = "Mark this todo as complete"
            completed = false
        }
        
        Todo.new {
            userId = demoUser.id
            text = "This todo is already done"
            completed = true
        }
        
        Todo.new {
            userId = demoUser.id
            text = "Test real-time updates with WebSockets"
            completed = false
        }
        
        println("Sample data created - Login with username: demo, password: demo")
    }
}

object TodoRepository {
    suspend fun createTodo(userId: Int, text: String): TodoDto = dbQuery {
        val todo = Todo.new {
            this.userId = EntityID(userId, Users)
            this.text = text
            this.completed = false
            this.updatedAt = Clock.System.now()
        }
        todo.toDto()
    }
    
    suspend fun getTodosByUser(userId: Int): List<TodoDto> = dbQuery {
        Todo.find { Todos.userId eq userId }
            .orderBy(Todos.createdAt to SortOrder.DESC)
            .map { it.toDto() }
    }
    
    suspend fun updateTodo(todoId: Int, userId: Int, text: String? = null, completed: Boolean? = null): TodoDto? = dbQuery {
        val todo = Todo.find { (Todos.id eq todoId) and (Todos.userId eq userId) }.firstOrNull()
        todo?.let {
            text?.let { newText -> it.text = newText }
            completed?.let { newCompleted -> it.completed = newCompleted }
            it.updatedAt = Clock.System.now()
            it.toDto()
        }
    }
    
    suspend fun deleteTodo(todoId: Int, userId: Int): Boolean = dbQuery {
        val todo = Todo.find { (Todos.id eq todoId) and (Todos.userId eq userId) }.firstOrNull()
        todo?.let {
            it.delete()
            true
        } ?: false
    }
    
    suspend fun clearCompleted(userId: Int): Int = dbQuery {
        val completedTodos = Todo.find { (Todos.userId eq userId) and (Todos.completed eq true) }
        val count = completedTodos.count().toInt()
        completedTodos.forEach { it.delete() }
        count
    }
}

object UserRepository {
    suspend fun createUser(username: String, email: String, password: String): UserDto? = dbQuery {
        try {
            val user = User.new {
                this.username = username
                this.email = email
                this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
            }
            user.toDto()
        } catch (e: Exception) {
            null // User already exists or other error
        }
    }
    
    suspend fun findUserByUsername(username: String): User? = dbQuery {
        User.find { Users.username eq username }.firstOrNull()
    }
    
    suspend fun findUserById(id: Int): User? = dbQuery {
        User.findById(id)
    }
    
    suspend fun verifyPassword(user: User, password: String): Boolean {
        return BCrypt.checkpw(password, user.passwordHash)
    }
}

suspend fun <T> dbQuery(block: () -> T): T = org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction { block() }
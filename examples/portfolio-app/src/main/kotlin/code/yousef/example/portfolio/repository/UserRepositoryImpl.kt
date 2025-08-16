package code.yousef.example.portfolio.repository

import code.yousef.example.portfolio.models.*
import code.yousef.example.portfolio.plugins.*
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserRepositoryImpl : UserRepository {
    
    override suspend fun getUserByUsername(username: String): User? = dbQuery {
        Users.select { Users.username eq username }
            .singleOrNull()
            ?.toUser()
    }
    
    override suspend fun createUser(user: User): User = dbQuery {
        val id = Users.insert {
            it[username] = user.username
            it[passwordHash] = user.passwordHash
            it[email] = user.email
            it[fullName] = user.fullName
            it[role] = user.role.value
            it[status] = user.status.value
        } get Users.id
        
        user.copy(id = id.value.toString())
    }
    
    override suspend fun updateUser(user: User): User = dbQuery {
        Users.update({ Users.id eq UUID.fromString(user.id) }) {
            it[username] = user.username
            it[passwordHash] = user.passwordHash
            it[email] = user.email
            it[fullName] = user.fullName
            it[role] = user.role.value
            it[status] = user.status.value
        }
        user
    }
    
    override suspend fun deleteUser(id: String): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq UUID.fromString(id) } > 0
    }
    
    override suspend fun getAllUsers(): List<SafeUser> = dbQuery {
        Users.selectAll()
            .map { it.toSafeUser() }
    }
    
    private fun ResultRow.toUser(): User {
        return User(
            id = this[Users.id].value.toString(),
            username = this[Users.username],
            passwordHash = this[Users.passwordHash],
            email = this[Users.email],
            fullName = this[Users.fullName],
            role = UserRole.fromValue(this[Users.role]),
            status = ContentStatus.fromValue(this[Users.status]),
            createdAt = this[Users.createdAt],
            updatedAt = this[Users.updatedAt]
        )
    }
    
    private fun ResultRow.toSafeUser(): SafeUser {
        return SafeUser(
            id = this[Users.id].value.toString(),
            username = this[Users.username],
            email = this[Users.email],
            fullName = this[Users.fullName],
            role = UserRole.fromValue(this[Users.role]),
            status = ContentStatus.fromValue(this[Users.status]),
            createdAt = this[Users.createdAt],
            updatedAt = this[Users.updatedAt]
        )
    }
}
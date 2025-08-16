package code.yousef.example.springboot.repository

import code.yousef.example.springboot.models.Todo
import code.yousef.example.springboot.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    fun findByUserIdOrderByCreatedAtDesc(@Param("userId") userId: Long): List<Todo>
    
    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId AND t.completed = :completed ORDER BY t.createdAt DESC")
    fun findByUserIdAndCompletedOrderByCreatedAtDesc(
        @Param("userId") userId: Long, 
        @Param("completed") completed: Boolean
    ): List<Todo>
    
    @Query("SELECT t FROM Todo t WHERE t.id = :todoId AND t.user.id = :userId")
    fun findByIdAndUserId(@Param("todoId") todoId: Long, @Param("userId") userId: Long): Todo?
    
    @Query("DELETE FROM Todo t WHERE t.user.id = :userId AND t.completed = true")
    fun deleteCompletedByUserId(@Param("userId") userId: Long): Int
    
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.user.id = :userId AND t.completed = false")
    fun countActiveByUserId(@Param("userId") userId: Long): Long
    
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.user.id = :userId AND t.completed = true")
    fun countCompletedByUserId(@Param("userId") userId: Long): Long
}
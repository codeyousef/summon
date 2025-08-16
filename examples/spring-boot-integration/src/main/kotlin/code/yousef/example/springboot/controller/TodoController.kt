package code.yousef.example.springboot.controller

import code.yousef.example.springboot.models.*
import code.yousef.example.springboot.service.TodoService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = ["*"])
class TodoController {
    
    @Autowired
    private lateinit var todoService: TodoService
    
    @GetMapping
    fun getTodos(
        @RequestParam(defaultValue = "ALL") filter: String,
        authentication: Authentication
    ): ResponseEntity<TodoResponse> {
        val todoFilter = try {
            TodoFilter.valueOf(filter.uppercase())
        } catch (e: IllegalArgumentException) {
            TodoFilter.ALL
        }
        
        val todos = todoService.getTodosByUser(authentication.name, todoFilter)
        return ResponseEntity.ok(TodoResponse(
            success = true,
            message = "Todos retrieved successfully",
            todos = todos
        ))
    }
    
    @PostMapping
    fun createTodo(
        @Valid @RequestBody request: CreateTodoRequest,
        authentication: Authentication
    ): ResponseEntity<TodoResponse> {
        val todo = todoService.createTodo(authentication.name, request)
        return if (todo != null) {
            ResponseEntity.ok(TodoResponse(
                success = true,
                message = "Todo created successfully",
                todo = todo
            ))
        } else {
            ResponseEntity.badRequest().body(TodoResponse(
                success = false,
                message = "Failed to create todo"
            ))
        }
    }
    
    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTodoRequest,
        authentication: Authentication
    ): ResponseEntity<TodoResponse> {
        val todo = todoService.updateTodo(authentication.name, id, request)
        return if (todo != null) {
            ResponseEntity.ok(TodoResponse(
                success = true,
                message = "Todo updated successfully",
                todo = todo
            ))
        } else {
            ResponseEntity.badRequest().body(TodoResponse(
                success = false,
                message = "Todo not found or update failed"
            ))
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteTodo(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<TodoResponse> {
        val deleted = todoService.deleteTodo(authentication.name, id)
        return if (deleted) {
            ResponseEntity.ok(TodoResponse(
                success = true,
                message = "Todo deleted successfully"
            ))
        } else {
            ResponseEntity.badRequest().body(TodoResponse(
                success = false,
                message = "Todo not found or delete failed"
            ))
        }
    }
    
    @DeleteMapping("/completed")
    fun clearCompleted(authentication: Authentication): ResponseEntity<TodoResponse> {
        val remainingTodos = todoService.clearCompleted(authentication.name)
        return ResponseEntity.ok(TodoResponse(
            success = true,
            message = "Completed todos cleared successfully",
            todos = remainingTodos
        ))
    }
    
    @GetMapping("/stats")
    fun getTodoStats(authentication: Authentication): ResponseEntity<ApiResponse> {
        val stats = todoService.getTodoStats(authentication.name)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Stats retrieved successfully",
            data = stats
        ))
    }
}
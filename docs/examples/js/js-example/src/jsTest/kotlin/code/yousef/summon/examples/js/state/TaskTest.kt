package code.yousef.summon.examples.js.state

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskTest {
    
    @Test
    fun testTaskCreation() {
        // Create a task
        val task = Task("task-1", "Test Task", false)
        
        // Verify task properties
        assertEquals("task-1", task.id, "Task ID should match")
        assertEquals("Test Task", task.title, "Task title should match")
        assertFalse(task.completed, "Task should not be completed")
    }
    
    @Test
    fun testTaskCopy() {
        // Create a task
        val task = Task("task-1", "Test Task", false)
        
        // Create a copy with modified properties
        val modifiedTask = task.copy(completed = true)
        
        // Verify original task is unchanged
        assertEquals("task-1", task.id, "Original task ID should be unchanged")
        assertEquals("Test Task", task.title, "Original task title should be unchanged")
        assertFalse(task.completed, "Original task should still be not completed")
        
        // Verify modified task has the expected properties
        assertEquals("task-1", modifiedTask.id, "Modified task ID should match original")
        assertEquals("Test Task", modifiedTask.title, "Modified task title should match original")
        assertTrue(modifiedTask.completed, "Modified task should be completed")
    }
    
    @Test
    fun testTaskEquality() {
        // Create two tasks with the same properties
        val task1 = Task("task-1", "Test Task", false)
        val task2 = Task("task-1", "Test Task", false)
        
        // Create a task with different ID
        val task3 = Task("task-2", "Test Task", false)
        
        // Create a task with different title
        val task4 = Task("task-1", "Different Task", false)
        
        // Create a task with different completed status
        val task5 = Task("task-1", "Test Task", true)
        
        // Verify equality
        assertEquals(task1, task2, "Tasks with the same properties should be equal")
        
        // Verify inequality
        assertFalse(task1 == task3, "Tasks with different IDs should not be equal")
        assertFalse(task1 == task4, "Tasks with different titles should not be equal")
        assertFalse(task1 == task5, "Tasks with different completed statuses should not be equal")
    }
    
    @Test
    fun testTaskHashCode() {
        // Create two tasks with the same properties
        val task1 = Task("task-1", "Test Task", false)
        val task2 = Task("task-1", "Test Task", false)
        
        // Verify hash codes are equal
        assertEquals(task1.hashCode(), task2.hashCode(), "Hash codes of equal tasks should be equal")
    }
    
    @Test
    fun testTaskToString() {
        // Create a task
        val task = Task("task-1", "Test Task", false)
        
        // Verify toString output contains all properties
        val taskString = task.toString()
        assertTrue(taskString.contains("task-1"), "toString should contain the task ID")
        assertTrue(taskString.contains("Test Task"), "toString should contain the task title")
        assertTrue(taskString.contains("completed=false"), "toString should contain the completed status")
    }
}
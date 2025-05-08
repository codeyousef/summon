package code.yousef.summon.examples.js.state

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskFilterTest {
    
    @Test
    fun testTaskFilterValues() {
        // Verify that the TaskFilter enum has the expected values
        assertEquals(3, TaskFilter.values().size, "TaskFilter should have 3 values")
        assertEquals(TaskFilter.ALL, TaskFilter.valueOf("ALL"), "TaskFilter should have an ALL value")
        assertEquals(TaskFilter.ACTIVE, TaskFilter.valueOf("ACTIVE"), "TaskFilter should have an ACTIVE value")
        assertEquals(TaskFilter.COMPLETED, TaskFilter.valueOf("COMPLETED"), "TaskFilter should have a COMPLETED value")
    }
    
    @Test
    fun testTaskFilterApplyToTasks() {
        // Create test tasks
        val completedTask = Task("task-1", "Completed Task", true)
        val activeTask = Task("task-2", "Active Task", false)
        val tasks = listOf(completedTask, activeTask)
        
        // Test ALL filter
        val allFilteredTasks = tasks.filter { task ->
            when (TaskFilter.ALL) {
                TaskFilter.ALL -> true
                TaskFilter.ACTIVE -> !task.completed
                TaskFilter.COMPLETED -> task.completed
            }
        }
        assertEquals(2, allFilteredTasks.size, "ALL filter should include all tasks")
        assertTrue(allFilteredTasks.contains(completedTask), "ALL filter should include completed tasks")
        assertTrue(allFilteredTasks.contains(activeTask), "ALL filter should include active tasks")
        
        // Test ACTIVE filter
        val activeFilteredTasks = tasks.filter { task ->
            when (TaskFilter.ACTIVE) {
                TaskFilter.ALL -> true
                TaskFilter.ACTIVE -> !task.completed
                TaskFilter.COMPLETED -> task.completed
            }
        }
        assertEquals(1, activeFilteredTasks.size, "ACTIVE filter should include only active tasks")
        assertFalse(activeFilteredTasks.contains(completedTask), "ACTIVE filter should not include completed tasks")
        assertTrue(activeFilteredTasks.contains(activeTask), "ACTIVE filter should include active tasks")
        
        // Test COMPLETED filter
        val completedFilteredTasks = tasks.filter { task ->
            when (TaskFilter.COMPLETED) {
                TaskFilter.ALL -> true
                TaskFilter.ACTIVE -> !task.completed
                TaskFilter.COMPLETED -> task.completed
            }
        }
        assertEquals(1, completedFilteredTasks.size, "COMPLETED filter should include only completed tasks")
        assertTrue(completedFilteredTasks.contains(completedTask), "COMPLETED filter should include completed tasks")
        assertFalse(completedFilteredTasks.contains(activeTask), "COMPLETED filter should not include active tasks")
    }
    
    @Test
    fun testTaskFilterToString() {
        // Verify toString output
        assertEquals("ALL", TaskFilter.ALL.toString(), "toString should return the enum name")
        assertEquals("ACTIVE", TaskFilter.ACTIVE.toString(), "toString should return the enum name")
        assertEquals("COMPLETED", TaskFilter.COMPLETED.toString(), "toString should return the enum name")
    }
}
package aoko.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores tasks and provides basic list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
        assert this.tasks != null : "Internal tasks list must be initialized";
    }

    /**
     * Creates a task list copied from an existing list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Source task list must not be null";
        this.tasks = new ArrayList<>(tasks);
        assert this.tasks != null : "Internal tasks list must be initialized";
    }

    /**
     * Returns the number of tasks.
     */
    public int size() {
        assert tasks != null : "Internal tasks list must not be null";
        return tasks.size();
    }

    /**
     * Adds a task.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task";
        tasks.add(task);
    }

    /**
     * Returns the task at the given index (0-based).
     */
    public Task get(int zeroBasedIndex) {
        assert zeroBasedIndex >= 0 && zeroBasedIndex < tasks.size() : "Index out of bounds: " + zeroBasedIndex;
        return tasks.get(zeroBasedIndex);
    }

    /**
     * Removes and returns the task at the given index (0-based).
     */
    public Task remove(int zeroBasedIndex) {
        assert zeroBasedIndex >= 0 && zeroBasedIndex < tasks.size() : "Index out of bounds: " + zeroBasedIndex;
        return tasks.remove(zeroBasedIndex);
    }

    /**
     * Returns an unmodifiable view of the tasks.
     */
    public List<Task> asUnmodifiableList() {
        assert tasks != null : "Internal tasks list must not be null";
        return Collections.unmodifiableList(tasks);
    }
}

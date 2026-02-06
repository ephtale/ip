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
    }

    /**
     * Creates a task list copied from an existing list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given index (0-based).
     */
    public Task get(int zeroBasedIndex) {
        return tasks.get(zeroBasedIndex);
    }

    /**
     * Removes and returns the task at the given index (0-based).
     */
    public Task remove(int zeroBasedIndex) {
        return tasks.remove(zeroBasedIndex);
    }

    /**
     * Returns an unmodifiable view of the tasks.
     */
    public List<Task> asUnmodifiableList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Replaces the current contents with the given tasks.
     *
     * @param newTasks Tasks to replace with.
     */
    public void replaceWith(List<Task> newTasks) {
        assert newTasks != null : "newTasks must not be null";
        tasks.clear();
        tasks.addAll(newTasks);
    }
}

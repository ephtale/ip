package aoko.task;

/**
 * Base type for all tasks.
 */
public abstract class Task {
    protected final String description;
    private boolean isDone;

    Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.trim().isEmpty() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks the task as done.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the raw task description.
     */
    public String getDescription() {
        assert description != null : "Task description must not be null";
        return description;
    }

    protected abstract String typeIcon();

    protected String taskDetails() {
        return description;
    }

    private String statusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns the formatted representation shown to the user.
     */
    public String display() {
        assert typeIcon() != null : "Task type icon must not be null";
        return typeIcon() + statusIcon() + " " + taskDetails();
    }
}

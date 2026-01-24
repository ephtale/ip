package aoko.task;

public abstract class Task {
    protected final String description;
    private boolean isDone;

    Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markDone() {
        this.isDone = true;
    }

    public void markNotDone() {
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getDescription() {
        return description;
    }

    protected abstract String typeIcon();

    protected String taskDetails() {
        return description;
    }

    private String statusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    public String display() {
        return typeIcon() + statusIcon() + " " + taskDetails();
    }
}

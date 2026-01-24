package aoko.task;

/**
 * A todo task with only a description.
 */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String typeIcon() {
        return "[T]";
    }
}

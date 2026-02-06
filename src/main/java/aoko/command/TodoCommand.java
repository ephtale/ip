package aoko.command;

import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.task.Todo;
import aoko.ui.Ui;

/**
 * Adds a todo task.
 */
public class TodoCommand implements AokoCommand {
    private static final String EMPTY_DESCRIPTION_MESSAGE =
            "Please provide a description for a todo (e.g., \"todo borrow book\").";

    private final String remainder;

    /**
     * Creates a todo command.
     *
     * @param remainder User input after the command word.
     */
    public TodoCommand(String remainder) {
        this.remainder = remainder == null ? "" : remainder;
        assert this.remainder != null : "Remainder must not be null";
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        assert ui != null : "UI must not be null";
        assert storage != null : "Storage must not be null";
        assert tasks != null : "Task list must not be null";
        assert remainder != null : "Remainder must not be null";

        if (remainder.trim().isEmpty()) {
            ui.showMessageBlock("Please provide a description for a todo (e.g., \"todo borrow book\").");
            return false;
        }

        Task task = new Todo(remainder.trim());
        assert task != null : "Constructed task must not be null";
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
        return false;
    }
}

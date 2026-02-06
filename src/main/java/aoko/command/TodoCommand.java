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
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        String description = remainder.trim();
        if (description.isEmpty()) {
            ui.showMessageBlock(EMPTY_DESCRIPTION_MESSAGE);
            return false;
        }

        Task task = new Todo(description);
        CommandValidation.addTaskAndPersist(task, tasks, storage, ui);
        return false;
    }
}

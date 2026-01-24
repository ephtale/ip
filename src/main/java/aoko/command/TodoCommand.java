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
        if (remainder.trim().isEmpty()) {
            ui.showMessageBlock("Please provide a description for a todo (e.g., \"todo borrow book\").");
            return false;
        }

        Task task = new Todo(remainder.trim());
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
        return false;
    }
}

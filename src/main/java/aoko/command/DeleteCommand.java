package aoko.command;

import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Deletes a task by its 1-based index.
 */
public class DeleteCommand implements AokoCommand {
    private static final String INVALID_INDEX_MESSAGE =
            "Please provide a valid task number to delete (e.g., \"delete 3\").";

    private final String[] parts;

    /**
     * Creates a delete command.
     *
     * @param parts Tokenized user input (command and arguments).
     */
    public DeleteCommand(String[] parts) {
        this.parts = parts;
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        assert ui != null : "UI must not be null";
        assert storage != null : "Storage must not be null";
        assert tasks != null : "Task list must not be null";
        assert parts != null : "Tokenized parts must not be null";

        Integer index = Parser.parseIndex(parts);
        if (index == null || index < 1 || index > tasks.size()) {
            ui.showMessageBlock("Please provide a valid task number to delete (e.g., \"delete 3\").");
            return false;
        }

        assert index >= 1 && index <= tasks.size() : "Validated index must be within range";

        Task removed = tasks.remove(index - 1);
        assert removed != null : "Removing a valid index should return a task";
        storage.save(tasks);
        ui.showDeleted(removed, tasks.size());
        return false;
    }
}

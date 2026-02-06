package aoko.command;

import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Marks a task as not done by its 1-based index.
 */
public class UnmarkCommand implements AokoCommand {
    private static final String INVALID_INDEX_MESSAGE =
            "Please provide a valid task number to unmark (e.g., \"unmark 2\").";

    private final String[] parts;

    /**
     * Creates an unmark command.
     *
     * @param parts Tokenized user input (command and arguments).
     */
    public UnmarkCommand(String[] parts) {
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
            ui.showMessageBlock("Please provide a valid task number to unmark (e.g., \"unmark 2\").");
            return false;
        }

        assert index >= 1 && index <= tasks.size() : "Validated index must be within range";

        Task task = tasks.get(index - 1);
        assert task != null : "Getting a valid index should return a task";
        task.markNotDone();
        storage.save(tasks);
        ui.showUnmarked(task);
        return false;
    }
}

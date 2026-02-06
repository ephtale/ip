package aoko.command;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Marks a task as done by its 1-based index.
 */
public class MarkCommand implements AokoCommand {
    private static final String INVALID_INDEX_MESSAGE =
            "Please provide a valid task number to mark (e.g., \"mark 2\").";

    private final String[] parts;

    /**
     * Creates a mark command.
     *
     * @param parts Tokenized user input (command and arguments).
     */
    public MarkCommand(String[] parts) {
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
            ui.showMessageBlock("Please provide a valid task number to mark (e.g., \"mark 2\").");
            return false;
        }

        assert index >= 1 && index <= tasks.size() : "Validated index must be within range";

        Task task = tasks.get(index - 1);
        assert task != null : "Getting a valid index should return a task";
        task.markDone();
        storage.save(tasks);
        ui.showMarked(task);
        return false;
    }
}

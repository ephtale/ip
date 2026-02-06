package aoko.command;

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
        Integer index = CommandValidation.parseValidTaskIndex(parts, tasks, ui, INVALID_INDEX_MESSAGE);
        if (index == null) {
            return false;
        }

        Task task = tasks.get(index - 1);
        task.markDone();
        storage.save(tasks);
        ui.showMarked(task);
        return false;
    }
}

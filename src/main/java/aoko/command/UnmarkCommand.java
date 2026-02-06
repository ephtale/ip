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
        Integer index = CommandValidation.parseValidTaskIndex(parts, tasks, ui, INVALID_INDEX_MESSAGE);
        if (index == null) {
            return false;
        }

        Task task = tasks.get(index - 1);
        task.markNotDone();
        storage.save(tasks);
        ui.showUnmarked(task);
        return false;
    }
}

package aoko.command;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Deletes a task by its 1-based index.
 */
public class DeleteCommand implements AokoCommand {
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
        Integer index = Parser.parseIndex(parts);
        if (index == null || index < 1 || index > tasks.size()) {
            ui.showMessageBlock("Please provide a valid task number to delete (e.g., \"delete 3\").");
            return false;
        }

        Task removed = tasks.remove(index - 1);
        storage.save(tasks);
        ui.showDeleted(removed, tasks.size());
        return false;
    }
}

package aoko.command;

import aoko.storage.Storage;
import aoko.task.TaskList;
import aoko.ui.Ui;

public interface AokoCommand {
    /**
     * Executes the command.
     *
     * @return true if the program should exit, false otherwise.
     */
    boolean execute(Ui ui, Storage storage, TaskList tasks);
}

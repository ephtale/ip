package aoko.command;

import aoko.storage.Storage;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Exits the application.
 */
public class ByeCommand implements AokoCommand {
    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        return true;
    }
}

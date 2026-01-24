package aoko.command;

import aoko.storage.Storage;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Handles unrecognized commands by showing a help message.
 */
public class UnknownCommand implements AokoCommand {
    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        ui.showUnknownCommand();
        return false;
    }
}

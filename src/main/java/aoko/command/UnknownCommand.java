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
        assert ui != null : "UI must not be null";
        assert storage != null : "Storage must not be null";
        assert tasks != null : "Task list must not be null";
        ui.showUnknownCommand();
        return false;
    }
}

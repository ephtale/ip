package aoko.command;

import aoko.storage.Storage;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Lists all tasks currently in the task list.
 */
public class ListCommand implements AokoCommand {
    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        assert ui != null : "UI must not be null";
        assert storage != null : "Storage must not be null";
        assert tasks != null : "Task list must not be null";
        ui.showList(tasks);
        return false;
    }
}

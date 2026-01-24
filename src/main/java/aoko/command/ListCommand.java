package aoko.command;

import aoko.storage.Storage;
import aoko.task.TaskList;
import aoko.ui.Ui;

public class ListCommand implements AokoCommand {
    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        ui.showList(tasks);
        return false;
    }
}

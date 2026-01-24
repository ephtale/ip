package aoko.command;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

public class UnmarkCommand implements AokoCommand {
    private final String[] parts;

    public UnmarkCommand(String[] parts) {
        this.parts = parts;
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        Integer index = Parser.parseIndex(parts);
        if (index == null || index < 1 || index > tasks.size()) {
            ui.showMessageBlock("Please provide a valid task number to unmark (e.g., \"unmark 2\").");
            return false;
        }

        Task task = tasks.get(index - 1);
        task.markNotDone();
        storage.save(tasks);
        ui.showUnmarked(task);
        return false;
    }
}

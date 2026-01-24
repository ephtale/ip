package aoko.command;

import java.util.ArrayList;
import java.util.List;

import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Finds tasks whose description contains a given keyword.
 */
public class FindCommand implements AokoCommand {
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param remainder Raw user input after the command word.
     */
    public FindCommand(String remainder) {
        this.keyword = remainder == null ? "" : remainder.trim();
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        if (keyword.isEmpty()) {
            ui.showMessageBlock("Please provide a keyword to search (e.g., \"find book\").");
            return false;
        }

        String needle = keyword.toLowerCase();
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks.asUnmodifiableList()) {
            if (task.getDescription().toLowerCase().contains(needle)) {
                matches.add(task);
            }
        }

        ui.showFind(matches);
        return false;
    }
}

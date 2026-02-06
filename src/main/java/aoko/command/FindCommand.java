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
        assert this.keyword != null : "Keyword must not be null";
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        assert ui != null : "UI must not be null";
        assert storage != null : "Storage must not be null";
        assert tasks != null : "Task list must not be null";
        assert keyword != null : "Keyword must not be null";

        if (keyword.isEmpty()) {
            ui.showMessageBlock("Please provide a keyword to search (e.g., \"find book\").");
            return false;
        }

        String needle = keyword.toLowerCase();
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks.asUnmodifiableList()) {
            assert task != null : "Task list must not contain null entries";
            if (task.getDescription().toLowerCase().contains(needle)) {
                matches.add(task);
            }
        }

        ui.showFind(matches);
        return false;
    }
}

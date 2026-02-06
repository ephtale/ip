package aoko.command;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Deadline;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Adds a deadline task with a parsed date/time.
 */
public class DeadlineCommand implements AokoCommand {
    private static final String DEADLINE_USAGE =
            "Please use: deadline <description> /by <by> (e.g., \"deadline return book /by Sunday\").";

    private final String remainder;

    /**
     * Creates a deadline command.
     *
     * @param remainder User input after the command word.
     */
    public DeadlineCommand(String remainder) {
        this.remainder = remainder == null ? "" : remainder;
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        int byIndex = remainder.indexOf("/by");
        if (remainder.trim().isEmpty() || byIndex < 0) {
            ui.showMessageBlock(DEADLINE_USAGE);
            return false;
        }

        String description = remainder.substring(0, byIndex).trim();
        String by = remainder.substring(byIndex + 3).trim();
        if (description.isEmpty() || by.isEmpty()) {
            ui.showMessageBlock(DEADLINE_USAGE);
            return false;
        }

        Parser.ParsedDateTime dateTime = Parser.parseDateTime(by);
        if (dateTime == null) {
            ui.showMessageBlock(
                    "I couldn't understand that date/time.",
                    "Try: yyyy-MM-dd (e.g., 2019-10-15) or d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
            return false;
        }

        Task task = new Deadline(description, dateTime.dateTime, dateTime.hasTime);
        CommandValidation.addTaskAndPersist(task, tasks, storage, ui);
        return false;
    }
}

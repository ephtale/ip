package aoko.command;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Deadline;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

public class DeadlineCommand implements AokoCommand {
    private final String remainder;

    public DeadlineCommand(String remainder) {
        this.remainder = remainder == null ? "" : remainder;
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        int byIndex = remainder.indexOf("/by");
        if (remainder.trim().isEmpty() || byIndex < 0) {
            ui.showMessageBlock(
                    "Please use: deadline <description> /by <by> (e.g., \"deadline return book /by Sunday\").");
            return false;
        }

        String description = remainder.substring(0, byIndex).trim();
        String by = remainder.substring(byIndex + 3).trim();
        if (description.isEmpty() || by.isEmpty()) {
            ui.showMessageBlock(
                    "Please use: deadline <description> /by <by> (e.g., \"deadline return book /by Sunday\").");
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
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
        return false;
    }
}

package aoko.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Deadline;
import aoko.task.Event;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Lists deadline/event tasks that occur on a specified date.
 */
public class OnCommand implements AokoCommand {
    private final String remainder;

    /**
     * Creates an on command.
     *
     * @param remainder User input after the command word.
     */
    public OnCommand(String remainder) {
        this.remainder = remainder == null ? "" : remainder;
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        if (remainder.trim().isEmpty()) {
            ui.showMessageBlock(
                    "Please provide a date (e.g., \"on 2019-10-15\" or \"on 2/12/2019\").");
            return false;
        }

        LocalDate date = Parser.parseDateOnly(remainder);
        if (date == null) {
            ui.showMessageBlock(
                    "I couldn't understand that date.",
                    "Try: yyyy-MM-dd (e.g., 2019-10-15) or d/M/yyyy (e.g., 2/12/2019)");
            return false;
        }

        List<Task> matches = new ArrayList<>();
        for (Task task : tasks.asUnmodifiableList()) {
            if (task instanceof Deadline deadline) {
                if (deadline.getBy().toLocalDate().equals(date)) {
                    matches.add(task);
                }
                continue;
            }
            if (task instanceof Event event) {
                LocalDate fromDate = event.getFrom().toLocalDate();
                LocalDate toDate = event.getTo().toLocalDate();
                if ((date.isEqual(fromDate) || date.isAfter(fromDate))
                        && (date.isEqual(toDate) || date.isBefore(toDate))) {
                    matches.add(task);
                }
            }
        }

        ui.showOn(date, matches);
        return false;
    }
}

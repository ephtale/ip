package aoko.command;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Event;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Adds an event task with a start and end date/time.
 */
public class EventCommand implements AokoCommand {
    private final String remainder;

    /**
     * Creates an event command.
     *
     * @param remainder User input after the command word.
     */
    public EventCommand(String remainder) {
        this.remainder = remainder == null ? "" : remainder;
    }

    @Override
    public boolean execute(Ui ui, Storage storage, TaskList tasks) {
        int fromIndex = remainder.indexOf("/from");
        int toIndex = remainder.indexOf("/to");
        if (remainder.trim().isEmpty() || fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            ui.showMessageBlock(
                    "Please use: event <description> /from <from> /to <to> (e.g., \"event project meeting /from Mon 2pm /to 4pm\").");
            return false;
        }

        String description = remainder.substring(0, fromIndex).trim();
        String from = remainder.substring(fromIndex + 5, toIndex).trim();
        String to = remainder.substring(toIndex + 3).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            ui.showMessageBlock(
                    "Please use: event <description> /from <from> /to <to> (e.g., \"event project meeting /from Mon 2pm /to 4pm\").");
            return false;
        }

        Parser.ParsedDateTime fromParsed = Parser.parseDateTime(from);
        if (fromParsed == null) {
            ui.showMessageBlock(
                    "I couldn't understand the event start date/time.",
                    "Try: yyyy-MM-dd (e.g., 2019-10-15) or d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
            return false;
        }

        Parser.ParsedDateTime toParsed = Parser.parseEventEnd(fromParsed, to);
        if (toParsed == null) {
            ui.showMessageBlock(
                    "I couldn't understand the event end date/time.",
                    "Try: yyyy-MM-dd (e.g., 2019-10-15), d/M/yyyy HHmm (e.g., 2/12/2019 1800), or time-only HHmm/HH:mm (e.g., 1600)");
            return false;
        }

        if (toParsed.dateTime.isBefore(fromParsed.dateTime)) {
            ui.showMessageBlock("The event end must not be before the start.");
            return false;
        }

        Task task = new Event(description, fromParsed.dateTime, fromParsed.hasTime, toParsed.dateTime, toParsed.hasTime);
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
        return false;
    }
}

package aoko.command;

import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Shared validation helpers for command implementations.
 */
final class CommandValidation {
    private CommandValidation() {
        // utility class
    }

    /**
     * Parses and validates a 1-based task index from tokenized input.
     *
     * <p>If invalid, prints {@code invalidMessage} via {@link Ui} and returns {@code null}.
     */
    static Integer parseValidTaskIndex(String[] parts, TaskList tasks, Ui ui, String invalidMessage) {
        Integer index = Parser.parseIndex(parts);
        if (index == null || index < 1 || index > tasks.size()) {
            ui.showMessageBlock(invalidMessage);
            return null;
        }
        return index;
    }

    static void addTaskAndPersist(Task task, TaskList tasks, Storage storage, Ui ui) {
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
    }
}

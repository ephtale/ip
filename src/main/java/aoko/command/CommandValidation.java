package aoko.command;

import aoko.parser.Parser;
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
}

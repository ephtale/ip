package aoko;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import aoko.command.AokoCommand;
import aoko.command.CommandFactory;
import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.TaskList;
import aoko.ui.Ui;

/**
 * Stateful execution engine for Aoko commands.
 *
 * <p>Holds the storage + task list so different UIs (CLI/GUI/tests) can execute commands against
 * the same in-memory state.
 */
public class AokoEngine {
    /**
     * Result of processing a single user input.
     */
    public static class EngineResponse {
        /** Text output that would have been printed for this input. */
        public final String output;

        /** Whether the application should exit after this input. */
        public final boolean shouldExit;

        /**
         * Creates a response.
         *
         * @param output Text output that would have been printed.
         * @param shouldExit Whether the application should exit.
         */
        public EngineResponse(String output, boolean shouldExit) {
            this.output = output;
            this.shouldExit = shouldExit;
        }
    }

    private final Storage storage;
    private final TaskList tasks;
    private final Deque<List<String>> undoStack;

    /**
     * Creates an engine backed by the given save path.
     *
     * @param savePath Path of the save file.
     */
    public AokoEngine(Path savePath) {
        this.storage = new Storage(savePath);
        this.tasks = new TaskList(storage.load());
        this.undoStack = new ArrayDeque<>();
    }

    private static boolean isUndoableMutation(Parser.Command command) {
        return switch (command) {
        case TODO, DEADLINE, EVENT, DELETE, MARK, UNMARK -> true;
        default -> false;
        };
    }

    /**
     * Prints welcome using the provided UI.
     */
    public void showWelcome(Ui ui) {
        ui.showWelcome();
    }

    /**
     * Processes a single line of input using the provided UI.
     *
     * @return true if the application should exit.
     */
    public boolean process(String userInput, Ui ui) {
        assert userInput != null : "userInput must not be null";
        assert ui != null : "ui must not be null";

        Parser.ParsedCommand parsed = Parser.parseCommand(userInput);

        if (parsed.command == Parser.Command.UNDO) {
            undo(ui);
            return false;
        }

        List<String> beforeSnapshot = null;
        if (isUndoableMutation(parsed.command)) {
            beforeSnapshot = storage.snapshot(tasks);
        }

        AokoCommand command = CommandFactory.fromParsed(parsed);
        boolean exit;
        try {
            exit = command.execute(ui, storage, tasks);
        } catch (RuntimeException e) {
            ui.showMessageBlock("Something went wrong while executing that command.");
            return false;
        }
        if (exit) {
            ui.showBye();
        }

        if (!exit && beforeSnapshot != null) {
            List<String> afterSnapshot = storage.snapshot(tasks);
            if (!afterSnapshot.equals(beforeSnapshot)) {
                undoStack.push(beforeSnapshot);
            }
        }
        return exit;
    }

    /**
     * Undoes the most recent successful state-changing command.
     */
    private void undo(Ui ui) {
        assert ui != null : "ui must not be null";

        if (undoStack.isEmpty()) {
            ui.showUndoEmpty();
            return;
        }

        List<String> snapshot = undoStack.pop();
        try {
            storage.restore(tasks, snapshot);
            ui.showUndoSuccess();
        } catch (RuntimeException e) {
            ui.showMessageBlock("Failed to undo the most recent change.");
        }
    }

    /**
     * Convenience for GUI/testing: processes input and returns what would have been printed.
     */
    public EngineResponse processToString(String userInput) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(ps);
            boolean exit = process(userInput, ui);
            return new EngineResponse(baos.toString(StandardCharsets.UTF_8), exit);
        }
    }

    /**
     * Convenience for GUI/testing: returns the welcome message as a string.
     */
    public String welcomeToString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(ps);
            showWelcome(ui);
            return baos.toString(StandardCharsets.UTF_8);
        }
    }
}

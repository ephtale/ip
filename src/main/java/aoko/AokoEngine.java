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
 */
public class AokoEngine {
    private static class Captured<T> {
        private final String output;
        private final T result;

        private Captured(String output, T result) {
            this.output = output;
            this.result = result;
        }
    }

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
        assert savePath != null : "Save path must not be null";
        this.storage = new Storage(savePath);
        assert this.storage != null : "Storage should be constructed";

        var loadedTasks = storage.load();
        assert loadedTasks != null : "Storage.load() must not return null";

        this.tasks = new TaskList(loadedTasks);
        assert this.tasks != null : "TaskList should be constructed";
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
        assert ui != null : "UI must be provided";
        ui.showWelcome();
    }

    /**
     * Processes a single line of input using the provided UI.
     *
     * @return true if the application should exit.
     */
    public boolean process(String userInput, Ui ui) {
        assert userInput != null : "User input must not be null";
        assert ui != null : "UI must not be null";
        assert storage != null : "Storage must be initialized";
        assert tasks != null : "Task list must be initialized";

        Parser.ParsedCommand parsed = Parser.parseCommand(userInput);

        if (parsed.command == Parser.Command.UNDO) {
            undo(ui);
            return false;
        }

        List<String> beforeSnapshot = null;
        if (isUndoableMutation(parsed.command)) {
            beforeSnapshot = storage.snapshot(tasks);
        }

        assert parsed != null : "Parser.parseCommand must not return null";
        assert parsed.parts != null : "Parsed command parts must not be null";
        assert parsed.remainder != null : "Parsed remainder must not be null";

        AokoCommand command = CommandFactory.fromParsed(parsed);
        boolean shouldExit;
        try {
            shouldExit = command.execute(ui, storage, tasks);
        } catch (RuntimeException e) {
            ui.showMessageBlock("Something went wrong while executing that command.");
            return false;
        }
        assert command != null : "CommandFactory must always return a command";
        if (shouldExit) {
            ui.showBye();
        }

        if (!shouldExit && beforeSnapshot != null) {
            List<String> afterSnapshot = storage.snapshot(tasks);
            if (!afterSnapshot.equals(beforeSnapshot)) {
                undoStack.push(beforeSnapshot);
            }
        }
        return shouldExit;
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
     * Processes input and returns what would have been printed.
     */
    public EngineResponse processToString(String userInput) {
        assert userInput != null : "User input must not be null";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(ps);
            assert ui != null : "UI should be constructed";
            boolean shouldExit = process(userInput, ui);
            String output = baos.toString(StandardCharsets.UTF_8);
            assert output != null : "Captured output must not be null";
            return new EngineResponse(output, shouldExit);
        }
    }

    /**
     * Returns the welcome message as a string.
     */
    public String welcomeToString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(ps);
            assert ui != null : "UI should be constructed";
            showWelcome(ui);
            String welcome = baos.toString(StandardCharsets.UTF_8);
            assert welcome != null : "Welcome message must not be null";
            return welcome;
        }
    }
}

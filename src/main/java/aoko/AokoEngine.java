package aoko;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;

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

    /**
     * Creates an engine backed by the given save path.
     *
     * @param savePath Path of the save file.
     */
    public AokoEngine(Path savePath) {
        this.storage = new Storage(savePath);
        this.tasks = new TaskList(storage.load());
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
        Parser.ParsedCommand parsed = Parser.parseCommand(userInput);
        AokoCommand command = CommandFactory.fromParsed(parsed);
        boolean exit = command.execute(ui, storage, tasks);
        if (exit) {
            ui.showBye();
        }
        return exit;
    }

    private static <T> Captured<T> captureOutput(Function<Ui, T> action) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(ps);
            T result = action.apply(ui);
            return new Captured<>(baos.toString(StandardCharsets.UTF_8), result);
        }
    }

    /**
     * Convenience for GUI/testing: processes input and returns what would have been printed.
     */
    public EngineResponse processToString(String userInput) {
        Captured<Boolean> captured = captureOutput(ui -> process(userInput, ui));
        return new EngineResponse(captured.output, captured.result);
    }

    /**
     * Convenience for GUI/testing: returns the welcome message as a string.
     */
    public String welcomeToString() {
        Captured<Void> captured = captureOutput(ui -> {
            showWelcome(ui);
            return null;
        });
        return captured.output;
    }
}

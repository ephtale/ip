package aoko;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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

package aoko;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import aoko.ui.Ui;

/**
 * Entry point for the Aoko chatbot application.
 *
 * <p>Wires together the UI, storage, and task list, then runs the main input loop.
 */
public class Aoko {
    private static final Path SAVE_PATH = Paths.get("data", "aoko.txt");

    /**
     * Starts the chatbot.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        AokoEngine engine = new AokoEngine(SAVE_PATH);

        engine.showWelcome(ui);
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine().trim();
                if (userInput.isEmpty()) {
                    continue;
                }

                boolean exit = engine.process(userInput, ui);
                if (exit) {
                    break;
                }
            }
        }
    }
}

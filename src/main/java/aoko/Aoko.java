package aoko;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import aoko.ui.Ui;

/**
 * Entry point for the Aoko chatbot application.
 */
public class Aoko {
    private static final Path SAVE_PATH = Paths.get("data", "aoko.txt");

    /**
     * Starts the chatbot.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        assert SAVE_PATH != null : "Save path must be initialized";
        Ui ui = new Ui();
        AokoEngine engine = new AokoEngine(SAVE_PATH);

        assert ui != null : "UI should be constructed";
        assert engine != null : "Engine should be constructed";

        engine.showWelcome(ui);
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine().trim();
                assert userInput != null : "Scanner.nextLine() should not return null";
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

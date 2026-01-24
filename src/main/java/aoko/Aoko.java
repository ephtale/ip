package aoko;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import aoko.command.AokoCommand;
import aoko.command.CommandFactory;
import aoko.parser.Parser;
import aoko.storage.Storage;
import aoko.task.TaskList;
import aoko.ui.Ui;

public class Aoko {
    private static final Path SAVE_PATH = Paths.get("data", "aoko.txt");
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(SAVE_PATH);
        TaskList tasks = new TaskList(storage.load());

        ui.showWelcome();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (!scanner.hasNextLine()) {
                    break; // EOF
                }
                String userInput = scanner.nextLine().trim();
                if (userInput.isEmpty()) {
                    continue;
                }

                boolean exit = handleCommand(ui, storage, tasks, userInput);
                if (exit) {
                    break;
                }
            }
        }

        ui.showBye();
    }

    private static boolean handleCommand(Ui ui, Storage storage, TaskList tasks, String userInput) {
        Parser.ParsedCommand parsed = Parser.parseCommand(userInput);
        AokoCommand command = CommandFactory.fromParsed(parsed);
        return command.execute(ui, storage, tasks);
    }
}

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Aoko {
    private static final String LINE = "____________________________________________________________";

    private static class Task {
        private final String description;
        private boolean isDone;

        Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        void markDone() {
            this.isDone = true;
        }

        void markNotDone() {
            this.isDone = false;
        }

        String statusIcon() {
            return isDone ? "[X]" : "[ ]";
        }

        String display() {
            return statusIcon() + " " + description;
        }
    }

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println("Hello! I'm Aoko, your Magecraft assistant.\nHow may I help you today?\n");
        System.out.println(LINE);

        List<Task> tasks = new ArrayList<>();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (!scanner.hasNextLine()) {
                    break; // EOF
                }
                String userInput = scanner.nextLine().trim();
            if (userInput.isEmpty()) {
                continue;
            }

            boolean exit = false;
            String[] parts = userInput.split("\\s+", 2);
            String command = parts[0].toLowerCase();

            switch (command) {
                case "list" -> {
                    System.out.println(LINE);
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i).display());
                    }
                    System.out.println(LINE);
                }
                case "mark" -> {
                    Integer index = parseIndex(parts);
                    if (index == null || index < 1 || index > tasks.size()) {
                        System.out.println(LINE);
                        System.out.println("Please provide a valid task number to mark (e.g., \"mark 2\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = tasks.get(index - 1);
                    task.markDone();

                    System.out.println(LINE);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task.display());
                    System.out.println(LINE);
                }
                case "unmark" -> {
                    Integer index = parseIndex(parts);
                    if (index == null || index < 1 || index > tasks.size()) {
                        System.out.println(LINE);
                        System.out.println("Please provide a valid task number to unmark (e.g., \"unmark 2\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = tasks.get(index - 1);
                    task.markNotDone();

                    System.out.println(LINE);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task.display());
                    System.out.println(LINE);
                }
                case "bye" -> exit = true;
                default -> {
                    System.out.println(LINE);
                    System.out.println("added: " + userInput + "\n");
                    System.out.println(LINE);
                    tasks.add(new Task(userInput));
                }
            }

            if (exit) {
                break;
            }
        }
        }

        System.out.println(LINE);
        System.out.println("See you again soon!\n");
        System.out.println(LINE);
    }

    private static Integer parseIndex(String[] parts) {
        if (parts.length < 2) {
            return null;
        }
        try {
            return Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

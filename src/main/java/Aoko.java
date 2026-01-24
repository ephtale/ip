import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Aoko {
    private static final String LINE = "____________________________________________________________";
    private static final Path SAVE_PATH = Paths.get("data", "aoko.txt");

    private enum Command {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, BYE, UNKNOWN;

        static Command parse(String token) {
            if (token == null) {
                return UNKNOWN;
            }
            return switch (token.toLowerCase()) {
                case "list" -> LIST;
                case "mark" -> MARK;
                case "unmark" -> UNMARK;
                case "delete" -> DELETE;
                case "todo" -> TODO;
                case "deadline" -> DEADLINE;
                case "event" -> EVENT;
                case "bye" -> BYE;
                default -> UNKNOWN;
            };
        }
    }

    private abstract static class Task {
        protected final String description;
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

        boolean isDone() {
            return isDone;
        }

        protected abstract String typeIcon();

        protected String taskDetails() {
            return description;
        }

        private String statusIcon() {
            return isDone ? "[X]" : "[ ]";
        }

        String display() {
            return typeIcon() + statusIcon() + " " + taskDetails();
        }
    }

    private static class Todo extends Task {
        Todo(String description) {
            super(description);
        }

        @Override
        protected String typeIcon() {
            return "[T]";
        }
    }

    private static class Deadline extends Task {
        private final String by;

        Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        protected String typeIcon() {
            return "[D]";
        }

        @Override
        protected String taskDetails() {
            return description + " (by: " + by + ")";
        }
    }

    private static class Event extends Task {
        private final String from;
        private final String to;

        Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        protected String typeIcon() {
            return "[E]";
        }

        @Override
        protected String taskDetails() {
            return description + " (from: " + from + " to: " + to + ")";
        }
    }

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println("Hello! I'm Aoko, your Magecraft assistant.\nHow may I help you today?\n");
        System.out.println(LINE);

        List<Task> tasks = loadTasks(SAVE_PATH);
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
                Command command = Command.parse(parts[0]);
                String remainder = parts.length > 1 ? parts[1].trim() : "";

                switch (command) {
                case LIST -> {
                    System.out.println(LINE);
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i).display());
                    }
                    System.out.println(LINE);
                }
                case DELETE -> {
                    Integer index = parseIndex(parts);
                    if (index == null || index < 1 || index > tasks.size()) {
                        System.out.println(LINE);
                        System.out.println("Please provide a valid task number to delete (e.g., \"delete 3\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task removed = tasks.remove(index - 1);
                    saveTasksSafely(SAVE_PATH, tasks);
                    System.out.println(LINE);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removed.display());
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(LINE);
                }
                case MARK -> {
                    Integer index = parseIndex(parts);
                    if (index == null || index < 1 || index > tasks.size()) {
                        System.out.println(LINE);
                        System.out.println("Please provide a valid task number to mark (e.g., \"mark 2\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = tasks.get(index - 1);
                    task.markDone();
                    saveTasksSafely(SAVE_PATH, tasks);

                    System.out.println(LINE);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task.display());
                    System.out.println(LINE);
                }
                case UNMARK -> {
                    Integer index = parseIndex(parts);
                    if (index == null || index < 1 || index > tasks.size()) {
                        System.out.println(LINE);
                        System.out.println("Please provide a valid task number to unmark (e.g., \"unmark 2\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = tasks.get(index - 1);
                    task.markNotDone();
                    saveTasksSafely(SAVE_PATH, tasks);

                    System.out.println(LINE);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task.display());
                    System.out.println(LINE);
                }
                case TODO -> {
                    if (remainder.isEmpty()) {
                        System.out.println(LINE);
                        System.out.println("Please provide a description for a todo (e.g., \"todo borrow book\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = new Todo(remainder);
                    tasks.add(task);
                    saveTasksSafely(SAVE_PATH, tasks);
                    printTaskAdded(task, tasks.size());
                }
                case DEADLINE -> {
                    int byIndex = remainder.indexOf("/by");
                    if (remainder.isEmpty() || byIndex < 0) {
                        System.out.println(LINE);
                        System.out.println("Please use: deadline <description> /by <by> (e.g., \"deadline return book /by Sunday\").");
                        System.out.println(LINE);
                        break;
                    }

                    String description = remainder.substring(0, byIndex).trim();
                    String by = remainder.substring(byIndex + 3).trim();
                    if (description.isEmpty() || by.isEmpty()) {
                        System.out.println(LINE);
                        System.out.println("Please use: deadline <description> /by <by> (e.g., \"deadline return book /by Sunday\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    saveTasksSafely(SAVE_PATH, tasks);
                    printTaskAdded(task, tasks.size());
                }
                case EVENT -> {
                    int fromIndex = remainder.indexOf("/from");
                    int toIndex = remainder.indexOf("/to");
                    if (remainder.isEmpty() || fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
                        System.out.println(LINE);
                        System.out.println("Please use: event <description> /from <from> /to <to> (e.g., \"event project meeting /from Mon 2pm /to 4pm\").");
                        System.out.println(LINE);
                        break;
                    }

                    String description = remainder.substring(0, fromIndex).trim();
                    String from = remainder.substring(fromIndex + 5, toIndex).trim();
                    String to = remainder.substring(toIndex + 3).trim();
                    if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                        System.out.println(LINE);
                        System.out.println("Please use: event <description> /from <from> /to <to> (e.g., \"event project meeting /from Mon 2pm /to 4pm\").");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = new Event(description, from, to);
                    tasks.add(task);
                    saveTasksSafely(SAVE_PATH, tasks);
                    printTaskAdded(task, tasks.size());
                }
                case BYE -> exit = true;
                case UNKNOWN -> {
                    System.out.println(LINE);
                    System.out.println("That's not a command I recognize.");
                    System.out.println("Available commands: list, mark, unmark, delete, todo, deadline, event, bye");
                    System.out.println(LINE);
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

    private static void printTaskAdded(Task task, int newSize) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task.display());
        System.out.println("Now you have " + newSize + " tasks in the list.");
        System.out.println(LINE);
    }

    private static void saveTasksSafely(Path path, List<Task> tasks) {
        try {
            saveTasks(path, tasks);
        } catch (IOException e) {
            System.err.println("Failed to save tasks to disk: " + e.getMessage());
        }
    }

    private static void saveTasks(Path path, List<Task> tasks) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = tasks.stream()
                .map(Aoko::encodeTask)
                .collect(Collectors.toList());

        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private static String encodeTask(Task task) {
        String doneFlag = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + doneFlag + " | " + task.description;
        }
        if (task instanceof Deadline deadline) {
            return "D | " + doneFlag + " | " + deadline.description + " | " + deadline.by;
        }
        if (task instanceof Event event) {
            return "E | " + doneFlag + " | " + event.description + " | " + event.from + " | " + event.to;
        }

        return "T | " + doneFlag + " | " + task.description;
    }

    private static List<Task> loadTasks(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (String line : lines) {
                Task task = decodeTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException e) {
            System.err.println("Failed to load tasks from disk: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static Task decodeTask(String line) {
        if (line == null) {
            return null;
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] parts = trimmed.split("\\s*\\|\\s*");
        try {
            String type = parts[0].trim();
            boolean isDone = parts.length > 1 && parts[1].trim().equals("1");

            Task task;
            switch (type) {
                case "T" -> {
                    if (parts.length < 3) {
                        return null;
                    }
                    task = new Todo(parts[2]);
                }
                case "D" -> {
                    if (parts.length < 4) {
                        return null;
                    }
                    task = new Deadline(parts[2], parts[3]);
                }
                case "E" -> {
                    if (parts.length < 5) {
                        return null;
                    }
                    task = new Event(parts[2], parts[3], parts[4]);
                }
                default -> {
                    return null;
                }
            }

            if (isDone) {
                task.markDone();
            }
            return task;
        } catch (Exception e) {
            System.err.println("Skipping corrupted task line: " + trimmed);
            return null;
        }
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

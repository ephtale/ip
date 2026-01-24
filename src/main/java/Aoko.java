import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Aoko {
    private static final String LINE = "____________________________________________________________";
    private static final Path SAVE_PATH = Paths.get("data", "aoko.txt");
    private static final DateTimeFormatter DISPLAY_DATE_ONLY = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private enum Command {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON, BYE, UNKNOWN;

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
                case "on" -> ON;
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
        private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
        private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

        private final LocalDateTime by;
        private final boolean hasTime;

        Deadline(String description, LocalDateTime by, boolean hasTime) {
            super(description);
            this.by = by;
            this.hasTime = hasTime;
        }

        @Override
        protected String typeIcon() {
            return "[D]";
        }

        @Override
        protected String taskDetails() {
            String formatted = hasTime ? by.format(DISPLAY_DATE_TIME) : by.toLocalDate().format(DISPLAY_DATE);
            return description + " (by: " + formatted + ")";
        }
    }

    private static class Event extends Task {
        private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
        private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

        private final LocalDateTime from;
        private final LocalDateTime to;
        private final boolean fromHasTime;
        private final boolean toHasTime;

        Event(String description, LocalDateTime from, boolean fromHasTime, LocalDateTime to, boolean toHasTime) {
            super(description);
            this.from = from;
            this.to = to;
            this.fromHasTime = fromHasTime;
            this.toHasTime = toHasTime;
        }

        @Override
        protected String typeIcon() {
            return "[E]";
        }

        @Override
        protected String taskDetails() {
            String formattedFrom = fromHasTime ? from.format(DISPLAY_DATE_TIME) : from.toLocalDate().format(DISPLAY_DATE);

            String formattedTo;
            if (toHasTime && fromHasTime && from.toLocalDate().equals(to.toLocalDate())) {
                formattedTo = to.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH));
            } else {
                formattedTo = toHasTime ? to.format(DISPLAY_DATE_TIME) : to.toLocalDate().format(DISPLAY_DATE);
            }

            return description + " (from: " + formattedFrom + " to: " + formattedTo + ")";
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
                case ON -> {
                    if (remainder.isEmpty()) {
                        System.out.println(LINE);
                        System.out.println("Please provide a date (e.g., \"on 2019-10-15\" or \"on 2/12/2019\").");
                        System.out.println(LINE);
                        break;
                    }

                    LocalDate date = parseDateOnly(remainder);
                    if (date == null) {
                        System.out.println(LINE);
                        System.out.println("I couldn't understand that date.");
                        System.out.println("Try: yyyy-MM-dd (e.g., 2019-10-15) or d/M/yyyy (e.g., 2/12/2019)");
                        System.out.println(LINE);
                        break;
                    }

                    List<Task> matches = new ArrayList<>();
                    for (Task task : tasks) {
                        if (task instanceof Deadline deadline) {
                            if (deadline.by.toLocalDate().equals(date)) {
                                matches.add(task);
                            }
                            continue;
                        }
                        if (task instanceof Event event) {
                            LocalDate fromDate = event.from.toLocalDate();
                            LocalDate toDate = event.to.toLocalDate();
                            if ((date.isEqual(fromDate) || date.isAfter(fromDate))
                                    && (date.isEqual(toDate) || date.isBefore(toDate))) {
                                matches.add(task);
                            }
                        }
                    }

                    System.out.println(LINE);
                    if (matches.isEmpty()) {
                        System.out.println("No tasks found on " + date.format(DISPLAY_DATE_ONLY) + ".");
                        System.out.println(LINE);
                        break;
                    }

                    System.out.println("Here are the tasks on " + date.format(DISPLAY_DATE_ONLY) + ":");
                    for (int i = 0; i < matches.size(); i++) {
                        System.out.println((i + 1) + "." + matches.get(i).display());
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

                        ParsedDateTime parsed = parseDateTime(by);
                        if (parsed == null) {
                            System.out.println(LINE);
                            System.out.println("I couldn't understand that date/time.");
                            System.out.println("Try: yyyy-MM-dd (e.g., 2019-10-15) or d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
                            System.out.println(LINE);
                            break;
                        }

                        Task task = new Deadline(description, parsed.dateTime, parsed.hasTime);
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

                    ParsedDateTime fromParsed = parseDateTime(from);
                    if (fromParsed == null) {
                        System.out.println(LINE);
                        System.out.println("I couldn't understand the event start date/time.");
                        System.out.println("Try: yyyy-MM-dd (e.g., 2019-10-15) or d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
                        System.out.println(LINE);
                        break;
                    }

                    ParsedDateTime toParsed = parseEventEnd(fromParsed, to);
                    if (toParsed == null) {
                        System.out.println(LINE);
                        System.out.println("I couldn't understand the event end date/time.");
                        System.out.println("Try: yyyy-MM-dd (e.g., 2019-10-15), d/M/yyyy HHmm (e.g., 2/12/2019 1800), or time-only HHmm/HH:mm (e.g., 1600)");
                        System.out.println(LINE);
                        break;
                    }

                    if (toParsed.dateTime.isBefore(fromParsed.dateTime)) {
                        System.out.println(LINE);
                        System.out.println("The event end must not be before the start.");
                        System.out.println(LINE);
                        break;
                    }

                    Task task = new Event(description, fromParsed.dateTime, fromParsed.hasTime, toParsed.dateTime, toParsed.hasTime);
                    tasks.add(task);
                    saveTasksSafely(SAVE_PATH, tasks);
                    printTaskAdded(task, tasks.size());
                }
                case BYE -> exit = true;
                case UNKNOWN -> {
                    System.out.println(LINE);
                    System.out.println("That's not a command I recognize.");
                    System.out.println("Available commands: list, mark, unmark, delete, todo, deadline, event, on, bye");
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
            String encodedBy = deadline.hasTime
                    ? deadline.by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    : deadline.by.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            return "D | " + doneFlag + " | " + deadline.description + " | " + encodedBy;
        }
        if (task instanceof Event event) {
            String encodedFrom = event.fromHasTime
                ? event.from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : event.from.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String encodedTo = event.toHasTime
                ? event.to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : event.to.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            return "E | " + doneFlag + " | " + event.description + " | " + encodedFrom + " | " + encodedTo;
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
                    String byRaw = parts[3].trim();
                    ParsedDateTime parsed;
                    if (byRaw.contains("T")) {
                        try {
                            parsed = new ParsedDateTime(LocalDateTime.parse(byRaw, DateTimeFormatter.ISO_LOCAL_DATE_TIME), true);
                        } catch (DateTimeParseException e) {
                            return null;
                        }
                    } else {
                        try {
                            LocalDate date = LocalDate.parse(byRaw, DateTimeFormatter.ISO_LOCAL_DATE);
                            parsed = new ParsedDateTime(date.atStartOfDay(), false);
                        } catch (DateTimeParseException e) {
                            return null;
                        }
                    }

                    task = new Deadline(parts[2], parsed.dateTime, parsed.hasTime);
                }
                case "E" -> {
                    if (parts.length < 5) {
                        return null;
                    }
                    ParsedDateTime fromParsed;
                    ParsedDateTime toParsed;

                    String fromRaw = parts[3].trim();
                    String toRaw = parts[4].trim();

                    fromParsed = parseIsoDateOrDateTime(fromRaw);
                    toParsed = parseIsoDateOrDateTime(toRaw);

                    if (fromParsed == null || toParsed == null) {
                        return null;
                    }

                    task = new Event(parts[2], fromParsed.dateTime, fromParsed.hasTime, toParsed.dateTime, toParsed.hasTime);
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

    private static class ParsedDateTime {
        private final LocalDateTime dateTime;
        private final boolean hasTime;

        private ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }
    }

    private static ParsedDateTime parseDateTime(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) {
            return null;
        }

        // Minimal: yyyy-MM-dd
        try {
            LocalDate date = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            return new ParsedDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        // Accept: yyyy-MM-dd HHmm or yyyy-MM-dd HH:mm
        try {
            DateTimeFormatter f1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
            return new ParsedDateTime(LocalDateTime.parse(s, f1), true);
        } catch (DateTimeParseException ignored) {
            // fall through
        }
        try {
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new ParsedDateTime(LocalDateTime.parse(s, f2), true);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        // Stretch-friendly: d/M/yyyy HHmm (e.g., 2/12/2019 1800)
        try {
            DateTimeFormatter f3 = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
            return new ParsedDateTime(LocalDateTime.parse(s, f3), true);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        // Stretch-friendly: d/M/yyyy (date-only)
        try {
            DateTimeFormatter f4 = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate date = LocalDate.parse(s, f4);
            return new ParsedDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        return null;
    }

    private static ParsedDateTime parseIsoDateOrDateTime(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) {
            return null;
        }

        if (s.contains("T")) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME), true);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        try {
            LocalDate date = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            return new ParsedDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static ParsedDateTime parseEventEnd(ParsedDateTime fromParsed, String rawTo) {
        ParsedDateTime full = parseDateTime(rawTo);
        if (full != null) {
            return full;
        }

        // Time-only end (same date as start): HHmm or HH:mm
        String s = rawTo == null ? "" : rawTo.trim();
        if (s.isEmpty()) {
            return null;
        }

        LocalTime time;
        try {
            DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HHmm");
            time = LocalTime.parse(s, hhmm);
        } catch (DateTimeParseException ignored) {
            try {
                DateTimeFormatter hhColon = DateTimeFormatter.ofPattern("H:mm");
                time = LocalTime.parse(s, hhColon);
            } catch (DateTimeParseException ignored2) {
                return null;
            }
        }

        LocalDate date = fromParsed.dateTime.toLocalDate();
        return new ParsedDateTime(LocalDateTime.of(date, time), true);
    }

    private static LocalDate parseDateOnly(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(s, f);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        return null;
    }
}

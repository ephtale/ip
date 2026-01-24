package aoko.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import aoko.parser.Parser;
import aoko.task.Deadline;
import aoko.task.Event;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.task.Todo;

public class Storage {
    private final Path path;

    public Storage(Path path) {
        this.path = path;
    }

    public List<Task> load() {
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

    public void save(TaskList taskList) {
        try {
            saveInternal(taskList.asUnmodifiableList());
        } catch (IOException e) {
            System.err.println("Failed to save tasks to disk: " + e.getMessage());
        }
    }

    private void saveInternal(List<Task> tasks) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = tasks.stream()
                .map(Storage::encodeTask)
                .collect(Collectors.toList());

        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private static String encodeTask(Task task) {
        String doneFlag = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + doneFlag + " | " + task.getDescription();
        }
        if (task instanceof Deadline deadline) {
            String encodedBy = deadline.hasTime()
                    ? deadline.getBy().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    : deadline.getBy().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            return "D | " + doneFlag + " | " + deadline.getDescription() + " | " + encodedBy;
        }
        if (task instanceof Event event) {
            String encodedFrom = event.fromHasTime()
                    ? event.getFrom().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    : event.getFrom().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String encodedTo = event.toHasTime()
                    ? event.getTo().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    : event.getTo().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            return "E | " + doneFlag + " | " + event.getDescription() + " | " + encodedFrom + " | " + encodedTo;
        }

        return "T | " + doneFlag + " | " + task.getDescription();
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
                    Parser.ParsedDateTime parsed = parseLegacyOrIso(parts[3].trim());
                    if (parsed == null) {
                        return null;
                    }
                    task = new Deadline(parts[2], parsed.dateTime, parsed.hasTime);
                }
                case "E" -> {
                    if (parts.length < 5) {
                        return null;
                    }
                    Parser.ParsedDateTime fromParsed = Parser.parseIsoDateOrDateTime(parts[3].trim());
                    Parser.ParsedDateTime toParsed = Parser.parseIsoDateOrDateTime(parts[4].trim());
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

    private static Parser.ParsedDateTime parseLegacyOrIso(String byRaw) {
        // Supports ISO date or ISO date-time (current format)
        if (byRaw.contains("T")) {
            try {
                return new Parser.ParsedDateTime(LocalDateTime.parse(byRaw, DateTimeFormatter.ISO_LOCAL_DATE_TIME), true);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        try {
            LocalDate date = LocalDate.parse(byRaw, DateTimeFormatter.ISO_LOCAL_DATE);
            return new Parser.ParsedDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

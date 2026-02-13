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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import aoko.parser.Parser;
import aoko.task.Deadline;
import aoko.task.Event;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.task.Todo;

/**
 * Loads and saves tasks to disk using a simple line-based text format.
 */
public class Storage {
    private final Path path;

    /**
     * Creates storage backed by a given file path.
     */
    public Storage(Path path) {
        assert path != null : "Storage path must not be null";
        this.path = path;
    }

    /**
     * Loads tasks from disk.
     *
     * <p>If the file does not exist or cannot be read, returns an empty list.
     */
    public List<Task> load() {
        assert path != null : "Storage path must not be null";
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            assert lines != null : "Files.readAllLines must not return null";
            List<Task> tasks = new ArrayList<>();
            Set<String> seen = new HashSet<>();
            for (String line : lines) {
                assert line != null : "Lines read from file must not be null";
                Task task = decodeTask(line);
                if (task != null) {
                    String key = task.detailsKey();
                    if (seen.add(key)) {
                        tasks.add(task);
                    } else {
                        System.err.println("Skipping duplicate task line: " + line);
                    }
                }
            }
            return tasks;
        } catch (IOException e) {
            System.err.println("Failed to load tasks from disk: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves the given task list to disk.
     */
    public void save(TaskList taskList) {
        assert taskList != null : "TaskList to save must not be null";
        try {
            saveInternal(taskList.asUnmodifiableList());
        } catch (IOException e) {
            System.err.println("Failed to save tasks to disk: " + e.getMessage());
        }
    }

    /**
     * Returns an in-memory snapshot of the given task list in the same encoded format used on disk.
     *
     * <p>This is intended for features like undo/redo, where a stable representation is preferred
     * over object identity.
     *
     * @param taskList Task list to snapshot.
     * @return Encoded task lines.
     */
    public List<String> snapshot(TaskList taskList) {
        assert taskList != null : "taskList must not be null";
        return taskList.asUnmodifiableList().stream()
                .map(Storage::encodeTask)
                .collect(Collectors.toList());
    }

    /**
     * Restores the given task list from a previously created snapshot and persists it to disk.
     *
     * <p>Corrupted lines are skipped using the same rules as {@link #load()}.
     *
     * @param taskList Task list instance to mutate.
     * @param snapshotLines Encoded task lines.
     */
    public void restore(TaskList taskList, List<String> snapshotLines) {
        assert taskList != null : "taskList must not be null";
        assert snapshotLines != null : "snapshotLines must not be null";

        List<Task> decoded = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String line : snapshotLines) {
            Task task = decodeTask(line);
            if (task != null) {
                String key = task.detailsKey();
                if (seen.add(key)) {
                    decoded.add(task);
                }
            }
        }

        taskList.replaceWith(decoded);
        save(taskList);
    }

    /**
     * Writes tasks to disk, creating parent directories if needed.
     */
    private void saveInternal(List<Task> tasks) throws IOException {
        assert tasks != null : "Task list to save must not be null";
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = tasks.stream()
                .map(Storage::encodeTask)
                .collect(Collectors.toList());

        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * Encodes a task into its persisted line representation.
     */
    private static String encodeTask(Task task) {
        assert task != null : "Cannot encode a null task";
        assert task.getDescription() != null : "Task description must not be null";
        assert !task.getDescription().trim().isEmpty() : "Task description must not be blank";
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

    /**
     * Decodes a persisted line into a task.
     *
     * <p>Returns {@code null} for corrupted/unsupported lines.
     */
    private static Task decodeTask(String line) {
        if (line == null) {
            return null;
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] parts = trimmed.split("\\s*\\|\\s*");
        assert parts != null : "Split parts must not be null";
        try {
            String type = parts[0].trim();
            boolean isDone = parts.length > 1 && parts[1].trim().equals("1");

            Task task;
            switch (type) {
            case "T" -> {
                if (parts.length < 3) {
                    return null;
                }
                String desc = parts[2] == null ? "" : parts[2].trim();
                if (desc.isEmpty()) {
                    return null;
                }
                task = new Todo(desc);
            }
            case "D" -> {
                if (parts.length < 4) {
                    return null;
                }
                String desc = parts[2] == null ? "" : parts[2].trim();
                if (desc.isEmpty()) {
                    return null;
                }
                Parser.ParsedDateTime parsed = parseLegacyOrIso(parts[3].trim());
                if (parsed == null) {
                    return null;
                }
                assert parsed.dateTime != null : "Parsed deadline date/time must not be null";
                task = new Deadline(desc, parsed.dateTime, parsed.hasTime);
            }
            case "E" -> {
                if (parts.length < 5) {
                    return null;
                }
                String desc = parts[2] == null ? "" : parts[2].trim();
                if (desc.isEmpty()) {
                    return null;
                }
                Parser.ParsedDateTime fromParsed = Parser.parseIsoDateOrDateTime(parts[3].trim());
                Parser.ParsedDateTime toParsed = Parser.parseIsoDateOrDateTime(parts[4].trim());
                if (fromParsed == null || toParsed == null) {
                    return null;
                }
                assert fromParsed.dateTime != null : "Parsed event start date/time must not be null";
                assert toParsed.dateTime != null : "Parsed event end date/time must not be null";
                task = new Event(
                        desc,
                        fromParsed.dateTime,
                        fromParsed.hasTime,
                        toParsed.dateTime,
                        toParsed.hasTime);
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

    /**
     * Parses legacy (date-only) or ISO date-time strings used for deadlines in storage.
     */
    private static Parser.ParsedDateTime parseLegacyOrIso(String byRaw) {
        assert byRaw != null : "Raw deadline string must not be null";
        // Supports ISO date or ISO date-time (current format)
        if (byRaw.contains("T")) {
            try {
                return new Parser.ParsedDateTime(
                        LocalDateTime.parse(byRaw, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        true);
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

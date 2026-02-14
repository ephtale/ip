package aoko.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aoko.task.Deadline;
import aoko.task.Event;
import aoko.task.Task;
import aoko.task.TaskList;
import aoko.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    void load_missingFile_returnsEmptyList() {
        Path saveFile = tempDir.resolve("aoko.txt");
        Storage storage = new Storage(saveFile);

        List<Task> loaded = storage.load();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    void saveThenLoad_roundTripsTasksAndDoneFlags() {
        Path saveFile = tempDir.resolve("aoko.txt");
        Storage storage = new Storage(saveFile);

        TaskList taskList = new TaskList();

        Todo todo = new Todo("read book");
        todo.markDone();

        Deadline deadlineDateOnly = new Deadline("return book",
                LocalDateTime.of(2019, 6, 6, 0, 0), false);

        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0), true,
                LocalDateTime.of(2019, 8, 6, 16, 0), true);
        event.markDone();

        taskList.add(todo);
        taskList.add(deadlineDateOnly);
        taskList.add(event);

        storage.save(taskList);

        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());

        assertTodo(loaded.get(0), "read book", true);
        assertDeadline(loaded.get(1), "return book", LocalDateTime.of(2019, 6, 6, 0, 0), false, false);
        assertEvent(loaded.get(2), "project meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0), true,
                LocalDateTime.of(2019, 8, 6, 16, 0), true,
                true);
    }

    private static void assertTodo(Task task, String expectedDescription, boolean expectedDone) {
        assertTrue(task instanceof Todo);
        assertEquals(expectedDescription, task.getDescription());
        assertEquals(expectedDone, task.isDone());
    }

    private static void assertDeadline(Task task, String expectedDescription, LocalDateTime expectedBy,
            boolean expectedHasTime, boolean expectedDone) {
        assertTrue(task instanceof Deadline);
        Deadline d = (Deadline) task;
        assertEquals(expectedDescription, d.getDescription());
        assertEquals(expectedHasTime, d.hasTime());
        assertEquals(expectedBy, d.getBy());
        assertEquals(expectedDone, d.isDone());
    }

    private static void assertEvent(Task task, String expectedDescription,
            LocalDateTime expectedFrom, boolean expectedFromHasTime,
            LocalDateTime expectedTo, boolean expectedToHasTime,
            boolean expectedDone) {
        assertTrue(task instanceof Event);
        Event e = (Event) task;
        assertEquals(expectedDescription, e.getDescription());
        assertEquals(expectedFromHasTime, e.hasFromTime());
        assertEquals(expectedToHasTime, e.hasToTime());
        assertEquals(expectedFrom, e.getFrom());
        assertEquals(expectedTo, e.getTo());
        assertEquals(expectedDone, e.isDone());
    }

    @Test
    void load_skipsCorruptedLines() throws IOException {
        Path saveFile = tempDir.resolve("aoko.txt");

        Files.write(saveFile, List.of(
                "T | 1 | ok todo",
                "THIS IS CORRUPTED",
                "D | 0 | bad deadline missing date",
                "D | 0 | ok deadline | 2019-06-06",
                "E | 0 | ok event | 2019-08-06T14:00 | 2019-08-06T16:00"
        ), StandardCharsets.UTF_8);

        Storage storage = new Storage(saveFile);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("ok todo", loaded.get(0).getDescription());
        assertEquals("ok deadline", loaded.get(1).getDescription());
        assertEquals("ok event", loaded.get(2).getDescription());
    }

    @Test
    void load_skipsDuplicateTasks() throws IOException {
        Path saveFile = tempDir.resolve("aoko.txt");

        Files.write(saveFile, List.of(
                "T | 0 | read book",
                "T | 1 | read book", // duplicate details, different done flag
                "D | 0 | return book | 2019-06-06",
                "D | 1 | return book | 2019-06-06", // duplicate deadline
                "E | 0 | meeting | 2019-08-06T14:00 | 2019-08-06T16:00",
                "E | 0 | meeting | 2019-08-06T14:00 | 2019-08-06T16:00" // exact duplicate
        ), StandardCharsets.UTF_8);

        Storage storage = new Storage(saveFile);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("return book", loaded.get(1).getDescription());
        assertEquals("meeting", loaded.get(2).getDescription());
    }
}

package aoko.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

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

        Task loadedTodo = loaded.get(0);
        assertTrue(loadedTodo instanceof Todo);
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Task loadedDeadline = loaded.get(1);
        assertTrue(loadedDeadline instanceof Deadline);
        Deadline d = (Deadline) loadedDeadline;
        assertEquals("return book", d.getDescription());
        assertFalse(d.hasTime());
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0), d.getBy());
        assertFalse(d.isDone());

        Task loadedEvent = loaded.get(2);
        assertTrue(loadedEvent instanceof Event);
        Event e = (Event) loadedEvent;
        assertEquals("project meeting", e.getDescription());
        assertTrue(e.fromHasTime());
        assertTrue(e.toHasTime());
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 0), e.getFrom());
        assertEquals(LocalDateTime.of(2019, 8, 6, 16, 0), e.getTo());
        assertTrue(e.isDone());
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
}

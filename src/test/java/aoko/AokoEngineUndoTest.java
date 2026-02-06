package aoko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AokoEngineUndoTest {

    @TempDir
    Path tempDir;

    @Test
    void undo_withNoHistory_showsNothingToUndo() {
        Path saveFile = tempDir.resolve("aoko.txt");
        AokoEngine engine = new AokoEngine(saveFile);

        AokoEngine.EngineResponse resp = engine.processToString("undo");
        assertTrue(resp.output.contains("Nothing to undo"));
    }

    @Test
    void undo_afterAdd_restoresPreviousState() throws Exception {
        Path saveFile = tempDir.resolve("aoko.txt");
        AokoEngine engine = new AokoEngine(saveFile);

        engine.processToString("todo read book");
        List<String> afterAdd = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals(1, afterAdd.size());

        engine.processToString("undo");
        List<String> afterUndo = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals(0, afterUndo.size());
    }

    @Test
    void undo_afterMark_revertsDoneFlag() throws Exception {
        Path saveFile = tempDir.resolve("aoko.txt");
        AokoEngine engine = new AokoEngine(saveFile);

        engine.processToString("todo read book");
        engine.processToString("mark 1");

        List<String> marked = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals(1, marked.size());
        assertTrue(marked.get(0).startsWith("T | 1 |"));

        engine.processToString("undo");
        List<String> afterUndo = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals(1, afterUndo.size());
        assertTrue(afterUndo.get(0).startsWith("T | 0 |"));
    }

    @Test
    void undo_afterNonMutatingCommand_doesNotUndoAnything() {
        Path saveFile = tempDir.resolve("aoko.txt");
        AokoEngine engine = new AokoEngine(saveFile);

        engine.processToString("list");
        AokoEngine.EngineResponse resp = engine.processToString("undo");
        assertTrue(resp.output.contains("Nothing to undo"));
    }
}

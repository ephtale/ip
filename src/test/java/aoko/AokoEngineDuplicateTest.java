package aoko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AokoEngineDuplicateTest {

    @TempDir
    Path tempDir;

    @Test
    void add_duplicateTask_isRejectedAndNotSaved() throws Exception {
        Path saveFile = tempDir.resolve("aoko.txt");
        AokoEngine engine = new AokoEngine(saveFile);

        engine.processToString("todo read book");
        AokoEngine.EngineResponse resp = engine.processToString("todo read book");
        assertTrue(resp.output.contains("already"));

        List<String> saved = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        assertEquals(1, saved.size());
    }
}

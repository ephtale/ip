package aoko.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;

/**
 * Launcher workaround for JavaFX classpath/module-path quirks.
 */
public class Launcher {
    public static void main(String[] args) {
        // When JavaFX is loaded from a shaded fat JAR (unnamed module on the classpath),
        // it logs a WARNING about an "unsupported" configuration. This is expected and
        // does not affect functionality, so we silence that specific logger to avoid
        // confusing end users.
        Logger.getLogger("com.sun.javafx.application.PlatformImpl").setLevel(Level.SEVERE);
        Application.launch(AokoGuiApp.class, args);
    }
}

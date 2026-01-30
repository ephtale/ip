package aoko.gui;

import javafx.application.Application;

/**
 * Launcher workaround for JavaFX classpath/module-path quirks.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(AokoGuiApp.class, args);
    }
}

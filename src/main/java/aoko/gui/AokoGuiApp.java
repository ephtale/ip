package aoko.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import aoko.AokoEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX GUI for Aoko using FXML.
 */
public class AokoGuiApp extends Application {
    private AokoEngine engine;

    private static Path resolveSavePath(List<String> rawArgs) {
        assert rawArgs == null || !rawArgs.contains(null) : "Raw args list should not contain null entries";
        if (rawArgs != null && !rawArgs.isEmpty()) {
            String first = rawArgs.get(0);
            if (first != null && !first.trim().isEmpty()) {
                return Paths.get(first.trim());
            }
        }
        return Paths.get("data", "aoko.txt");
    }

    @Override
    public void start(Stage stage) {
        assert stage != null : "Primary stage must not be null";
        try {
            Path savePath = resolveSavePath(getParameters().getRaw());
            assert savePath != null : "Resolved save path must not be null";
            engine = new AokoEngine(savePath);
            assert engine != null : "Engine must be constructed";

            var mainWindowResource = AokoGuiApp.class.getResource("/view/MainWindow.fxml");
            assert mainWindowResource != null : "Missing /view/MainWindow.fxml resource";
            FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
            AnchorPane ap = fxmlLoader.load();
            assert ap != null : "Main window root must not be null";

            MainWindow controller = fxmlLoader.getController();
            assert controller != null : "MainWindow controller must not be null";
            controller.setEngine(engine);
            controller.showBotMessage(engine.welcomeToString());

            Scene scene = new Scene(ap);
            stage.setTitle("Aoko");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load GUI layout", e);
        }
    }
}

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
        try {
            Path savePath = resolveSavePath(getParameters().getRaw());
            engine = new AokoEngine(savePath);

            FXMLLoader fxmlLoader = new FXMLLoader(AokoGuiApp.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();

            MainWindow controller = fxmlLoader.getController();
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

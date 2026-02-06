package aoko.gui;

import aoko.AokoEngine;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private AokoEngine engine;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.jpg"));
    private final Image aokoImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.jpg"));

    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML scrollPane must be injected";
        assert dialogContainer != null : "FXML dialogContainer must be injected";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the engine instance */
    public void setEngine(AokoEngine engine) {
        assert engine != null : "Engine must not be null";
        this.engine = engine;
    }

    public void showBotMessage(String text) {
        assert dialogContainer != null : "Dialog container must be initialized";
        assert text != null : "Bot message text must not be null";
        dialogContainer.getChildren().add(DialogBox.getAokoDialog(text, aokoImage));
    }

    @FXML
    private void handleUserInput() {
        if (engine == null) {
            assert false : "Engine should be set before handling user input";
            return;
        }

        assert userInput != null : "FXML userInput must be injected";
        assert dialogContainer != null : "FXML dialogContainer must be injected";
        assert sendButton != null : "FXML sendButton must be injected";
        assert userImage != null : "User image must be loaded";
        assert aokoImage != null : "Bot image must be loaded";

        String input = userInput.getText();
        if (input == null) {
            input = "";
        }
        input = input.trim();
        if (input.isEmpty()) {
            return;
        }

        AokoEngine.EngineResponse response = engine.processToString(input);
        assert response != null : "Engine response must not be null";
        assert response.output != null : "Engine response output must not be null";
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAokoDialog(response.output, aokoImage)
        );
        userInput.clear();

        if (response.shouldExit) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            Platform.exit();
        }
    }
}

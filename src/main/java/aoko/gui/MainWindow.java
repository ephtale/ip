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
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the engine instance */
    public void setEngine(AokoEngine engine) {
        this.engine = engine;
    }

    public void showBotMessage(String text) {
        dialogContainer.getChildren().add(DialogBox.getAokoDialog(text, aokoImage));
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleUserInput() {
        if (engine == null) {
            return;
        }

        String input = userInput.getText();
        if (input == null) {
            input = "";
        }
        input = input.trim();
        if (input.isEmpty()) {
            return;
        }

        AokoEngine.EngineResponse response = engine.processToString(input);
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

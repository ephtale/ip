package aoko.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private Label senderName;

    @FXML
    private VBox messageContainer;

    @FXML
    private VBox messageBubble;

    @FXML
    private Separator topDivider;

    @FXML
    private Separator bottomDivider;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String name, String text, Image img) {
        assert name != null : "Dialog speaker name must not be null";
        assert text != null : "Dialog text must not be null";
        assert img != null : "Dialog image must not be null";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load dialog box layout", e);
        }

        assert dialog != null : "FXML dialog label must be injected";
        assert senderName != null : "FXML sender name label must be injected";
        assert displayPicture != null : "FXML image view must be injected";
        setNameTextAndImage(name, text, img);
    }

    private void setNameTextAndImage(String name, String text, Image img) {
        senderName.setText(name);
        setDialogTextWithDividers(text);
        displayPicture.setImage(img);

        senderName.getStyleClass().add("sender-name");
        dialog.getStyleClass().add("dialog-text");
        messageBubble.getStyleClass().add("dialog-bubble");
        topDivider.getStyleClass().add("dialog-divider");
        bottomDivider.getStyleClass().add("dialog-divider");

        senderName.setMaxWidth(Double.MAX_VALUE);
        senderName.setAlignment(Pos.CENTER_RIGHT);
        messageContainer.setAlignment(Pos.TOP_RIGHT);

        dialog.setMaxWidth(Double.MAX_VALUE);
        dialog.setAlignment(Pos.CENTER_RIGHT);
        dialog.setTextAlignment(TextAlignment.RIGHT);
        makeAvatarCircularAndSmall();
    }

    private void setDialogTextWithDividers(String rawText) {
        assert rawText != null : "Dialog text must not be null";

        String[] lines = rawText.split("\\R", -1);
        boolean hasDividerLines = false;
        StringBuilder cleaned = new StringBuilder();
        for (String line : lines) {
            assert line != null : "Message line must not be null";
            String trimmed = line.trim();
            if (trimmed.length() >= 5 && trimmed.chars().allMatch(ch -> ch == '_')) {
                hasDividerLines = true;
                continue;
            }
            cleaned.append(line).append("\n");
        }

        String cleanedText = cleaned.toString();
        cleanedText = cleanedText.replaceAll("^\\s+", "");
        cleanedText = cleanedText.replaceAll("\\s+$", "");
        dialog.setText(cleanedText);

        setDividerVisible(topDivider, hasDividerLines);
        setDividerVisible(bottomDivider, hasDividerLines);
    }

    private void setDividerVisible(Separator divider, boolean isVisible) {
        assert divider != null : "Divider must not be null";
        divider.setVisible(isVisible);
        divider.setManaged(isVisible);
    }

    private void makeAvatarCircularAndSmall() {
        double avatarSize = 36;
        displayPicture.setFitWidth(avatarSize);
        displayPicture.setFitHeight(avatarSize);
        displayPicture.setPreserveRatio(true);
        displayPicture.setSmooth(true);

        double radius = avatarSize / 2.0;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    private static DialogBox create(String name, String text, Image img, boolean shouldFlip) {
        DialogBox dialogBox = new DialogBox(name, text, img);
        if (shouldFlip) {
            dialogBox.flip();
        }
        return dialogBox;
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        assert dialog != null : "Dialog label must be initialized";
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        assert tmp != null : "Children list must not be null";
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);

        assert messageContainer != null : "Message container must be initialized";
        assert messageBubble != null : "Message bubble must be initialized";
        assert senderName != null : "Sender name must be initialized";

        messageContainer.setAlignment(Pos.TOP_LEFT);
        senderName.setAlignment(Pos.CENTER_LEFT);
        messageBubble.getStyleClass().add("reply-label");

        dialog.setAlignment(Pos.CENTER_LEFT);
        dialog.setTextAlignment(TextAlignment.LEFT);
        dialog.getStyleClass().add("reply-text");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return create("Alice", text, img, false);
    }

    public static DialogBox getAokoDialog(String text, Image img) {
        return create("Aoko", text, img, true);
    }
}

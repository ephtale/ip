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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
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
        assert displayPicture != null : "FXML image view must be injected";
        setTextAndImage(text, img);
    }

    private void setTextAndImage(String text, Image img) {
        dialog.setText(text);
        displayPicture.setImage(img);
    }

    private static DialogBox create(String text, Image img, boolean flip) {
        DialogBox dialogBox = new DialogBox(text, img);
        if (flip) {
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
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return create(text, img, false);
    }

    public static DialogBox getAokoDialog(String text, Image img) {
        return create(text, img, true);
    }
}

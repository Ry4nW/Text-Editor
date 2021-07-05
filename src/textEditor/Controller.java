package textEditor;

import IO.IO;
import IO.IOResult;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.InlineCssTextArea;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    public MenuItem githubMI;
    public MenuItem iconMI;
    public MenuItem aboutMI;

    private final InlineCssTextArea textArea = new InlineCssTextArea();

    public Label fileMessage;
    public Label errorMessage;

    public Button underlineButton;
    public Button italicsButton;
    public Button boldButton;
    public Button removeStylingBtn;
    public Button clearAllBtn;
    public Button undoBtn;

    public IO model = new IO(); // Class for our IO system.
    public BorderPane borderPane;
    private TextFile currentTextFile;
    private ArrayList<String> autoSaveText = new ArrayList<>(); // This ArrayList will auto save text from {textArea}
    // when the client decides to execute {clearTextArea}.

    // Requires: Nothing.
    // Modifies: Letter.
    // Effects: Sets wrap text for our editor's text area, aka creates a newline for the user to type in
    // once the end of the textEditor's width is reached, border, border style/width, button disabling to
    // prevent errors and other features on initialization.
    public void initialize() {

        removeStylingBtn.setDisable(true);
        undoBtn.setDisable(true);
        fileMessage.setText("");

        borderPane.centerProperty().setValue(textArea); // Adds our text area to the center of the borderPane.

        textArea.setWrapText(true);
        textArea.setStyle("-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 2px; -fx-font-family: Arial; -fx-font-size: 12px");

    }


    // Requires: Nothing.
    // Modifies: textArea.
    // Effects: Saves all text from {textEditor} into a file through save() in the {IO} class. If a NullPointerException
    // occurs (aka the user did not load an existing .txt file to edit), it will allow the user to create a new one.
    // Prints StackTrace if all else fails.
    public void onSave() {

        try {

            TextFile textFile = new TextFile(currentTextFile.getFile(), Arrays.asList(textArea.getText().split("\n")));
            model.save(textFile);
            fileMessage.setText(currentTextFile.getFile() + " was successfully saved.");

        } catch (NullPointerException bruh) {

            try {

                final String content = textArea.getText();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Text as .txt");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Document (*.txt)", ".txt");
                fileChooser.getExtensionFilters().add(extFilter);

                File file = fileChooser.showSaveDialog(null);

                try (final BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
                    writer.write(content);
                    writer.flush();
                }

                fileMessage.setText("Text successfully saved as " + file.getName() + ". Please open that file to continue working on it.");

            } catch (Exception bruh2) {
                fileMessage.setText("File save failed.");
                bruh2.printStackTrace();
            }

        }

    }


    // Requires: Nothing.
    // Modifies: textArea.
    // Effects: Loads all text from an existing .txt file through load() in the {IO} class. Appends all text from
    // the file to {textArea}, allowing the user to edit and update the same file.
    public void onLoad() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {

            Path path = file.toPath();
            IOResult<TextFile> io = model.load(Paths.get(path.toString()));

            if (io.checked() && io.hasData()) {

                currentTextFile = io.getData();
                textArea.clear();

                StringBuilder text = new StringBuilder();

                for (int i = 0; i < currentTextFile.getContent().size(); i++) {
                    text.append(currentTextFile.getContent().get(i)).append("\n");
                }

                textArea.appendText(text.toString());
                fileMessage.setText("Text successfully loaded from " + currentTextFile.getFile());

            } else {
                fileMessage.setText("File load failed. Please make sure that you're loading from a .txt file.");
            }
        }
    }


    // Requires: Nothing.
    // Modifies: Nothing.
    // Effects: Terminates the process.
    public void onClose() {
        System.exit(0);
    }


    // Requires: Nothing.
    // Modifies: Clients browser.
    // Effects: Opens a link on the client's default browser to the Github page of the application.
    public void onInfoGithub() {

        try {
            Desktop.getDesktop().browse(new URL("https://github.com/Ry4nW/Text-Editor").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }


    // Requires: Nothing.
    // Modifies: Client's browser.
    // Effects: Opens a link on the client's default browser to the application's Icon "provider".
    public void onInfoIcon() {

        try {
            Desktop.getDesktop().browse(new URL("https://www.flaticon.com/free-icon/text-editor_196308").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }


    // Requires: Nothing.
    // Modifies: Client's browser.
    // Effects: Inserts a popup serving as an "About Page" when clicked.
    public void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("About");
        alert.setContentText("Letter. \nSave, load and write your compositions with ease.");
        alert.show();
    }


    // Requires: Highlighted text the client wants to bold.
    // Modifies: Highlighted text in textArea.
    // Effects: Bolds any highlighted text when executed.
    public void boldText() {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-weight: bold");
        removeStylingBtn.setDisable(false);
    }


    // Requires: Highlighted text client wants to italicize.
    // Modifies: Highlighted text in textArea.
    // Effects: Bolds any highlighted text when executed.
    public void italicizeText() {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-style: italic");
        removeStylingBtn.setDisable(false);
    }


    // Requires: Highlighted text client wants to underline.
    // Modifies: Highlights text in textArea.
    // Effects: Bolds any highlighted text when executed.
    public void underlineText() {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-underline: true");
        removeStylingBtn.setDisable(false);
    }

    // Requires: Highlighted text client wants to remove all styling.
    // Modifies: Highlighted text in textArea.
    // Effects: Bolds any highlighted text when executed.
    public void removeStyling() {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-weight: initial");
        removeStylingBtn.setDisable(false);
    }


    // Requires: Nothing.
    // Modifies: textArea.
    // Effects: Completely empties the textArea.
    public void clearTextArea() {

        fileMessage.setText("");

        if (textArea.getContent().length() != 0) {

            autoSaveText.clear();

            autoSaveText.add(textArea.getText());

            textArea.clear();
            undoBtn.setDisable(false);

        } else {
            fileMessage.setText("Text area is empty.");
        }

    }

    // Requires: A {clearTextArea} execution.
    // Modifies: textArea.
    // Effects: Undos a {clearTextArea} execution.
    public void onUndo() {

        fileMessage.setText("");

        StringBuilder stringBuilder = new StringBuilder();

        for (String str : autoSaveText) {
            stringBuilder.append(str).append("\n");
        }

        textArea.appendText(stringBuilder.toString());
        undoBtn.setDisable(true);
        fileMessage.setText("Text successfully restored.");

    }

    // Requires: Nothing.
    // Modifies: Highlighted text client wants to change font.
    // Effects: Changes the font for highlighted text.
    public void setArialFont() {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-family: Arial, sans-serif");
    }

    public void setHelveticaFont() {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-family: Helvetica, sans-serif");
    }

    public void setTimesNewRomanFont() {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-family: Times New Roman, Times, serif");
    }

    // Requires: Nothing.
    // Modifies: Highlighted text client wants to change font size.
    // Effects: Changes the selected font size for highlighted text.
    public void setFontSize10(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 10px");
    }

    public void setFontSize11(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 11px");
    }

    public void setFontSize12(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 12px");
    }

    public void setFontSize13(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 14px");
    }

    public void setFontSize14(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 16px");
    }

    public void setFontSize15(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 24px");
    }

    public void setFontSize16(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 48px");
    }

    public void setFontSize17(ActionEvent actionEvent) {
        IndexRange selection = textArea.getSelection();
        textArea.setStyle(selection.getStart(), selection.getEnd(), "-fx-font-size: 72px");
    }
}

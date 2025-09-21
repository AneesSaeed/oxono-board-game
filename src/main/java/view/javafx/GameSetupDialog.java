package view.javafx;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;

public class GameSetupDialog {

    private int rows;
    private int cols;
    private String aiLevel;
    private boolean playWithAI;
    private boolean confirmed;

    private TextField rowInput;
    private TextField colInput;
    private RadioButton humanRadio;
    private RadioButton aiRadio;
    private ComboBox<String> aiLevelCombo;
    private Button startBtn;

    public GameSetupDialog(Stage parentStage){
        Stage dialog = new Stage();
        dialog.initOwner(parentStage);
        dialog.setTitle("Game setup");

        GridPane grid = createGridPane();

        HBox rowsInputBox = createLabeledInputBox("Rows", "Enter rows. e.g(4)", true);
        HBox colsInputBox = createLabeledInputBox("Cols", "Enter columns. e.g(4)", false);
        HBox playerTypeBox = createPlayerTypeSelection();
        HBox aiLevelBox =  createAiLevelSelection();
        HBox buttonBox = createBtnBox(dialog);

        grid.add(rowsInputBox, 0, 0);
        grid.add(colsInputBox, 0, 1);
        grid.add(playerTypeBox, 0, 2);
        grid.add(aiLevelBox, 0, 3);
        grid.add(buttonBox, 0, 4, 2, 1);

        Scene scene = new Scene(grid,500,380);

        URL cssUrl = getClass().getResource("/styles/gameSetUpDialog.css");
        if (cssUrl != null){
            String cssFile = cssUrl.toExternalForm();
            scene.getStylesheets().add(cssFile);
        }else {
            showAlert("File Not Found", "Please check file path");
        }

        dialog.setScene(scene);

        scene.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.ENTER){
                startBtn.fire();
            }
        });

        dialog.showAndWait();
    }

    //Helper methods
    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private HBox createLabeledInputBox(String labelText, String promptText, boolean isRowInput) {
        Label label = new Label(labelText);
        label.getStyleClass().add("font-style");

        TextField inputField = new TextField();
        inputField.setPromptText(promptText);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (isRowInput){
            rowInput = inputField;
        }else {
            colInput = inputField;
        }
        return new HBox(label, spacer, inputField);
    }

    private HBox createPlayerTypeSelection() {
        Label playerTypeLabel = new Label("Opponent: ");
        playerTypeLabel.getStyleClass().add("font-style");

        ToggleGroup playerTypeGroup = new ToggleGroup();
        humanRadio = new RadioButton("Human");
        humanRadio.getStyleClass().add("font-style");
        humanRadio.setToggleGroup(playerTypeGroup);

        aiRadio = new RadioButton("AI");
        aiRadio.getStyleClass().add("font-style");
        aiRadio.setToggleGroup(playerTypeGroup);
        aiRadio.setSelected(true); //Default

        playerTypeGroup.selectedToggleProperty().addListener((e)->{
            aiLevelCombo.setDisable(humanRadio.isSelected());
        });

        return  new HBox(50,playerTypeLabel, humanRadio, aiRadio);
    }

    private HBox createAiLevelSelection() {
        Label aiLevelLabel = new Label("AI Level:");
        aiLevelLabel.getStyleClass().add("font-style");

        aiLevelCombo = new ComboBox<>();
        aiLevelCombo.getItems().addAll("Easy");
        aiLevelCombo.setValue("Easy");
        return new HBox(100, aiLevelLabel, aiLevelCombo);
    }


    private HBox createBtnBox(Stage dialog) {
        startBtn = new Button("Start");
        startBtn.setPadding(new Insets(10));
        startBtn.getStyleClass().add("green-background");
        startBtn.getStyleClass().add("font-style");

        Button closeBtn = new Button("Close");
        closeBtn.setPadding(new Insets(10));
        closeBtn.getStyleClass().add("red-background");
        closeBtn.getStyleClass().add("font-style");

        startBtn.setOnAction(e -> handleStart(dialog));
        closeBtn.setOnAction(e-> handleClose(dialog));


        HBox buttonBox = new HBox(50, closeBtn, startBtn);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private void handleStart(Stage dialog) {
        try{
            int inputRows = Integer.parseInt(rowInput.getText());
            int inputCols = Integer.parseInt(colInput.getText());

            if (inputRows < 4 || inputCols < 4){
                showAlert("Invalid dimensions", "Dimension should be at least 4x4");
                return;
            }
            if (inputRows % 2 != 0 || inputCols % 2 != 0){
                showAlert("Invalid dimensions", "Only even dimensions are allowed");
                return;
            }

            rows = inputRows;
            cols = inputCols;
            playWithAI = aiRadio.isSelected();
            aiLevel = playWithAI ? aiLevelCombo.getValue() : null;
            confirmed = true;
            dialog.close();
        } catch (NumberFormatException nfe) {
            showAlert("Invalid Input", "Rows and columns must be valid numbers");
        }
    }

    private void handleClose(Stage dialog) {
        confirmed = false;
        dialog.close();
    }

    private void showAlert(String title, String s) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(s);
        alert.showAndWait();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public String getAiLevel() {
        return aiLevel;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isPlayWithAI() {
        return playWithAI;
    }
}

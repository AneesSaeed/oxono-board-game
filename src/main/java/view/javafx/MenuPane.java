package view.javafx;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MenuPane extends BorderPane {

    private final Button undoBtn;
    private final Button redoBtn;
    private final Button surrenderBtn;

    public MenuPane(Runnable onRestart){
        Button restartBtn = new Button("Restart");
        undoBtn = new Button("Undo");
        redoBtn = new Button("Redo");
        surrenderBtn = new Button("Surrender");

        restartBtn.setStyle("-fx-background-color: #34ae34; -fx-text-fill: white; -fx-font-size: 25");
        undoBtn.setStyle("-fx-background-color: lightblue; -fx-font-size: 15");
        redoBtn.setStyle("-fx-background-color: lightblue; -fx-font-size: 15");
        surrenderBtn.setStyle("-fx-background-color: #c51616; -fx-text-fill: white; -fx-font-size: 25");

        HBox leftBox = new HBox(restartBtn);
        leftBox.setSpacing(10);
        leftBox.setStyle("-fx-alignment: center-left;");

        HBox centerBox = new HBox(10, undoBtn, redoBtn);
        centerBox.setStyle("-fx-alignment: center;");

        HBox rightBox = new HBox(surrenderBtn);
        rightBox.setSpacing(10);
        rightBox.setStyle("-fx-alignment: center-right;");


        restartBtn.setOnAction(e->{
            if (onRestart != null){
                onRestart.run();
            }
        });

        setLeft(leftBox);
        setCenter(centerBox);
        setRight(rightBox);
        setStyle("-fx-background-color: #643b7d;");
        setPrefHeight(60);
    }

    public void setUndoAction(Runnable onUndo){
        undoBtn.setOnAction(e ->{
            if (onUndo != null){
                onUndo.run();
            }
        });
    }

    public void setRedoAction(Runnable onRedo){
        if (onRedo != null){
            redoBtn.setOnAction(e -> onRedo.run());
        }
    }

    public void setSurrenderAction(Runnable onSurrender){
        if (onSurrender != null) {
            surrenderBtn.setOnAction(e -> onSurrender.run());
        }
    }

    public void disableUndoRedoSurrender() {
        undoBtn.setDisable(true);
        redoBtn.setDisable(true);
        surrenderBtn.setDisable(true);
    }
}

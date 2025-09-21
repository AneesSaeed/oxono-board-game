package view.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Color;

public class InfoPane extends VBox {
    private final Label emptyTilesLeftLabel;
    private final Label playerNameLabel;
    private final Label remainingXLabel;
    private final Label remainingOLabel;

    public InfoPane(String playerName, Color playerColor){
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #888181;");

        emptyTilesLeftLabel = new Label("Empty tiles: 0");
        emptyTilesLeftLabel.setStyle("-fx-text-fill: white; -fx-font-size: 21px");

        playerNameLabel = new Label(playerName);
        playerNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 21px");

        VBox topSec = new VBox(20, emptyTilesLeftLabel, playerNameLabel);
        topSec.setPrefWidth(180);
        topSec.setAlignment(Pos.CENTER);

        Region centerSpacer = new Region();
        centerSpacer.setPrefHeight(200);

        remainingXLabel = createPawnLabel("images/"+playerColor+"_X.png");
        remainingOLabel = createPawnLabel("images/"+playerColor+"_O.png");

        VBox centerSec = new VBox(50, remainingXLabel, remainingOLabel);
        centerSec.setPrefWidth(150);
        centerSec.setAlignment(Pos.CENTER);

        getChildren().addAll(topSec, centerSpacer, centerSec);
    }

    private Label createPawnLabel(String imagePath) {
        Label label = new Label("0");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 21px;");

        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        label.setGraphic(imageView);
        return label;
    }

    public void setRemainingX(String remainingX) {
        remainingXLabel.setText(remainingX);
    }

    public void setRemainingO(String remainingO) {
        remainingOLabel.setText(remainingO);
    }

    public void setEmptyTilesLeft(int emptyTiles) {
        emptyTilesLeftLabel.setText("Empty tiles: " + emptyTiles);
    }
}

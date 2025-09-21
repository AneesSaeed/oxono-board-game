package view.javafx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane {
    private final ImageView content;

    public Tile() {
        setStyle("-fx-background-color: #643b7d;");
        content = new ImageView();

        content.fitWidthProperty().bind(widthProperty().multiply(0.8));
        content.fitHeightProperty().bind(heightProperty().multiply(0.8));

        getChildren().add(content);
    }

    public void setContent(Image image) {
        content.setImage(image);
    }
}

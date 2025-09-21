package view.javafx;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import model.Game;
import model.Position;
import model.TileState;

import java.util.List;
import java.util.function.BiConsumer;

public class BoardPane extends GridPane {
    private final int rows;
    private final int cols;
    private final Tile[][] grid;

    private final Image totemXImage = loadImage("images/totem_X.png");
    private final Image totemOImage = loadImage("images/totem_O.png");
    private final Image pinkXPawnImage = loadImage("images/PINK_X.png");
    private final Image pinkOPawnImage = loadImage("images/PINK_O.png");
    private final Image blackXPawnImage = loadImage("images/BLACK_X.png");
    private final Image blackOPawnImage = loadImage("images/BLACK_O.png");

    public BoardPane(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.grid = new Tile[rows][cols];

        initializeGrid();
        configureBoardStyle();
    }

    private Image loadImage(String path){
        try {
            return new Image(path);
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Invalid image path: "+path);
            return createPlaceHolderImage();
        }
    }

    private Image createPlaceHolderImage() {
        return new Image("images/placeHolder.png");
    }

    private void initializeGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Tile tile = new Tile();
                tile.setStyle(generateTileStyle(row, col));

                tile.prefWidthProperty().bind(this.widthProperty().divide(cols));
                tile.prefHeightProperty().bind(this.heightProperty().divide(rows));

                grid[row][col] = tile;
                add(tile, col, row);
            }
        }
    }

    private void configureBoardStyle() {
        setPadding(new Insets(25));
        setStyle("-fx-border-color: #4cd4ff; -fx-border-width: 20px; -fx-background-color: #643b7d");
    }

    public void setOnTileClick(BiConsumer<Integer, Integer> onTileClick){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int finalCol = col;
                int finalRow = row;
                grid[row][col].setOnMouseClicked(e -> onTileClick.accept(finalRow, finalCol));
            }
        }
    }

    public void updateBoard(Game game){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TileState state = game.getTileState(new Position(row, col));
                Tile tile = grid[row][col];
                tile.setContent(getImageForState(state));
            }
        }
    }

    private String generateTileStyle(int row, int col) {
        StringBuilder style = new StringBuilder("-fx-background-color: #643b7d;");

        if (row != rows - 1) {
            style.append("-fx-border-width: 0 0 2px 0; -fx-border-color: #904593;");
        }
        if (col != cols - 1) {
            if (row != rows - 1) {
                style.append("-fx-border-width: 0 2px 2px 0; -fx-border-color: #904593;");
            } else {
                style.append("-fx-border-width: 0 2px 0 0; -fx-border-color: #904593 ;");
            }
        }

        return style.toString();
    }

    private Image getImageForState(TileState state) {
        return switch (state){
            case EMPTY -> null;
            case TOTEM_X -> totemXImage;
            case TOTEM_O -> totemOImage;
            case PINK_X -> pinkXPawnImage;
            case PINK_O -> pinkOPawnImage;
            case BLACK_X -> blackXPawnImage;
            case BLACK_O -> blackOPawnImage;
        };
    }

    public void highLightInvalidTile(Position pos){
        clearHighlights();
        grid[pos.x()][pos.y()].setStyle("-fx-background-color: #953838;");
    }

    public void highLightValidTiles(List<Position> positions){
        clearHighlights();
        for (Position pos : positions){
            grid[pos.x()][pos.y()].setStyle("-fx-background-color: green; -fx-border-color: white");
        }
    }

    public void clearHighlights() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col].setStyle(generateTileStyle(row, col));
            }
        }
    }

    public void disableBoardInteraction() {
        setDisable(true);
    }
}

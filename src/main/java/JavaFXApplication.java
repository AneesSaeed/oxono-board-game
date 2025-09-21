import controller.JavaFXController;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Color;
import model.Game;
import model.strategy.ComputerStrategy;
import model.strategy.RandomStrategy;
import view.javafx.*;

public class JavaFXApplication extends Application {
    private Game model;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Oxono");
        if (showGameSetupDialog(primaryStage)) {
            primaryStage.show();
        } else {
            System.out.println("Game setup canceled. Exiting application...");
            primaryStage.close();
        }
    }

    private boolean showGameSetupDialog(Stage primaryStage) {
        GameSetupDialog setupDialog = new GameSetupDialog(primaryStage);

        if (setupDialog.isConfirmed()) {
            int rows = setupDialog.getRows();
            int cols = setupDialog.getCols();
            String aiLevel = setupDialog.isPlayWithAI() ? setupDialog.getAiLevel() : null;

            setupGame(primaryStage, rows, cols, aiLevel);
            return true;
        }
        return false;
    }

    private void setupGame(Stage primaryStage, int rows, int cols, String aiLevel) {
        model = new Game(rows, cols, getStrategy(aiLevel));
        boolean isPlayingWithAI = (aiLevel != null);
        setupGameUI(primaryStage, model, isPlayingWithAI, creatRestartCallBack(primaryStage));
        model.notifyObservers();
    }

    private Runnable creatRestartCallBack(Stage primaryStage){
        return ()->{
            if (showGameSetupDialog(primaryStage)) {
                System.out.println("Game restart canceled. Continuing current game...");
            }
        };
    }

    private void setupGameUI(Stage primaryStage, Game model, boolean isPlayingWithAI, Runnable onRestart) {
        MenuPane menu = new MenuPane(onRestart);
        InfoPane pinkPlayerInfo = new InfoPane("PINK Player", Color.PINK);
        InfoPane blackPlayerInfo = isPlayingWithAI
                ? new InfoPane("AI Player", Color.BLACK)
                : new InfoPane("BLACK Player", Color.BLACK);
        BoardPane board = new BoardPane(model.getRows(), model.getCols());
        Pane boardContainer = createBoardContainer(board);
        new JavaFXController(model, menu, board, pinkPlayerInfo, blackPlayerInfo, boardContainer);

        // Build the layout
        BorderPane root = new BorderPane();
        root.setTop(menu);
        root.setCenter(boardContainer);
        root.setLeft(pinkPlayerInfo);
        root.setRight(blackPlayerInfo);

        Scene scene = new Scene(root);
        addKeyHandler(scene);
        primaryStage.setScene(scene);
        setFullScreenBounds(primaryStage);
        primaryStage.show();
    }

    private Pane createBoardContainer(BoardPane board) {
        Pane boardContainer = new Pane();

        board.prefWidthProperty().bind(boardContainer.widthProperty().multiply(0.7));
        board.prefHeightProperty().bind(boardContainer.heightProperty().multiply(0.9));
        board.layoutXProperty().bind(
                boardContainer.widthProperty()
                .subtract(board.widthProperty())
                .divide(2)
        );
        board.layoutYProperty().bind(
                boardContainer.heightProperty()
                .subtract(board.heightProperty())
                .divide(2)
        );
        boardContainer.getChildren().add(board);

        return boardContainer;
    }


    private ComputerStrategy getStrategy(String aiLevel){
        if (aiLevel != null && aiLevel.equalsIgnoreCase("easy")) {
            return new RandomStrategy();
        }
        return null;
    }

    private void addKeyHandler(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                switch (e.getCode()) {
                    case Z -> model.undo();
                    case Y -> model.redo();
                    case S -> model.surrender();
                }
            }
        });
    }

    private void setFullScreenBounds(Stage primaryStage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
    }
}


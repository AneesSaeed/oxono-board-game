package controller;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import model.*;
import model.observer.Observer;
import view.javafx.BoardPane;
import view.javafx.InfoPane;
import view.javafx.MenuPane;

public class JavaFXController implements Observer {

    private final Game model;
    private final MenuPane menu;
    private final BoardPane board;
    private final InfoPane pinkInfoPane;
    private final InfoPane blackInfoPane;
    private final Pane boardContainer;

    public JavaFXController(Game model, MenuPane menu, BoardPane board, InfoPane pinkInfoPane, InfoPane blackInfoPane, Pane boardContainer) {
        this.model = model;
        this.menu = menu;
        this.board = board;
        this.pinkInfoPane = pinkInfoPane;
        this.blackInfoPane = blackInfoPane;
        this.boardContainer = boardContainer;
        this.model.addObserver(this);

        initializeMenuActions();
        initializeBoardInteraction();
    }

    @Override
    public void update() {
        board.updateBoard(model);
        updatePlayersInfo();
        updateBoardBackground();

        if (model.isGameOver()){
            displayGameOverAlert();
        }
    }

    private void initializeMenuActions() {
        menu.setUndoAction(model::undo);
        menu.setRedoAction(model::redo);
        menu.setSurrenderAction(()->{
            model.surrender();
            displayGameOverAlert();
        });
    }

    private void initializeBoardInteraction() {
        board.setOnTileClick((row, col)-> handleTileClick(new Position(row, col)));
    }

    private void handleTileClick(Position clickedPos) {
        try {
            if (model.isGameOver()){
                return;
            }

            TileState tileState = model.getTileState(clickedPos);
            if (model.isHasMovedTotem()){
                processPawnInsertion(clickedPos);
                if (model.isAiTurn() && !model.isGameOver()){
                    scheduleAiTurn();
                }
            } else {
                processTotemSelectionOrMovement(tileState, clickedPos);
            }
        } catch (OxonoException e) {
            board.highLightInvalidTile(clickedPos);
            displayError(e.getMessage());
        }
    }

    private void scheduleAiTurn() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> handleAiTurn());
        pause.play();
    }

    private void handleAiTurn() {
        try {
            model.executeAITurn();
            model.setChosenTotem(null);
        } catch (OxonoException e) {
            displayError("Ai turn error : "+e.getMessage());
        }
    }

    private void processPawnInsertion(Position clickedPos) {
        Symbol symbol = model.getChosenTotemSymbol();
        Pawn pawn = model.getPawnOfSymbol(symbol);
        model.insertPawn(pawn, clickedPos);
        board.clearHighlights();
        model.setChosenTotem(null);
    }

    private void processTotemSelectionOrMovement(TileState tileState, Position clickedPos) {
        board.clearHighlights();
        if (tileState == TileState.TOTEM_X || tileState == TileState.TOTEM_O) {
            selectTotem(tileState, clickedPos);
        } else if (model.getChosenTotemSymbol() != null) {
            moveSelectedTotem(clickedPos);
        } else {
            throw new OxonoException("Please select a totem before making a move");
        }
    }

    private void selectTotem(TileState tileState, Position clickedPos) {
        Symbol symbol = tileState == TileState.TOTEM_X ? Symbol.X : Symbol.O;
        model.setChosenTotem(symbol);
        board.highLightValidTiles(model.getValidMoves(clickedPos));
    }

    private void moveSelectedTotem(Position clickedPos) {
        Totem chosenTotem = model.getTotemOfSymbol(model.getChosenTotemSymbol());
        model.moveTotem(chosenTotem, clickedPos);
        board.highLightValidTiles(model.getValidPawnInsertions());
    }

    private void updatePlayersInfo() {
        updatePinkInfo();
        updateBlackInfo();
    }

    private void updateBlackInfo() {
        blackInfoPane.setRemainingX(model.remainingXForPlayer(Color.BLACK));
        blackInfoPane.setRemainingO(model.remainingOForPlayer(Color.BLACK));
        blackInfoPane.setEmptyTilesLeft(model.getEmptyTileCount());
    }

    private void updatePinkInfo() {
        pinkInfoPane.setRemainingX(model.remainingXForPlayer(Color.PINK));
        pinkInfoPane.setRemainingO(model.remainingOForPlayer(Color.PINK));
        pinkInfoPane.setEmptyTilesLeft(model.getEmptyTileCount());
    }

    private void updateBoardBackground() {
        boardContainer.setStyle("-fx-background-color:" + getPlayerColor() + ";");
    }

    private void displayGameOverAlert(){
        Color winnerColor = model.getWinnerColor();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Game over");
        if (winnerColor != null) {
            alert.setContentText("winner: " + winnerColor);
        } else {
            alert.setContentText("Its a draw");
        }
        board.disableBoardInteraction();
        menu.disableUndoRedoSurrender();
        alert.show();
    }

    private void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Action");
        alert.setHeaderText("Game Rule Violation");
        alert.setContentText(message);
        alert.show();
    }

    public String getPlayerColor() {
        Color currPlayerColor = model.getCurrPlayerColor();
        return switch (currPlayerColor) {
            case PINK -> "pink";
            case BLACK -> "#373232";
        };
    }
}
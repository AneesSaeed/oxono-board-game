package controller;

import model.*;
import model.observer.Observer;
import view.ConsoleView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleController implements Observer {
    private static final Pattern MOVE_COMMAND_PATTERN = Pattern.compile("^move\\s+([xo])\\s+(\\d+)$");
    private static final Pattern INSERT_COMMAND_PATTERN = Pattern.compile("^insert\\s+(\\d+)$");

    private final Game model;
    private final ConsoleView view;


    public ConsoleController(Game model, ConsoleView view) {
        this.model = model;
        this.view = view;
        this.model.addObserver(this);
    }

    @Override
    public void update() {
        updateView();
    }

    /**
     * Starts the main game loop.
     * Displays the game title, updates the view, and processes user input
     * until the game ends.
     * Exceptions are caught to ensure the user is informed of any invalid moves
     * without breaking the game loop.
     */
    public void start() {
        while (!model.isGameOver()) {
            try {
                if (model.isAiTurn()){
                    handleAITurn();
                }else {
                    handleHumanTurn();
                }
            } catch (OxonoException e) {
                view.showError(e.getMessage());
            }
        }
        displayEnd();
    }

    /**
     * Handles the Ai player turn
     */
    private void handleAITurn(){
        view.showMessage("AI is thinking...");
        model.executeAITurn();
        view.showMessage("AI completed its turn...");
    }

    /**
     * Handles the human player turn
     */
    private void handleHumanTurn(){
            String input = view.getUserInput();
            handleInput(input);
    }

    /**
     * Handles user input for human players
     *
     * @param input The user input.
     */
    private void handleInput(String input) {
        // undo, redo, or surrender
        if (processSpecialInput(input)) {
            return;
        }
        //moveTotem or insertPawnCommand
        parseInput(input);
    }

    /**
     * Processes special commands like undo, redo, and surrender.
     *
     * @param input The user input.
     * @return true if a special input was handled; false otherwise.
     */
    private boolean processSpecialInput(String input) {
        switch (input.toLowerCase()) {
            case "undo":
                model.undo();
                view.showMessage("Undo executed.");
                return true;
            case "redo":
                model.redo();
                view.showMessage("Redo executed.");
                return true;
            case "surrender":
                model.surrender();
                view.showMessage("Player surrendered.");
                return true;
            default:
                return false;
        }
    }

    /**
     * Parses the user input and returns the corresponding command.
     *
     * @param input The user input.
     * @return A command object
     */
    private void parseInput(String input) {
        Matcher moveMatcher = MOVE_COMMAND_PATTERN.matcher(input);
        if (moveMatcher.matches()) {
            moveTotem(moveMatcher);
            return;
        }

        Matcher insertMatcher = INSERT_COMMAND_PATTERN.matcher(input);
        if (insertMatcher.matches()) {
            insertPawn(insertMatcher);
            return;
        }
        throw new OxonoException("Invalid command.");
    }

    /**
     * Creates a MoveTotemCommand from the user input.
     *
     * @param matcher The regex matcher containing the parsed input.
     * @return A MoveTotemCommand object.
     */
    private void moveTotem(Matcher matcher) {
        Symbol symbol = parseSymbol(matcher.group(1));
        int tileNum = Integer.parseInt(matcher.group(2));
        Position totemDestPos = model.tileNumberToPos(tileNum);

        model.setChosenTotem(symbol);
        Totem totem = model.getTotemOfSymbol(model.getChosenTotemSymbol());
        model.moveTotem(totem, totemDestPos);
    }

    private Symbol parseSymbol(String symbolString) {
        return symbolString.equals("x") ? Symbol.X : Symbol.O;
    }

    /**
     * Creates an InsertPawnCommand from the user input.
     *
     * @param matcher The regex matcher containing the parsed input.
     * @return An InsertPawnCommand object.
     */
    private void insertPawn(Matcher matcher) {
        int tileNum = Integer.parseInt(matcher.group(1));
        Position position = model.tileNumberToPos(tileNum);
        Symbol chosenTotemSymbol = model.getLastMovedTotSym();
        Pawn pawn = model.getPawnOfSymbol(chosenTotemSymbol);
        model.insertPawn(pawn, position);
    }

    /**
     * Displays the end-game screen.
     */
    private void displayEnd() {
        System.out.println("\n🎉✨ GAME OVER! ✨🎉");
        Color winnerColor = model.getWinnerColor();
        if ( winnerColor != null) {
            view.showMessage("--- Winner: " + winnerColor + " ---");
        } else {
            view.showMessage("🤝  It's a draw!  🤝");
        }
        view.showMessage("🌟 Come back soon! 🌟");
    }

    /**
     * Updates the board and player status in the view.
     */
    private void updateView() {
        view.displayBoard(model);
        view.displayPlayerStatus(model);
    }
}

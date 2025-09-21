package model;

import model.command.CommandManager;
import model.observer.Observable;
import model.observer.Observer;
import model.strategy.ComputerStrategy;

import java.util.ArrayList;
import java.util.List;


/**
 * Facade class for managing the game's flow and interactions between subsystems.
 */
public class Game implements Observable {

    private final List<Observer> observers;

    //Players
    private final Player black;
    private final  Player pink;
    private Player currPlayer;
    private Player winner;
    private final ComputerStrategy computerStrategy;

    //Game state
    private final Board board;
    private final GameValidator validator;
    private final CommandManager commandManager;

    private Symbol lastMovedTotSym;
    private Position lastMovedTotPos;
    private boolean hasMovedTotem;
    private Position lastInPawnPos;

    /**
     * Initializes the game with a board of specified dimensions.
     *
     * @param rows Number of rows for the game board.
     * @param cols Number of columns for the game board.
     */
    public Game(int rows, int cols, ComputerStrategy computerStrategy){
        this.observers = new ArrayList<>();
        this.black = new Player(Color.BLACK);
        this.pink = new Player(Color.PINK);
        this.currPlayer = pink;
        this.winner = null;
        this.board = new Board(rows, cols);
        this.validator = new GameValidator(board);
        this.commandManager = new CommandManager();
        this.lastMovedTotSym = null;
        this.hasMovedTotem = false;
        this.lastInPawnPos = null;
        this.computerStrategy = computerStrategy;
    }

    // -------------------------------
    // Game State and Flow Management
    // -------------------------------

    /**
     * Checks if the game is over, either by a win or draw condition.
     *
     * @return True if the game is over, otherwise false.
     */
    public boolean isGameOver(){
        return ( winner != null || validator.checkDrawCondition(pink, black));
    }

    /**
     * Gets the winner of the game, if any.
     *
     * @return The winning {@code Player}, or {@code null} if the game is not yet won.
     */
    public Color getWinnerColor() {
        if (winner == null){
            return null;
        }
        return winner.getColor();
    }

    /**
     * Gets the current player whose turn it is.
     *
     * @return The current {@code Player}.
     */
    public Color getCurrPlayerColor() {
        return currPlayer.getColor();
    }

    /**
     * check if current player is Ai-player
     *
     * @return true if current player is Ai-player
     */
    public boolean isAiTurn(){
        if (computerStrategy != null){
            return currPlayer == black;
        }
        return false;
    }

    /**
     * Moves to the next player's turn.
     */
    void changePlayer(){
        currPlayer = (currPlayer == black) ? pink : black;
    }

    /**
     * Moves to the next player's turn.
     */
    private void nextTurn(){
        changePlayer();
    }

    /**
     * Handles the surrender action, declaring the opponent as the winner.
     */
    public void surrender(){
        winner = (currPlayer == black) ? pink : black;
        stateChanged();
    }

    // -------------------------
    // Totem Movement Management
    // -------------------------
    /**
     * Moves a totem to a new position on the board.
     *
     * @param totem  The totem to be moved.
     * @param newPos The new position for the totem.
     * @throws OxonoException if the game's rules are violated
     */
    public void moveTotem(Totem totem, Position newPos){
        checkSymbolAvailability();
        Position totemCurrPos = getTotemPos(totem);
        if (hasMovedTotem) {
            throw new OxonoException("Cannot move a totem twice.");
        }
        if (!isValidTotemMove(totemCurrPos, newPos)) {
            throw new OxonoException("Invalid totem move.");
        }
        Position oldPos = getTotemPos(totem);
        moveInBoard(totem, newPos);
        commandManager.addCommand(new MoveTotemCommand(this, totem, newPos ,oldPos));
    }

    void moveInBoard(Totem totem, Position newPos){
        board.moveTotem(totem, newPos);
        lastMovedTotSym = totem.getSymbol();
        lastMovedTotPos = newPos;
        hasMovedTotem = true;
        stateChanged();
    }

    /**
     * Validates whether a totem move is valid based on the game rules.
     *
     * @param currPos The current position of the totem.
     * @param newPos  The target position for the totem.
     * @return {@code true} if the move is valid, otherwise {@code false}.
     */
    boolean isValidTotemMove(Position currPos, Position newPos){
        return validator.isValidTotemMove(currPos, newPos);
    }

    /**
     * Resets the "totem moved" state for the current turn.
     *
     * @param state {@code true} to indicate a totem has been moved, otherwise {@code false}.
     */
    void setTotemMoved(boolean state) {
        hasMovedTotem = state;
    }

    public boolean isHasMovedTotem() {
        return hasMovedTotem;
    }

    /**
     * Gets the position of the specified totem.
     *
     * @param totem The {@code Totem} to locate.
     * @return The {@code Position} of the specified totem on the board.
     */
    public Position getTotemPos(Totem totem){
        return board.getTotemPos(totem);
    }

    /**
     * Gets the position of the specified totem.
     *
     * @param symbol The Totem {@code Symbol} to locate.
     * @return The {@code Position} of the specified totem on the board.
     */
    public Position getTotemPos(Symbol symbol){
        return board.getTotemPos(symbol);
    }

    /**
     * Retrieves the symbol of the last moved totem.
     *
     * @return The {@code Symbol} of the last moved totem.
     */
    public Symbol getLastMovedTotSym() {
        return lastMovedTotSym;
    }

    public Totem getTotemOfSymbol(Symbol symbol){
        return new Totem(symbol);
    }


    // ------------------------
    // Pawn Management
    // ------------------------
    /**
     * Inserts a pawn at the specified position on the board.
     *
     * @param pawn The {@code Pawn} to insert.
     * @param newPos  The {@code Position} where the pawn will be placed.
     * @throws OxonoException if the game's rules are violated
     */
    public void insertPawn(Pawn pawn, Position newPos){
        validatePawnInsertion(newPos);
        insertPawnInBoard(pawn, newPos);
        commandManager.addCommand(new InsertPawnCommand(this, pawn, newPos));
    }

    void insertPawnInBoard(Pawn pawn, Position pos){
        board.insertPawn(pawn, pos);
        this.lastInPawnPos = pos;

        if (validator.checkWinCondition(lastInPawnPos)){
            winner = currPlayer;
        } else {
            currPlayer.decreasePawn(pawn);
        }
        setTotemMoved(false); // Set hasMovedTotem to false. to enforce moving a totem before inserting another pawn.
        nextTurn();
        stateChanged();
    }

    /**
     * Validates whether a pawn can be inserted at a given position.
     *
     * @param pos The {@code Position} to validate.
     * @throws OxonoException if the move is invalid or a totem has not been moved yet.
     */
    void validatePawnInsertion(Position pos) {
        if (!hasMovedTotem) {
            throw new OxonoException("You must move a totem before inserting a pawn.");
        }
        validator.canInsertPawn(lastMovedTotPos, pos);
    }

    public Pawn getPawnOfSymbol(Symbol s){
        return new Pawn(s, currPlayer.getColor());
    }

    // ---------------------------
    // Undo and Redo Functionality
    // ---------------------------

    /**
     * Undoes the last command.
     */
    public void undo(){
        commandManager.undo();
    }

    /**
     * Redoes the last undone command.
     */
    public void redo(){
        commandManager.redo();
    }

    /**
     * execute the AI turn
     */
    public void executeAITurn(){
        computerStrategy.playTurn(this);
    }
    // -------------------------------
    // Console View Helper Methods
    // -------------------------------
    /**
     * Converts a tile number into a {@code Position}.
     *
     * @param tileNum The number of the tile (1-based index).
     * @return The corresponding {@code Position} on the board.
     */
    public Position tileNumberToPos(int tileNum) {
        int cols = board.getCols();

        int row = (tileNum - 1) / cols;
        int col = (tileNum - 1) % cols;

        return new Position(row, col);
    }

    // -------------------------------
    // Token Management
    // -------------------------------
    /**
     * Removes the token at the specified position.
     *
     * @param newPos The {@code Position} of the token to remove.
     */
    void removeToken(Position newPos){
        board.getTile(newPos).removeToken();
        stateChanged();
    }

    // -------------------------------
    // Validation
    // -------------------------------
    /**
     * Checks whether the current player can use their chosen symbol for a move.
     *
     * @throws OxonoException if:
     * <ul>
     *   <li>The current player does not have enough pawns of the chosen symbol.</li>
     *   <li>The current player has not chosen a symbol.</li>
     * </ul>
     */
    void checkSymbolAvailability(){
        validator.checkSymbolAvailability(currPlayer);
    }


    // -------------------------------
    // Board and Tile Information
    // -------------------------------
    /**
     * Determines if a tile is empty.
     *
     * @param position The {@code Position} of the tile to check.
     * @return {@code true} if the tile is empty, otherwise {@code false}.
     */
    public boolean isTileEmpty(Position position) {
        return board.getTile(position).getToken() == null;
    }

    /**
     * Gets the symbol of the token at the specified position.
     *
     * @param position The {@code Position} of the tile to check.
     * @return The symbol of the token, or an empty string if no token is present.
     */
    public String getTileSymbol(Position position) {
        Token token = board.getTile(position).getToken();
        return (token != null) ? token.getSymbol().toString() : ""; // Return token symbol or empty string
    }

    /**
     * Gets the color of the tile at the specified position.
     *
     * @param position The {@code Position} of the tile.
     * @return The color of the tile.
     */
    public String getTileColor(Position position) {
        return board.getTile(position).getColor(); // Tile colors are immutable strings
    }

    public int getEmptyTileCount(){
        return validator.getNumberOfEmptyTiles();
    }

    public int getRows(){
        return board.getRows();
    }

    public int getCols(){
        return board.getCols();
    }

    // -------------------------------
    // Player Management
    // -------------------------------
    /**
     * Restores a pawn to the current player.
     */
    void restorePawnToCurrentPlayer(Pawn pawn) {
        currPlayer.increasePawn(pawn);
    }

    /**
     * Gets the number of "X" pawns remaining for the current player.
     *
     * @return The remaining number of "X" pawns as a string.
     */
    public String remainingX() {
        return String.valueOf(currPlayer.getRemainingX());
    }

    /**
     * Gets the number of "O" pawns remaining for the current player.
     *
     * @return The remaining number of "O" pawns as a string.
     */
    public String remainingO() {
        return String.valueOf(currPlayer.getRemainingO());
    }

    public String remainingXForPlayer(Color playerColor){
        return switch (playerColor) {
            case PINK -> String.valueOf(pink.getRemainingX());
            case BLACK -> String.valueOf(black.getRemainingX());
        };
    }

    public String remainingOForPlayer(Color playerColor){
        return switch (playerColor) {
            case PINK -> String.valueOf(pink.getRemainingO());
            case BLACK -> String.valueOf(black.getRemainingO());
        };
    }
    // -------------------------------
    // Controller Helper
    // -------------------------------
    /**
     * Sets the chosen totem for the current player.
     *
     * @param symbol The symbol (as a string) of the chosen totem.
     */
    public void setChosenTotem(Symbol symbol){
        if (getValidMoves(getTotemPos(symbol)).isEmpty()) {
            throw new OxonoException("Please choose another totem no valid moves for this totem");
        }
        currPlayer.setChosenTotem(symbol);
    }


    /**
     * Gets the symbol of the current player's chosen totem.
     *
     * @return The {@code Symbol} of the chosen totem.
     */
    public Symbol getChosenTotemSymbol(){
        return currPlayer.getChosenTotemSym();
    }

    // -------------------------------
    // Tile Highlighter Functionality
    // -------------------------------

    /**
     * Retrieves all valid moves for a totem.
     *
     * @param totemPos The {@code Position} of the totem.
     * @return A {@code Set} of valid positions for the token to move to.
     */
    public List<Position> getValidMoves(Position totemPos) {
        return validator.getValidMoves(totemPos);
    }

    /**
     * Retrieves all valid positions where a pawn can be inserted.
     *
     * @return A {@code Set} of valid positions for pawn insertion.
     */
    public List<Position> getValidPawnInsertions() {
        return validator.getValidPawnInsertions(lastMovedTotPos);
    }



    // -------------------------------
    // Observable methods
    // -------------------------------

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.  forEach(Observer::update);
    }

    /**
     * Marks that the state of this observable object has changed and notifies all registered observers.
     * This is a convenience method that internally calls {@code notifyObservers}.
     */
    private void stateChanged(){
        notifyObservers();
    }

    // -------------------------------
    // methods for JavaFx
    // -------------------------------
    public TileState getTileState(Position pos){
        Tile tile = board.getTile(pos);
        return tile.getState();
    }



    // -------------------------------
    // Helper methods for testing
    // -------------------------------
    void resetPawnsCountsToZeroForTest(){
        resetPawnXCountToZeroForTest();
        resetPawnOCountToZeroForTest();
    }

    void resetPawnXCountToZeroForTest(){
        black.setXtoZero();
        pink.setXtoZero();
    }

    void resetPawnOCountToZeroForTest(){
        black.setOToZero();
        pink.setOToZero();
    }
}

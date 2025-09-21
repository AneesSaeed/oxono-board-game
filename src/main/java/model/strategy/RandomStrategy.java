package model.strategy;

import model.*;

import java.util.List;
import java.util.Random;

/**
 * RandomStrategy represents a simple AI strategy that makes decisions at random.
 * The AI:
 * - Chooses a symbol ('X' or 'O') randomly.
 * - Moves a totem to a random valid position.
 * - Inserts a pawn at a random valid position.
 */
public class RandomStrategy implements ComputerStrategy{
    private final Random random = new Random();


    /**
     * Executes the AI's turn with random decisions.
     * - Selects a random symbol ('X' or 'O').
     * - Moves a totem to a random valid position.
     * - Places a pawn at a random valid position.
     *
     * @param model The model facade for interacting with the game state.
     */
    @Override
    public void playTurn(Game model) {
        Symbol chosenSymbol = chooseSymbolIfNeeded(model);
        moveTotemIfNeeded(model, chosenSymbol);
        insertPawnAtRandomValidPosition(model, chosenSymbol);
    }

    /**
     * Chooses a random symbol ('X' or 'O') if a totem has not been moved.
     *
     * @param model The game model.
     * @return The chosen Symbol.
     */
    private Symbol chooseSymbolIfNeeded(Game model) {
        if (!model.isHasMovedTotem()) {
            Symbol chosenSymbol = chooseRandomSymbol();
            System.out.println("AI chose the symbol: " + chosenSymbol);
            try {
                model.setChosenTotem(chosenSymbol);
            }catch (OxonoException e){
                chosenSymbol = switchToAlternativeTotem(chosenSymbol);
                model.setChosenTotem(chosenSymbol);
            }
            return chosenSymbol;
        }
        return model.getChosenTotemSymbol();
    }

    /**
     * Moves a totem to a random valid position if needed
     * @param model The Model facade
     * @param chosenSymbol The symbol of the totem to move
     */
    private void moveTotemIfNeeded(Game model, Symbol chosenSymbol) {
        if (!model.isHasMovedTotem()) {
            Totem totem = model.getTotemOfSymbol(chosenSymbol);
            Position totemPos = model.getTotemPos(totem);
            List<Position> validMoves = model.getValidMoves(totemPos);
            if (!validMoves.isEmpty()){
                Position totemDest = validMoves.get(random.nextInt(validMoves.size()));
                model.moveTotem(totem, totemDest);
                pause(1500);
            }
        }
    }

    /**
     * Insert a pawn at a random valid position
     *
     * @param model The Model facade
     * @param chosenSymbol The symbol of the pawn to insert
     */
    private void insertPawnAtRandomValidPosition(Game model, Symbol chosenSymbol) {
        List<Position> validPawnInsPos = model.getValidPawnInsertions();
        if (!validPawnInsPos.isEmpty()){
            Pawn pawn = model.getPawnOfSymbol(chosenSymbol);
            Position pawnDest = validPawnInsPos.get(random.nextInt(validPawnInsPos.size()));
            model.insertPawn(pawn, pawnDest);
        }
    }

    /**
     * Chooses a random symbol ('X' or 'O').
     *
     * @return The chosen Symbol.
     */
    private Symbol chooseRandomSymbol() {
        return random.nextBoolean() ? Symbol.X : Symbol.O;
    }


    private Symbol switchToAlternativeTotem(Symbol currSymbol) {
        Symbol alternativeSymbol = currSymbol == Symbol.X ? Symbol.O : Symbol.X;
        System.out.println("Ai switched to " + alternativeSymbol);
        return alternativeSymbol;
    }

    /**
     * Pauses execution for a specified duration.
     *
     * @param milliseconds The pause duration in milliseconds.
     */
    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("AI turn was interrupted", e);
        }
    }
}

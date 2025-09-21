package model;

import model.strategy.RandomStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(6, 6, new RandomStrategy());
    }

    @Test
    void shouldReturnTrueWhenWinnerIsSet() {
        game.surrender();
        boolean gameIsOver = game.isGameOver();
        assertTrue(gameIsOver,"Game should end when there is a winner");
    }

    @Test
    void shouldReturnTrueWhenDrawConditionIsMet() {
        game.resetPawnsCountsToZeroForTest();
        boolean gameIsOver = game.isGameOver();
        assertTrue(gameIsOver,"Game should end in case of a draw");
    }

    @Test
    void shouldReturnFalseWhenNoWinnerAndNoDraw() {
        boolean gameIsOver = game.isGameOver();
        assertFalse(gameIsOver, "Game should not be over if there is no winner or draw condition.");
    }

    @Test
    void shouldEndGameWhenFourPawnsAreAligned() {
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position currentTotemPos = game.getTotemPos(totemX);

        Position[] totemPositions = {
                new Position(currentTotemPos.x(), currentTotemPos.y() - 1), // First move
                new Position(currentTotemPos.x(), currentTotemPos.y()), // Second move
                new Position(currentTotemPos.x(), currentTotemPos.y() + 1), // Third move
                new Position(currentTotemPos.x(), currentTotemPos.y() + 2)  // Fourth move
        };

        Position[] pawnPositions = {
                new Position(totemPositions[0].x()-1, totemPositions[0].y()), // Adjacent(top) to first move
                new Position(totemPositions[1].x()-1, totemPositions[1].y()), // Adjacent(top) to second move
                new Position(totemPositions[2].x()-1, totemPositions[2].y()), // Adjacent(top) to third move
                new Position(totemPositions[3].x()-1, totemPositions[3].y())  // Adjacent(top) to fourth move
        };

        Pawn[] pawns = {
                new Pawn(Symbol.X, Color.BLACK),
                new Pawn(Symbol.X, Color.BLACK),
                new Pawn(Symbol.X, Color.BLACK),
                new Pawn(Symbol.X, Color.BLACK)
        };

        for (int i = 0; i < 4; i++) {
            game.setChosenTotem(Symbol.X); // Choose the totem before moving
            game.moveTotem(totemX, totemPositions[i]);
            game.insertPawn(pawns[i], pawnPositions[i]);
        }

        boolean gameIsOver = game.isGameOver();
        assertTrue(gameIsOver, "Game should end when four pawns are aligned in a row.");
        assertEquals(Color.BLACK,game.getWinnerColor(),"Pink starts the game so fourth turn have to be Black");
    }

    @Test
    void shouldEndGameWhenFourPawnsAreAlignedByColor() {
        // Arrange: Initialize Totem and positions
        Totem totemO = game.getTotemOfSymbol(Symbol.O);
        Totem totemX = game.getTotemOfSymbol(Symbol.X); // Add totem for X since we alternate
        Position currentTotemPosO = game.getTotemPos(totemO);
        Position currentTotemPosX = game.getTotemPos(totemX);

        Position[] totemPositionsO = {
                new Position(currentTotemPosO.x() + 1, currentTotemPosO.y()),     // First move
                new Position(currentTotemPosO.x() + 1, currentTotemPosO.y() + 1)  // Third move
        };

        Position[] totemPositionsX = {
                new Position(currentTotemPosX.x() + 2, currentTotemPosX.y()), // Second move
                new Position(currentTotemPosX.x() + 2, currentTotemPosX.y() - 1)  // Fourth move
        };

        Position[] pawnPositions = {
                new Position(totemPositionsO[0].x() - 1, totemPositionsO[0].y()), // Above first move (O)
                new Position(totemPositionsX[0].x() - 1, totemPositionsX[0].y()), // Above second move (X)
                new Position(totemPositionsO[1].x() - 1, totemPositionsO[1].y()), // Above third move (O)
                new Position(totemPositionsX[1].x() - 1, totemPositionsX[1].y())  // Above fourth move (X)
        };

        Pawn[] pawns = {
                new Pawn(Symbol.O, Color.PINK),
                new Pawn(Symbol.X, Color.PINK),
                new Pawn(Symbol.O, Color.PINK),
                new Pawn(Symbol.O, Color.PINK)
        };

        game.setChosenTotem(Symbol.O);
        game.moveTotem(totemO, totemPositionsO[0]);
        game.insertPawn(pawns[0], pawnPositions[0]);

        game.setChosenTotem(Symbol.X);
        game.moveTotem(totemX, totemPositionsX[0]);
        game.insertPawn(pawns[1], pawnPositions[1]);

        game.setChosenTotem(Symbol.O);
        game.moveTotem(totemO, totemPositionsO[1]);
        game.insertPawn(pawns[2], pawnPositions[2]);

        game.setChosenTotem(Symbol.X);
        game.moveTotem(totemX, totemPositionsX[1]);
        game.insertPawn(pawns[3], pawnPositions[3]);

        boolean gameIsOver = game.isGameOver();
        assertTrue(gameIsOver, "Game should end when four pawns are aligned by color.");
    }




    @Test
    void blackShouldBeWinnerIfPinkSurrenders() {
        game.surrender();
        Color winnerColor = game.getWinnerColor();
        assertEquals(Color.BLACK, winnerColor, "Winner should be Black. because by PINK starts the game");
    }

    @Test
    void shouldReturnNullWhenNoWinner() {
        Color winnerColor = game.getWinnerColor();
        assertNull(winnerColor, "winner color should be null when game is not won by anyone");
    }

    // ----------------------
    // Totem Movement Testing
    // ----------------------
    @Test
    void shouldThrowInvalidMoveExceptionIfNoTotemIsChosen() {
        Totem totemO = game.getTotemOfSymbol(Symbol.O);
        Position totemOPos = game.getTotemPos(totemO);
        Position totemNewPos = new Position(totemOPos.x(), totemOPos.y() - 2);

        OxonoException exception = assertThrows(
                OxonoException.class,
                () -> game.moveTotem(totemO, totemNewPos)
        );

        assertEquals("No totem chosen.", exception.getMessage());
    }

    @Test
    void shouldReturnSymbolXIfChosenTotemIsX(){
        game.setChosenTotem(Symbol.X);
        Symbol symbol = game.getChosenTotemSymbol();
        assertEquals(Symbol.X,symbol);
    }

    @Test
    void shouldThrowInvalidMoveExceptionIfTotemIsMovedDiagonally() {
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);
        game.setChosenTotem(Symbol.X);
        Position totemNewPos = new Position(totemXPos.x() - 1, totemXPos.y() - 1);

        OxonoException exception = assertThrows(
                OxonoException.class,
                () -> game.moveTotem(totemX, totemNewPos)
        );

        assertEquals(
                "Totem can only move in a straight line horizontally or vertically.",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowInvalidMoveExceptionIfTotemIsMovedOutsideBoard(){
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);
        game.setChosenTotem(Symbol.X);
        Position totemNewPos = new Position(totemXPos.x()-50, totemXPos.y());



        OxonoException exception = assertThrows(
                OxonoException.class,
                ()->game.moveTotem(totemX, totemNewPos));
        assertEquals(exception.getMessage(),"Invalid destination position: The position must be inside the board.");
    }

    @Test
    void shouldThrowInvalidMoveExceptionIfTotemIsMovedAboveOccupiedTile(){
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);
        game.setChosenTotem(Symbol.X);
        Position totemNewPos = new Position(totemXPos.x()+1, totemXPos.y());
        game.moveTotem(totemX,totemNewPos);

        Pawn pawn = new Pawn(Symbol.X,Color.BLACK);
        game.insertPawn(pawn, new Position(totemNewPos.x()+1,totemNewPos.y()));

        Totem totemO = game.getTotemOfSymbol(Symbol.O);
        Position totemOPos = game.getTotemPos(totemO);
        game.setChosenTotem(Symbol.O);

        OxonoException exception = assertThrows(
                OxonoException.class, ()->
                game.moveTotem(totemO, new Position(totemOPos.x(), totemOPos.y()-2)));
        assertEquals(exception.getMessage(),"Cannot pass above a tile already occupied by a token.");
    }

    @Test
    void shouldThrowInvalidMoveExceptionIfTotemIsMovedTwice() {
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);
        game.setChosenTotem(Symbol.X);
        Position totemXNewPos = new Position(totemXPos.x() - 1, totemXPos.y());
        game.moveTotem(totemX, totemXNewPos);

        Totem totemO = game.getTotemOfSymbol(Symbol.O);
        Position totemOPos = game.getTotemPos(totemO);
        game.setChosenTotem(Symbol.O);
        Position totemONewPos = new Position(totemOPos.x(), totemOPos.y() + 1);

        OxonoException exception = assertThrows(
                OxonoException.class,
                () -> game.moveTotem(totemO, totemONewPos)
        );

        assertEquals("Cannot move a totem twice.", exception.getMessage());
    }


    @Test
    void shouldThrowExceptionWhenMovingTotemWithoutEnoughPawns() {
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);

        game.resetPawnXCountToZeroForTest();
        game.setChosenTotem(Symbol.X);

        Position targetPosition = new Position(totemXPos.x() - 1, totemXPos.y() - 1);

        OxonoException exception = assertThrows(
                OxonoException.class,
                () -> game.moveTotem(totemX, targetPosition)
        );

        assertEquals("Move not possible you don't have enough matching pawn", exception.getMessage());
    }



    // ----------------------
    // Pawn Insertion Testing
    // ----------------------

    @Test
    void shouldThrowInvalidMoveExceptionIfTryToInsertInAnOccupiedTile() {
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);
        game.setChosenTotem(Symbol.X);
        Position totemNewPos = new Position(totemXPos.x() + 1, totemXPos.y());
        game.moveTotem(totemX, totemNewPos);

        Pawn pawn = new Pawn(Symbol.X, Color.BLACK);
        Position occupiedPosition = new Position(totemNewPos.x(), totemNewPos.y());

        OxonoException exception = assertThrows(
                OxonoException.class,
                () -> game.insertPawn(pawn, occupiedPosition)
        );

        assertEquals("Invalid destination position: The tile must be empty.", exception.getMessage());
    }


    @Test
    void shouldThrowInvalidMoveExceptionIfTryToInsertOtherThanAdjacent() {
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);
        game.setChosenTotem(Symbol.X);
        Position totemNewPos = new Position(totemXPos.x() + 1, totemXPos.y());
        game.moveTotem(totemX, totemNewPos);

        Pawn pawn = new Pawn(Symbol.X, Color.BLACK);
        Position nonAdjacentPosition = new Position(totemNewPos.x(), totemNewPos.y() - 2);

        OxonoException exception = assertThrows(
                OxonoException.class,
                () -> game.insertPawn(pawn, nonAdjacentPosition)
        );

        assertEquals("The specified position is not adjacent to the totem.", exception.getMessage());
    }


    @Test
    void doesNotThrowInvalidMoveExceptionIfInsertionIsAdjacentToTotem() {
        Totem totemX = game.getTotemOfSymbol(Symbol.X);
        Position totemXPos = game.getTotemPos(totemX);
        game.setChosenTotem(Symbol.X);
        Position totemNewPos = new Position(totemXPos.x(), totemXPos.y() - 1);
        game.moveTotem(totemX, totemNewPos);

        Position[] directions = {
                new Position(totemNewPos.x() - 1, totemNewPos.y()), // Up
                new Position(totemNewPos.x() + 1, totemNewPos.y()), // Down
                new Position(totemNewPos.x(), totemNewPos.y() - 1), // Left
                new Position(totemNewPos.x(), totemNewPos.y() + 1)  // Right
        };
        Position insertPos = directions[new Random().nextInt(4)];
        Pawn pawn = new Pawn(Symbol.X, Color.BLACK);

        assertDoesNotThrow(() -> game.insertPawn(pawn, insertPos));
    }
}
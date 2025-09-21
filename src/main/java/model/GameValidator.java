package model;

import java.util.ArrayList;
import java.util.List;

class GameValidator {

    private final Board board;
    private final int rows;
    private final int cols;

    public GameValidator(Board board) {
        this.board = board;
        this.rows = board.getRows();
        this.cols = board.getCols();
    }

    /**
     * Validates whether moving a totem from the current position to the new position is allowed.
     *
     * <p>This method performs several checks:
     * <ul>
     *   <li>Ensures both positions (`curr` and `newPos`) are valid and inside the board boundaries.</li>
     *   <li>Validates that the current position contains a totem.</li>
     *   <li>Checks that the destination tile is empty.</li>
     *   <li>Handles movement rules for enclaved and non-enclaved totems.</li>
     * </ul>
     *
     * @param curr the current position of the totem
     * @param newPos the destination position for the totem
     * @return {@code true} if the move is valid; otherwise, throws an exception
     * @throws OxonoException if any of the validation checks fail
     */
    public boolean isValidTotemMove(Position curr, Position newPos) {
        validatePosition(curr, "current");
        validatePosition(newPos, "destination");

        Token token = board.getTile(curr).getToken();
        validateToken(token, curr);
        validateDestination(newPos);

        if (isTotemEnclaved(curr)) {
            validateEnclavedMove(curr, newPos);
        } else {
            validatePathClear(curr, newPos);
        }
        return true;
    }

    /**
     * Determines if a pawn can be inserted at the specified position relative to a totem.
     *
     * <p>This method checks the following:
     * <ul>
     *   <li>Ensures the totem's position (`totemPos`) is valid.</li>
     *   <li>If the totem is enclaved, the pawn can be placed anywhere but it have to be inside the board and the tile have to be empty</li>
     *   <li>If the totem is not enclaved, the pawn must be placed on an adjacent tile to the totem, but not diagonally</li>
     * </ul>
     *
     * @param totemPos the position of the totem
     * @param pawnPos the position where the pawn is to be inserted
     * @throws OxonoException if the totem position is invalid
     */
    public void canInsertPawn(Position totemPos, Position pawnPos) {
        validatePosition(totemPos, "totem");
        validatePosition(pawnPos, "pawn");
        validateDestination(pawnPos);
        if (isTotemEnclaved(totemPos)) {
            return;
        }
        if (!getInBoundsNeighbors(totemPos).contains(pawnPos)){
            throw new OxonoException("The specified position is not adjacent to the totem.");
        }
    }

    /**
     * Checks whether the game has reached a winning condition based on the last inserted pawn.
     *
     * <p>The method validates the following:
     * <ul>
     *   <li>The provided position (`lastInPawn`) is valid and within the board boundaries.</li>
     *   <li>Determines if there is a winning alignment of four pawns in a row, either vertically or horizontally.</li>
     * </ul>
     *
     * @param lastInPawn the position of the last inserted pawn
     * @return {@code true} if a winning condition is met; {@code false} otherwise
     * @throws OxonoException if the provided position is invalid
     */
    public boolean checkWinCondition(Position lastInPawn) {
        validatePosition(lastInPawn, "last inserted pawn");

        int x = lastInPawn.x();
        int y = lastInPawn.y();

        return checkAlignment(x, 0, 0, 1) || //Horizontally
               checkAlignment(0, y, 1, 0);   //Vertically
    }

    /**
     * Checks whether the game has reached a draw condition.
     *
     * <p>A draw condition is met if both players have no remaining pawns to place
     * (i.e., all "O" and "X" pawns are used up for both players).
     *
     * @param p1 the first player
     * @param p2 the second player
     * @return {@code true} if the game is in a draw state; {@code false} otherwise
     * @throws OxonoException if either of the player objects is {@code null}
     */
    public boolean checkDrawCondition(Player p1, Player p2) {
        return (getNumberOfEmptyTiles() == 0) || noValidTotemMoveLeft() ||
               (p1.getRemainingO() + p1.getRemainingX() == 0 &&
                p2.getRemainingO() + p2.getRemainingX() == 0);
    }

    private boolean noValidTotemMoveLeft(){
        return getValidMoves(board.getTotemPos(Symbol.X)).isEmpty() &&
               getValidMoves(board.getTotemPos(Symbol.O)).isEmpty();
    }

    /**
     * Validates the availability of the chosen Totem symbol for the given player.
     *
     * @param player the player whose symbol availability is being checked.
     * @throws OxonoException if no symbol is chosen or if the player has insufficient pawns of the chosen type.
     */
    public void checkSymbolAvailability(Player player) {
        Symbol chosenSymbol = player.getChosenTotemSym();
        if (chosenSymbol == null) {
            throw new OxonoException("No totem chosen.");
        }
         if ((chosenSymbol == Symbol.O && player.getRemainingO() <= 0) ||
                 (chosenSymbol == Symbol.X && player.getRemainingX() <= 0)){
             throw new OxonoException("Move not possible you don't have enough matching pawn");
         }
    }

    private void validatePosition(Position pos, String posType) {
        if (pos == null) {
            throw new OxonoException("The " + posType + " position cannot be null.");
        }
        if (!isInBoard(pos)) {
            throw new OxonoException("Invalid " + posType + " position: The position must be inside the board.");
        }
    }

    private void validateToken(Token token, Position curr) {
        if (token == null) {
            throw new OxonoException("The tile at position " + curr + " is empty. No token is present.");
        }
        if (!(token instanceof Totem)) {
            throw new OxonoException("The token at position " + curr + " is not a totem.");
        }
    }

    private void validateDestination(Position newPos) {
        if (!isTileEmpty(newPos)) {
            throw new OxonoException("Invalid destination position: The tile must be empty.");
        }
    }

    private void validateEnclavedMove(Position curr, Position newPos) {
        if (!getFirstEmptyAdjacentPositions(curr).contains(newPos)) {
            throw new OxonoException("Invalid move: Enclaved totems can only move to the first adjacent empty position.");
        }
    }

    private void validatePathClear(Position curr, Position newPos) {
        if (!isPathClear(curr, newPos)) {
            throw new OxonoException("Cannot pass above a tile already occupied by a token.");
        }
    }

    /**
     * Determines whether a totem at the specified position is enclaved.
     *
     * <p>A totem is considered enclaved if all its valid neighboring tiles are occupied
     * (i.e., there are no empty tiles adjacent to the totem).
     *
     * @param totemPos the position of the totem to check
     * @return {@code true} if the totem is enclaved; {@code false} otherwise
     * @throws OxonoException if the position is invalid or out of bounds
     */
    public boolean isTotemEnclaved(Position totemPos) {
        List<Position> inBoundsNeighbors = getInBoundsNeighbors(totemPos);
        for (Position neighborPosition : inBoundsNeighbors) {
            if (isTileEmpty(neighborPosition)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks whether there is an alignment of four consecutive pawns, based on either matching symbols or colors,
     * starting from the specified position and moving in a specific direction.
     *
     * @param startX the starting x-coordinate on the board
     * @param startY the starting y-coordinate on the board
     * @param dx the horizontal step direction (1 = right, -1 = left, 0 = no movement)
     * @param dy the vertical step direction (1 = down, -1 = up, 0 = no movement)
     * @return {@code true} if four consecutive pawns are aligned based on valid pairs; {@code false} otherwise
     */
    private boolean checkAlignment(int startX, int startY, int dx, int dy) {
        int alignBySymbolCount = 0;
        int alignByColorCount = 0;

        Symbol initialSymbol = null;
        Color initialColor = null;

        for (int step = 0; step < Math.max(rows, cols); step++) {
            int x = startX + step * dx;
            int y = startY + step * dy;

            // If out of bounds, reset the alignment counters
            if (x < 0 || x >= rows || y < 0 || y >= cols) {
                alignBySymbolCount = 0;
                alignByColorCount = 0;
                initialSymbol = null;
                initialColor = null;
                continue;
            }

            Position pos = new Position(x, y);

            // Check if the tile contains a Pawn
            if (!isTileEmpty(pos) && board.getTile(pos).getToken() instanceof Pawn pawn) {
                // Symbol alignment
                if (initialSymbol == null || pawn.getSymbol() == initialSymbol) {
                    initialSymbol = pawn.getSymbol();
                    alignBySymbolCount++;
                } else {
                    initialSymbol = pawn.getSymbol();
                    alignBySymbolCount = 1;
                }

                // Color alignment
                if (initialColor == null || pawn.getColor() == initialColor) {
                    initialColor = pawn.getColor();
                    alignByColorCount++;
                } else {
                    initialColor = pawn.getColor();
                    alignByColorCount = 1;
                }

                // If either alignment reaches 4 means someone win
                if (alignBySymbolCount >= 4 || alignByColorCount >= 4) {
                    return true;
                }
            } else {
                // Reset counters if the tile is empty or invalid
                alignBySymbolCount = 0;
                alignByColorCount = 0;
                initialSymbol = null;
                initialColor = null;
            }
        }
        return false;
    }

    private boolean isPathClear(Position oldPos, Position newPos) {
        boolean isHorizontal = oldPos.x() == newPos.x();
        boolean isVertical = oldPos.y() == newPos.y();

        if (isHorizontal) {
            int start = Math.min(oldPos.y(), newPos.y()) + 1;
            int end = Math.max(oldPos.y(), newPos.y());

            for (int i = start; i < end; i++) {
                if (!isTileEmpty(new Position(oldPos.x(), i))) {
                    return false;
                }
            }
        } else if (isVertical) {
            int start = Math.min(oldPos.x(), newPos.x()) + 1;
            int end = Math.max(oldPos.x(), newPos.x());

            for (int i = start; i < end; i++) {
                if (!isTileEmpty(new Position(i, oldPos.y()))) {
                    return false;
                }
            }
        } else {
            throw new OxonoException("Totem can only move in a straight line horizontally or vertically.");
        }
        return true;
    }

    private boolean isInBoard(Position pos) {
        return pos.x() >= 0 && pos.x() < rows && pos.y() >= 0 && pos.y() < cols;
    }

    private boolean isTileEmpty(Position pos) {
        return board.getTile(pos).isEmpty();
    }

    private List<Position> getInBoundsNeighbors(Position pos) {
        List<Position> inBoundsNeighbors = new ArrayList<>();
        int x = pos.x();
        int y = pos.y();

        Position up = new Position(x - 1, y);
        Position right = new Position(x, y + 1);
        Position down = new Position(x + 1, y);
        Position left = new Position(x, y - 1);

        if (isInBoard(up)) inBoundsNeighbors.add(up);
        if (isInBoard(right)) inBoundsNeighbors.add(right);
        if (isInBoard(down)) inBoundsNeighbors.add(down);
        if (isInBoard(left)) inBoundsNeighbors.add(left);

        return inBoundsNeighbors;
    }

    /**
     * Finds the first empty positions adjacent to a given position in all four directions.
     *
     * <p>The method checks each direction (up, down, left, right) and returns the first empty position
     * found in each direction, if any.
     *
     * @param pos the starting position
     * @return a list of the first empty positions adjacent to the given position
     */
    private List<Position> getFirstEmptyAdjacentPositions(Position pos) {
        List<Position> adjacentEmptyPositions = new ArrayList<>();
        int x = pos.x();
        int y = pos.y();

        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, 1},  // right
                {0, -1}  // left
        };

        for (int[] dir : directions) {
            int currX = x;
            int currY = y;

            while (true) {
                currX += dir[0];
                currY += dir[1];

                Position currPos = new Position(currX, currY);
                if (!isInBoard(currPos)) break;
                if (isTileEmpty(currPos)) {
                    adjacentEmptyPositions.add(currPos);
                    break;
                }
            }
        }
        return adjacentEmptyPositions;
    }

    /**
     * Returns the valid moves for a Totem at the given position.
     * If the Totem is enclaved, valid positions are adjacent empty positions. Otherwise,
     * calculates valid moves in straight lines until encountering a boundary or non-empty tile.
     *
     * @param totemPos the current position of the Totem.
     * @return a list of valid positions for the Totem to move to.
     */
    public List<Position> getValidMoves(Position totemPos) {
        List<Position> validMoves = new ArrayList<>();

        if (isTotemEnclaved(totemPos)) {
            validMoves.addAll(getFirstEmptyAdjacentPositions(totemPos));
        }else{
            List<Position> directions = getInBoundsNeighbors(totemPos);
            for (Position direction : directions){
                int deltaX = direction.x() - totemPos.x();
                int deltaY = direction.y() - totemPos.y();

                Position curr = direction;
                while (isInBoard(curr) && isTileEmpty(curr)){
                    validMoves.add(curr);
                    curr = new Position(curr.x() + deltaX, curr.y() + deltaY);
                }
            }
        }
        return validMoves;
    }

    /**
     * Returns a list of valid positions for inserting a pawn based on the last moved Totem's position.
     * If the Totem is enclaved, all empty positions are valid. Otherwise, only empty neighboring positions are valid.
     *
     * @param lastMovedTotPos the position of the last moved Totem.
     * @return a list of valid positions for pawn insertion.
     */
    public List<Position> getValidPawnInsertions(Position lastMovedTotPos){
        List<Position> validInsertions = new ArrayList<>();

        if (isTotemEnclaved(lastMovedTotPos)) {
            validInsertions.addAll(getAllEmptyPos());
        }else {
            validInsertions.addAll(getEmptyNeighbors(lastMovedTotPos));
        }
        return validInsertions;
    }

    private List<Position> getAllEmptyPos(){
        List<Position> emptyPositions = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position position = new Position(i, j);
                if (isTileEmpty(position)) {
                    emptyPositions.add(position);
                }
            }
        }
        return emptyPositions;
    }

    /**
     * Count the number of empty tiles
     * @return empty tiles count
     */
    public int getNumberOfEmptyTiles(){
        return getAllEmptyPos().size();
    }

    private List<Position> getEmptyNeighbors(Position pos) {
        List<Position> emptyNeighbors = new ArrayList<>();
        List<Position> inBoundsNeighbors = getInBoundsNeighbors(pos);

        for (Position neighbor : inBoundsNeighbors) {
            if (isTileEmpty(neighbor)) {
                emptyNeighbors.add(neighbor);
            }
        }
        return emptyNeighbors;
    }
}
